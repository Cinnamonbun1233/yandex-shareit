package ru.practicum.shareit.booking.service;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.GetBookingRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.status.BookingStatus;
import ru.practicum.shareit.booking.status.State;
import ru.practicum.shareit.booking.validation.BookingMapper;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.dto.ItemShortResponseDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserShortResponseDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.booking.model.QBooking.booking;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private BookingServiceImpl bookingService;
    @Captor
    private ArgumentCaptor<Predicate> predicateArgumentCaptor;

    private static BookingRequestDto getBookingRequestDto() {
        return BookingRequestDto.builder()
                .id(1L)
                .status(BookingStatus.WAITING)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(10))
                .itemId(1L)
                .build();
    }

    private static Item getItem(User owner, boolean available) {
        return Item.builder()
                .id(1L)
                .name("Грабли")
                .description("Грабли для уборки листвы")
                .owner(owner)
                .available(available)
                .build();
    }

    private static User getUser(long id, String mail) {
        return User.builder()
                .id(id)
                .name("Дима")
                .email(mail)
                .build();
    }

    private static Booking getBooking(User user, Item item) {
        return Booking.builder()
                .id(1L)
                .status(BookingStatus.WAITING)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(10))
                .booker(user)
                .item(item)
                .build();
    }

    @Test
    void addBookingShouldThrowItemNotFoundException() {
        BookingRequestDto bookingRequestDto = getBookingRequestDto();

        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        ItemNotFoundException exception = assertThrows(ItemNotFoundException.class, ()
                -> bookingService.createNewBooking(bookingRequestDto, 1L));
        assertThat(exception.getMessage(), containsString("Вещь с id: 1 не обнаружена"));
    }

    @Test
    void addBookingShouldThrowItemNotAvailableException() {
        Item item = getItem(null, false);

        BookingRequestDto bookingRequestDto = getBookingRequestDto();
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        ItemNotAvailableException exception = assertThrows(ItemNotAvailableException.class, ()
                -> bookingService.createNewBooking(bookingRequestDto, 1L));
        assertThat(exception.getMessage(), containsString("Вещь с id: 1 недоступна для брони"));
    }

    @Test
    void addBookingShouldThrowUserNotFound() {
        User user = getUser(1L, "dima@yandex.ru");
        Item item = getItem(user, true);
        BookingRequestDto bookingRequestDto = getBookingRequestDto();

        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, ()
                -> bookingService.createNewBooking(bookingRequestDto, 1L));
        assertThat(exception.getMessage(), containsString("Пользователь с id: 1 не обнаружен"));
    }

    @Test
    void addBookingShouldThrowResponseStatusException() {
        User user = getUser(1L, "dima@yandex.ru");

        Item item = getItem(user, true);

        BookingRequestDto bookingRequestDto = getBookingRequestDto();
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, ()
                -> bookingService.createNewBooking(bookingRequestDto, 1L));
        assertThat(exception.getMessage(), containsString("Бронь для владельца вещи недоступна"));
    }

    @Test
    void addBookingShouldReturnBookingResponseDto() {
        User user = getUser(1L, "dima@yandex.ru");

        User owner = getUser(2L, "fima@yandex.ru");

        Item item = getItem(owner, true);
        BookingRequestDto bookingRequestDto = getBookingRequestDto();
        Booking booking = BookingMapper.bookingRequestDtoToBooking(bookingRequestDto, item, user);

        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(bookingRepository.save(any()))
                .thenReturn(booking);

        BookingResponseDto result = bookingService.createNewBooking(bookingRequestDto, 1L);

        assertThat(result.getStatus(), equalTo(BookingStatus.WAITING));
        assertThat(result, instanceOf(BookingResponseDto.class));
        assertThat(result.getItem(), instanceOf(ItemShortResponseDto.class));
        assertThat(result.getBooker(), instanceOf(UserShortResponseDto.class));

        verify(itemRepository, times(1)).findById(anyLong());
        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).save(any());

        verifyNoMoreInteractions(itemRepository, userRepository, bookingRepository);
    }

    @Test
    void getBookingByIdShouldThrowBookingNotFoundEx() {
        when(bookingRepository.findBooking(anyLong(), anyLong()))
                .thenThrow(new BookingNotFoundException("Бронь с id: 1 не обнаружена"));

        BookingNotFoundException exception = assertThrows(BookingNotFoundException.class, ()
                -> bookingService.getBookingByUserId(1L, 1L));
        assertThat(exception.getMessage(), containsString("Бронь с id: 1 не обнаружена"));
        assertThat(exception, instanceOf(BookingNotFoundException.class));
    }

    @Test
    void getBookingByIdShouldReturnBookingResponseDto() {
        User user = getUser(1L, "fima@yandex.ru");
        Item item = getItem(user, true);
        Booking booking = getBooking(user, item);

        when(bookingRepository.findBooking(anyLong(), anyLong()))
                .thenReturn(Optional.of(booking));
        BookingResponseDto result = bookingService.getBookingByUserId(1L, 1L);

        assertThat(result.getId(), equalTo(booking.getId()));
        assertThat(result, notNullValue());
        assertThat(result, instanceOf(BookingResponseDto.class));
        assertThat(result.getBooker().getName(), equalTo(user.getName()));
    }

    @Test
    void approveBookingShouldThrowBookingNotFoundEx() {
        when(bookingRepository.findBookingByOwner(anyLong(), anyLong()))
                .thenReturn(Optional.empty());
        BookingNotFoundException bookingNotFoundException = assertThrows(BookingNotFoundException.class, ()
                -> bookingService.approveBooking(1L, true, 1L));
        assertThat(bookingNotFoundException.getMessage(),
                containsString("Бронь с id: 1 для владельца с id: 1 не обнаружена"));
        assertThat(bookingNotFoundException, instanceOf(BookingNotFoundException.class));
    }

    @Test
    void approveBookingShouldThrowResponseStatusEx() {
        Booking booking = Booking.builder()
                .id(1L)
                .status(BookingStatus.APPROVED)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(10))
                .build();

        when(bookingRepository.findBookingByOwner(anyLong(), anyLong()))
                .thenReturn(Optional.of(booking));
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, ()
                -> bookingService.approveBooking(1L, true, 1L));
        assertThat(exception.getMessage(),
                containsString("Невозможно изменить статус аренды после подтверждения"));
        assertThat(exception, instanceOf(ResponseStatusException.class));
    }

    @Test
    void approveBookingShouldChangeStatusToRejected() {
        User user = getUser(1L, "fima@yandex.ru");
        Item item = getItem(null, true);
        Booking booking = getBooking(user, item);

        when(bookingRepository.findBookingByOwner(anyLong(), anyLong()))
                .thenReturn(Optional.of(booking));
        when(bookingRepository.save(any()))
                .thenReturn(booking);

        BookingResponseDto rejected = bookingService.approveBooking(1L, false, 1L);
        assertThat(rejected.getStatus(), equalTo(BookingStatus.REJECTED));
        booking.setStatus(BookingStatus.WAITING);
        BookingResponseDto approved = bookingService.approveBooking(1L, true, 1L);
        assertThat(approved.getStatus(), equalTo(BookingStatus.APPROVED));
    }

    @Test
    void approveBookingShouldChangeStatusToApproved() {
        User user = getUser(1L, "fima@yandex.ru");
        Item item = getItem(null, true);
        Booking booking = getBooking(user, item);

        when(bookingRepository.findBookingByOwner(anyLong(), anyLong()))
                .thenReturn(Optional.of(booking));
        when(bookingRepository.save(any()))
                .thenReturn(booking);
        BookingResponseDto approved = bookingService.approveBooking(1L, true, 1L);
        assertThat(approved.getStatus(), equalTo(BookingStatus.APPROVED));
    }

    @Test
    void getAllUserBookingsShouldThrowBookingNotFoundEx() {
        List<Predicate> predicates = new ArrayList<>();
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "startDate"));
        predicates.add(booking.item.owner.id.eq(1L));

        when(bookingRepository.findAll(Objects.requireNonNull(ExpressionUtils.allOf(predicates)), pageRequest))
                .thenReturn(Page.empty());
        GetBookingRequest getBookingRequest = GetBookingRequest.of(State.ALL, 1L, true);

        BookingNotFoundException bookingNotFoundException = assertThrows(BookingNotFoundException.class, ()
                -> bookingService.getAllUserBookings(getBookingRequest, pageRequest));
        assertThat(bookingNotFoundException.getMessage(), containsString("Пользователь с id : 1 не имеет брони"));
        assertThat(bookingNotFoundException, instanceOf(BookingNotFoundException.class));
    }

    @Test
    void getAllUserBookingsShouldReturnBookingsRejected() {
        List<Predicate> predicates = new ArrayList<>();
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "startDate"));
        predicates.add(booking.booker.id.eq(1L));
        predicates.add(booking.status.eq(BookingStatus.REJECTED));
        User user = getUser(1L, "fima@yandex.ru");
        Item item = getItem(null, true);
        Booking booking = getBooking(user, item);
        booking.setStatus(BookingStatus.REJECTED);

        Page<Booking> bookings = new PageImpl<>(List.of(booking));

        when(bookingRepository.findAll(Objects.requireNonNull(ExpressionUtils.allOf(predicates)), pageRequest))
                .thenReturn(bookings);
        GetBookingRequest getBookingRequest = GetBookingRequest.of(State.REJECTED, 1L, false);

        List<BookingResponseDto> result = bookingService.getAllUserBookings(getBookingRequest, pageRequest);
        assertDoesNotThrow(() -> bookingService.getAllUserBookings(getBookingRequest, pageRequest));
        assertThat(result, not(empty()));
        assertThat(result, hasItem(allOf(
                hasProperty("id", equalTo(booking.getId())),
                hasProperty("booker", instanceOf(UserShortResponseDto.class))
        )));
    }

    @Test
    void getAllUserBookingsShouldReturnBookingsWAITING() {
        List<Predicate> predicates = new ArrayList<>();
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "startDate"));
        predicates.add(booking.booker.id.eq(1L));
        predicates.add(booking.status.eq(BookingStatus.WAITING));
        User user = getUser(1L, "fima@yandex.ru");
        Item item = getItem(null, true);
        Booking booking = getBooking(user, item);

        Page<Booking> bookings = new PageImpl<>(List.of(booking));

        when(bookingRepository.findAll(Objects.requireNonNull(ExpressionUtils.allOf(predicates)), pageRequest))
                .thenReturn(bookings);
        GetBookingRequest getBookingRequest = GetBookingRequest.of(State.WAITING, 1L, false);
        List<BookingResponseDto> result = bookingService.getAllUserBookings(getBookingRequest, pageRequest);

        assertDoesNotThrow(() -> bookingService.getAllUserBookings(getBookingRequest, pageRequest));
        assertThat(result, not(empty()));
        assertThat(result, hasItem(allOf(
                hasProperty("id", equalTo(booking.getId())),
                hasProperty("booker", instanceOf(UserShortResponseDto.class))
        )));
    }

    @Test
    void getAllUserBookingsShouldContainFuturePredicate() {
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "startDate"));
        User user = getUser(1L, "fima@yandex.ru");
        Item item = getItem(null, true);
        Booking booking = getBooking(user, item);

        Page<Booking> bookings = new PageImpl<>(List.of(booking));
        GetBookingRequest getBookingRequest = GetBookingRequest.of(State.FUTURE, 1L, false);

        when(bookingRepository.findAll(predicateArgumentCaptor.capture(), eq(pageRequest)))
                .thenReturn(bookings);
        List<BookingResponseDto> result = bookingService.getAllUserBookings(getBookingRequest, pageRequest);

        Predicate value = predicateArgumentCaptor.getValue();
        assertThat(value, notNullValue());
        assertThat(value.toString(), containsStringIgnoringCase(
                String.format("booking.booker.id = %s && booking.startDate >", booking.getId())));
    }

    @Test
    void getAllUserBookingsShouldContainCurrentPredicate() {
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "startDate"));
        User user = getUser(1L, "fima@yandex.ru");
        Item item = getItem(null, true);
        Booking booking = getBooking(user, item);

        Page<Booking> bookings = new PageImpl<>(List.of(booking));
        GetBookingRequest request = GetBookingRequest.of(State.CURRENT, 1L, false);

        when(bookingRepository.findAll(predicateArgumentCaptor.capture(), eq(pageRequest)))
                .thenReturn(bookings);
        List<BookingResponseDto> result = bookingService.getAllUserBookings(request, pageRequest);

        Predicate value = predicateArgumentCaptor.getValue();
        assertThat(value, notNullValue());
        assertThat(value.toString(), allOf(
                containsStringIgnoringCase(
                        String.format("booking.booker.id = %s && booking.startDate <=", booking.getId())),
                containsStringIgnoringCase("booking.endDate >")
        ));
    }

    @Test
    void getAllUserBookingsShouldContainPastPredicate() {
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "startDate"));
        User user = getUser(1L, "fima@yandex.ru");
        Item item = getItem(null, true);
        Booking booking = getBooking(user, item);

        Page<Booking> bookings = new PageImpl<>(List.of(booking));
        GetBookingRequest request = GetBookingRequest.of(State.PAST, 1L, false);

        when(bookingRepository.findAll(predicateArgumentCaptor.capture(), eq(pageRequest)))
                .thenReturn(bookings);
        List<BookingResponseDto> result = bookingService.getAllUserBookings(request, pageRequest);

        Predicate value = predicateArgumentCaptor.getValue();
        assertThat(value, notNullValue());
        assertThat(value.toString(), containsStringIgnoringCase(
                String.format("booking.booker.id = %s && booking.endDate <", booking.getId())));
    }

    @Test
    void getAllUserBookingsShouldThrowUnknownStateEx() {
        GetBookingRequest getBookingRequest = GetBookingRequest.of(State.UNSUPPORTED_STATUS, 1L, false);

        PageRequest pageRequest = PageRequest.of(0, 10);
        UnknownStateException unknownStateException = assertThrows(UnknownStateException.class, ()
                -> bookingService.getAllUserBookings(getBookingRequest, pageRequest));
        assertThat(unknownStateException.getMessage(), containsStringIgnoringCase(State.UNSUPPORTED_STATUS.name()));
    }
}