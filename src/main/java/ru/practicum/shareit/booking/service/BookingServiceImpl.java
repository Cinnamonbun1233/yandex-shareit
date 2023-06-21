package ru.practicum.shareit.booking.service;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.GetBookingRequest;
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

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public BookingResponseDto createNewBooking(BookingRequestDto bookingRequestDto, Long userId) {
        Item item = itemRepository.findById(bookingRequestDto.getItemId())
                .orElseThrow(() -> new ItemNotFoundException(String.format("Предмет с id: '%s' не найден",
                        bookingRequestDto.getItemId())));

        if (!item.getAvailable()) {
            throw new ItemNotAvailableException(String.format("Предмет с id: '%s' недоступен для бронирования", item.getId()));
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format("Пользователь с id: '%s' не найден", userId)));

        if (item.getOwner().equals(user)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Бронирование для владельца предмета недоступно");
        }

        Booking booking = BookingMapper.bookingRequestDtoToBooking(bookingRequestDto, item, user);
        return BookingMapper.bookingToBookingResponseDto(bookingRepository.save(booking));
    }

    public BookingResponseDto getBookingById(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findBooking(bookingId, userId)
                .orElseThrow(() -> new BookingNotFoundException(String.format("Бронирование с id: '%s' не найдено", bookingId)));
        return BookingMapper.bookingToBookingResponseDto(booking);
    }

    @Transactional
    public BookingResponseDto approveBooking(Long bookingId, Boolean approved, Long ownerId) {
        Booking booking = bookingRepository.findBookingByOwner(bookingId, ownerId)
                .orElseThrow(() -> new BookingNotFoundException(String.format(
                        "Бронирование с id: '%s' для владельца предмета с id: '%s' не найдено", bookingId, ownerId)));
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
                    "Невозможно изменить статус аренды предмета после подтверждения бронирования");
        }
    }

    public List<BookingResponseDto> getAllUserBookings(GetBookingRequest getBookingRequest) {
        List<Predicate> predicates = new ArrayList<>();
        LocalDateTime curTime = LocalDateTime.now();

        if (getBookingRequest.isOwner()) {
            predicates.add(booking.item.owner.id.eq(getBookingRequest.getUserId()));
        } else {
            predicates.add(booking.booker.id.eq(getBookingRequest.getUserId()));
        }

        stateSwitcher(getBookingRequest, predicates, curTime);

        Pageable page = PageRequest.of(getBookingRequest.getFrom(), getBookingRequest.getSize(),
                Sort.by(Sort.Direction.DESC, "startDate"));
        List<BookingResponseDto> dtos = BookingMapper.bookingsToBookingResponseDtoList(
                bookingRepository.findAll(Objects.requireNonNull(ExpressionUtils.allOf(predicates)), page));

        if (dtos.isEmpty())
            throw new BookingNotFoundException(String.format(
                    "Пользователь с id: '%s' не имеет бронирования", getBookingRequest.getUserId()));

        return dtos;
    }

    private static void stateSwitcher(GetBookingRequest getBookingRequest, List<Predicate> predicates, LocalDateTime curTime) {
        switch (getBookingRequest.getState()) {
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
    }
}