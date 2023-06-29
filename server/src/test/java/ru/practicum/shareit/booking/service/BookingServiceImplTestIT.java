package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.GetBookingRequest;
import ru.practicum.shareit.booking.status.BookingStatus;
import ru.practicum.shareit.booking.status.State;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemShortResponseDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(properties = "db.name=test", webEnvironment = SpringBootTest.WebEnvironment.NONE)
class BookingServiceImplTestIT {
    private final BookingService bookingService;
    private final ItemService itemService;
    private final UserService userService;

    private static UserRequestDto getUserDto(String email) {
        return UserRequestDto.builder()
                .name("Дима")
                .email(email)
                .build();
    }

    private static ItemRequestDto getItemDto() {
        return ItemRequestDto.builder()
                .name("Грабли")
                .description("Грабли для уборки листвы")
                .available(true)
                .build();
    }

    private static BookingRequestDto getBookingRequestDto(Long itemId) {
        return BookingRequestDto.builder()
                .status(BookingStatus.WAITING)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(10))
                .itemId(itemId)
                .build();
    }

    @Test
    void getAllUserBookingsWhenNotOwner() {
        UserRequestDto user = userService.createNewUser(getUserDto("dima@yandex.ru"));
        UserRequestDto owner = userService.createNewUser(getUserDto("fima@yandex.ru"));
        ItemShortResponseDto item = itemService.createNewItem(getItemDto(), owner.getId());
        BookingResponseDto booking = bookingService.createNewBooking(getBookingRequestDto(item.getId()), user.getId());
        GetBookingRequest request = GetBookingRequest.of(State.CURRENT, user.getId(), false);
        PageRequest pageRequest = PageRequest.of(0, 10);

        List<BookingResponseDto> result = bookingService.getAllUserBookings(request, pageRequest);

        assertThat(result, hasSize(1));
        assertThat(result, hasItem(allOf(
                hasProperty("id", equalTo(booking.getId())),
                hasProperty("startDate", equalTo(booking.getStartDate())),
                hasProperty("endDate", equalTo(booking.getEndDate())),
                hasProperty("item", notNullValue()),
                hasProperty("booker", notNullValue()),
                hasProperty("status", equalTo(BookingStatus.WAITING))
        )));
    }

    @Test
    void getAllUserBookingsWhenOwner() {
        UserRequestDto user = userService.createNewUser(getUserDto("dima@yandex.ru"));
        UserRequestDto owner = userService.createNewUser(getUserDto("fima@yandex.ru"));
        ItemShortResponseDto item = itemService.createNewItem(getItemDto(), owner.getId());
        BookingResponseDto booking = bookingService.createNewBooking(getBookingRequestDto(item.getId()), user.getId());
        GetBookingRequest request = GetBookingRequest.of(State.CURRENT, owner.getId(), true);
        PageRequest pageRequest = PageRequest.of(0, 10);

        List<BookingResponseDto> ownerResult = bookingService.getAllUserBookings(request, pageRequest);

        assertThat(ownerResult, hasSize(1));
        assertThat(ownerResult, hasItem(allOf(
                hasProperty("id", equalTo(booking.getId())),
                hasProperty("startDate", equalTo(booking.getStartDate())),
                hasProperty("endDate", equalTo(booking.getEndDate())),
                hasProperty("item", notNullValue()),
                hasProperty("booker", notNullValue()),
                hasProperty("status", equalTo(BookingStatus.WAITING))
        )));
    }

    @Test
    void getAllUserBookingsShouldReturnFutureBookings() {
        UserRequestDto user = userService.createNewUser(getUserDto("dima@yandex.ru"));
        UserRequestDto owner = userService.createNewUser(getUserDto("fima@yandex.ru"));
        ItemShortResponseDto item = itemService.createNewItem(getItemDto(), owner.getId());

        BookingRequestDto bookingRequestDto = getBookingRequestDto(item.getId());
        bookingRequestDto.setStartDate(LocalDateTime.now().plusMinutes(30));
        BookingResponseDto booking = bookingService.createNewBooking(bookingRequestDto, user.getId());

        GetBookingRequest request = GetBookingRequest.of(State.FUTURE, user.getId(), false);
        PageRequest pageRequest = PageRequest.of(0, 10);

        List<BookingResponseDto> result = bookingService.getAllUserBookings(request, pageRequest);

        assertThat(result, hasSize(1));
        assertThat(result, hasItem(allOf(
                hasProperty("id", equalTo(booking.getId())),
                hasProperty("startDate", equalTo(booking.getStartDate())),
                hasProperty("endDate", equalTo(booking.getEndDate())),
                hasProperty("item", notNullValue()),
                hasProperty("booker", notNullValue()),
                hasProperty("status", equalTo(BookingStatus.WAITING))
        )));
    }

    @Test
    void getAllUserBookingsShouldReturnPastBookings() {
        UserRequestDto user = userService.createNewUser(getUserDto("dima@yandex.ru"));
        UserRequestDto owner = userService.createNewUser(getUserDto("fima@yandex.ru"));
        ItemShortResponseDto item = itemService.createNewItem(getItemDto(), owner.getId());

        BookingRequestDto bookingRequestDto = getBookingRequestDto(item.getId());
        bookingRequestDto.setStartDate(LocalDateTime.now().minusDays(15));
        bookingRequestDto.setEndDate(LocalDateTime.now().minusDays(10));
        BookingResponseDto booking = bookingService.createNewBooking(bookingRequestDto, user.getId());

        GetBookingRequest request = GetBookingRequest.of(State.PAST, user.getId(), false);
        PageRequest pageRequest = PageRequest.of(0, 10);

        List<BookingResponseDto> result = bookingService.getAllUserBookings(request, pageRequest);

        assertThat(result, not(empty()));
        assertThat(result, hasItem(allOf(
                hasProperty("id", equalTo(booking.getId())),
                hasProperty("startDate", equalTo(booking.getStartDate())),
                hasProperty("endDate", equalTo(booking.getEndDate())),
                hasProperty("item", notNullValue()),
                hasProperty("booker", notNullValue()),
                hasProperty("status", equalTo(BookingStatus.WAITING))
        )));
    }

    @Test
    void approveBookingShouldChangeStatusApprove() {
        UserRequestDto user = userService.createNewUser(getUserDto("dima@yandex.ru"));
        UserRequestDto owner = userService.createNewUser(getUserDto("fima@yandex.ru"));
        ItemShortResponseDto item = itemService.createNewItem(getItemDto(), owner.getId());
        BookingResponseDto booking = bookingService.createNewBooking(getBookingRequestDto(item.getId()), user.getId());

        BookingResponseDto approveResult = bookingService.approveBooking(booking.getId(), true, owner.getId());

        assertThat(approveResult.getStatus(), equalTo(BookingStatus.APPROVED));
    }

    @Test
    void approveBookingShouldChangeStatusRejected() {
        UserRequestDto user = userService.createNewUser(getUserDto("dima@yandex.ru"));
        UserRequestDto owner = userService.createNewUser(getUserDto("fima@yandex.ru"));
        ItemShortResponseDto item = itemService.createNewItem(getItemDto(), owner.getId());
        BookingResponseDto booking = bookingService.createNewBooking(getBookingRequestDto(item.getId()), user.getId());

        BookingResponseDto rejectedResult = bookingService.approveBooking(booking.getId(), false, owner.getId());

        assertThat(rejectedResult.getStatus(), equalTo(BookingStatus.REJECTED));
    }
}