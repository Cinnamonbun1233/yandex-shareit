package ru.practicum.shareit.booking.service;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.State;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.utils.BookingMapper;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static ru.practicum.shareit.booking.model.QBooking.booking;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Transactional
    public BookingResponseDto addBooking(BookingRequestDto dto, Long userId) {
        Item item = itemRepository.findById(dto.getItemId()).orElseThrow(() ->
                new ItemNotFoundException(String.format("Вещь с id: %s не обнаружена", dto.getItemId())));

        if (!item.getAvailable()) {
            throw new ItemNotAvailableException(String.format("Вещь с id: %s недоступна для брони", item.getId()));
        }
        User user = userRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundException(String.format("Пользователь с id: %s не обнаружен", userId)));
        if (item.getOwner().equals(user)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Бронь для владельца вещи недоступна");
        }
        Booking booking = BookingMapper.dtoToBooking(dto, item, user);

        return BookingMapper.toResponseDto(bookingRepository.save(booking));
    }

    public BookingResponseDto getBookingById(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findBooking(bookingId, userId)
                .orElseThrow(() -> new BookingNotFoundException(String.format("Бронь с id: %s не обнаружена", bookingId)));
        return BookingMapper.toResponseDto(booking);
    }

    @Transactional
    public BookingResponseDto approveBooking(Long bookingId, Boolean approved, Long ownerId) {
        Booking booking = bookingRepository.findBookingByOwner(bookingId, ownerId)
                .orElseThrow(() -> new BookingNotFoundException(
                        String.format("Бронь с id: %s для владельца с id: %s не обнаружена", bookingId, ownerId)));
        checkAlreadyApproved(booking);
        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        return BookingMapper.toResponseDto(bookingRepository.save(booking));
    }

    private void checkAlreadyApproved(Booking booking) {
        BookingStatus status = booking.getStatus();
        if (booking.getStatus() != null && (status == BookingStatus.APPROVED || status == BookingStatus.REJECTED)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Невозможно изменить статус аренды после подтверждения");
        }
    }

    public List<BookingResponseDto> getAllUserBookings(State state, Long userId, boolean owner) {
        List<Predicate> predicates = new ArrayList<>();
        LocalDateTime curTime = LocalDateTime.now();
        if (owner) {
            predicates.add(booking.item.owner.id.eq(userId));
        } else {
            predicates.add(booking.booker.id.eq(userId));
        }
        switch (state) {
            case ALL:
                break;
            case FUTURE:
                predicates.add(booking.startDate.after(curTime));
                break;
            case PAST:
                predicates.add(booking.endDate.before(curTime));
                break;
            case CURRENT:
                predicates.add(booking.startDate.loe(curTime)
                        .and(booking.endDate.gt(curTime)));
                break;
            case REJECTED:
                predicates.add(booking.status.eq(BookingStatus.REJECTED));
                break;
            case WAITING:
                predicates.add(booking.status.eq(BookingStatus.WAITING));
                break;
            default:
                throw new UnknownStateException(State.UNSUPPORTED_STATUS.name());
        }
        List<BookingResponseDto> dtos = BookingMapper.toResponseDto(
                bookingRepository.findAll(ExpressionUtils.allOf(predicates),
                        Sort.by(Sort.Direction.DESC, "startDate")));

        if (dtos.isEmpty())
            throw new BookingNotFoundException(String.format("Пользователь с id : %s не имеет брони", userId));

        return dtos;
    }
}
