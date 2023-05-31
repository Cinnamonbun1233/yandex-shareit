package ru.practicum.shareit.booking.service;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.status.BookingStatus;
import ru.practicum.shareit.booking.status.State;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static ru.practicum.shareit.booking.model.QBooking.booking;

//DONE!!!
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Transactional
    public BookingResponseDto createNewBooking(BookingRequestDto bookingRequestDto, Long userId) {
        Item item = itemRepository.findById(bookingRequestDto.getItemId()).orElseThrow(() ->
                new ItemNotFoundException("Предмет с id: '" + bookingRequestDto.getItemId() + "' не найден"));

        if (!item.getAvailable()) {
            throw new ItemNotAvailableException(String.format("Предмет с id: '" + item.getId() + "' недоступен для брони"));
        }

        User user = userRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundException("Пользователь с id: '" + userId + "' не найден"));

        if (item.getOwner().equals(user)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Бронь для владельца предмета недоступна");
        }

        Booking booking = BookingMapper.bookingRequestDtoToBooking(bookingRequestDto, item, user);

        return BookingMapper.bookingToBookingResponseDto(bookingRepository.save(booking));
    }

    public List<BookingResponseDto> getAllUserBookings(State state, Long userId, Boolean owner) {
        List<Predicate> predicateList = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        if (owner) {
            predicateList.add(booking.item.owner.id.eq(userId));
        } else {
            predicateList.add(booking.booker.id.eq(userId));
        }

        stateSwitcher(state, predicateList, now);

        List<BookingResponseDto> bookingResponseDtoList = BookingMapper.bookingsToBookingResponseDtoList(
                bookingRepository.findAll(Objects.requireNonNull(ExpressionUtils.allOf(predicateList)),
                        Sort.by(Sort.Direction.DESC, "startDate")));

        if (bookingResponseDtoList.isEmpty()) {
            throw new BookingNotFoundException("Пользователь с id: '" + userId + "' не имеет брони");
        }

        return bookingResponseDtoList;
    }

    public BookingResponseDto getBookingById(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findBooking(bookingId, userId).orElseThrow(()
                -> new BookingNotFoundException("Бронь с id: '" + bookingId + "' не обнаружена"));
        return BookingMapper.bookingToBookingResponseDto(booking);
    }

    @Transactional
    public BookingResponseDto approveBooking(Long bookingId, Boolean approved, Long ownerId) {
        Booking booking = bookingRepository.findBookingByOwner(bookingId, ownerId).orElseThrow(()
                -> new BookingNotFoundException("Бронь с id: '" + bookingId + "' для пользователя с id: '"
                + ownerId + "' не обнаружена"));
        checkAlreadyApproved(booking);

        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }

        return BookingMapper.bookingToBookingResponseDto(bookingRepository.save(booking));
    }

    private void checkAlreadyApproved(Booking booking) {
        BookingStatus status = booking.getStatus();
        if (booking.getStatus() != null && (status == BookingStatus.APPROVED || status == BookingStatus.REJECTED)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Невозможно изменить статус аренды после подтверждения");
        }
    }

    private static void stateSwitcher(State state, List<Predicate> predicateList, LocalDateTime now) {
        switch (state) {
            case ALL:
                break;
            case FUTURE:
                predicateList.add(booking.startDate.after(now));
                break;
            case PAST:
                predicateList.add(booking.endDate.before(now));
                break;
            case CURRENT:
                predicateList.add(booking.startDate.loe(now).and(booking.endDate.gt(now)));
                break;
            case REJECTED:
                predicateList.add(booking.status.eq(BookingStatus.REJECTED));
                break;
            case WAITING:
                predicateList.add(booking.status.eq(BookingStatus.WAITING));
                break;
            default:
                throw new UnknownStateException(State.UNSUPPORTED_STATUS.name());
        }
    }
}