package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.status.BookingStatus;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Transactional
@SpringBootTest(properties = "db.name=test", webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceImplTestIT {
    private final ItemService itemService;
    private final BookingService bookingService;
    private final UserService userService;

    private static UserRequestDto getUserDto(String email) {
        return UserRequestDto.builder()
                .name("Дима")
                .email(email)
                .build();
    }

    private static CommentRequestDto getCommentDto() {
        return CommentRequestDto.builder()
                .text("Отличные грабли")
                .build();
    }

    private static ItemRequestDto getItemDto() {
        return ItemRequestDto.builder()
                .name("Грабли")
                .description("Грабли для уборки листвы")
                .available(true)
                .build();
    }

    private static BookingRequestDto getBookingRequestDto(Long itemId, LocalDateTime start, LocalDateTime end) {
        return BookingRequestDto.builder()
                .status(BookingStatus.WAITING)
                .startDate(start)
                .endDate(end)
                .itemId(itemId)
                .build();
    }

    @Test
    void getItemByIdWhenOwnerShouldReturnItem() {
        LocalDateTime now = LocalDateTime.now();
        UserRequestDto user = userService.createNewUser(getUserDto("dima@yandex.ru"));
        UserRequestDto owner = userService.createNewUser(getUserDto("fima@yandex.ru"));
        ItemShortResponseDto item = itemService.createNewItem(getItemDto(), owner.getId());
        BookingResponseDto nextBooking = bookingService.createNewBooking(getBookingRequestDto(
                item.getId(), now.plusDays(1L), now.plusDays(4L)), user.getId());

        ItemResponseDto result = itemService.getItemByUserId(owner.getId(), item.getId());

        assertThat(result, notNullValue());
        assertThat(result.getNextBooking().getId(), equalTo(nextBooking.getId()));
        assertThat(result.getComments(), empty());
    }

    @Test
    void getItemByIdWhenNotOwnerBookingsShouldBeNull() {
        LocalDateTime now = LocalDateTime.now();

        UserRequestDto user = userService.createNewUser(getUserDto("dima@yandex.ru"));
        UserRequestDto owner = userService.createNewUser(getUserDto("fima@yandex.ru"));
        ItemShortResponseDto item = itemService.createNewItem(getItemDto(), owner.getId());
        BookingResponseDto nextBooking = bookingService.createNewBooking(getBookingRequestDto(
                item.getId(), now.plusDays(1L), now.plusDays(4L)), user.getId());

        ItemResponseDto result = itemService.getItemByUserId(user.getId(), item.getId());

        assertThat(result, notNullValue());
        assertThat(result.getNextBooking(), nullValue());
        assertThat(result.getLastBooking(), nullValue());
        assertThat(result.getComments(), empty());
    }

    @Test
    void getItemsByOwnerBookingsNotEmptyCommentsNotNull() {
        LocalDateTime now = LocalDateTime.now();

        UserRequestDto user = userService.createNewUser(getUserDto("dima@yandex.ru"));
        UserRequestDto owner = userService.createNewUser(getUserDto("fima@yandex.ru"));
        ItemShortResponseDto item = itemService.createNewItem(getItemDto(), owner.getId());
        BookingResponseDto nextBooking = bookingService.createNewBooking(getBookingRequestDto(
                item.getId(), now.plusDays(1L), now.plusDays(4L)), user.getId());
        BookingResponseDto lastBooking = bookingService.createNewBooking(getBookingRequestDto(
                item.getId(), now.minusDays(4L), now.minusDays(1L)), user.getId());

        CommentResponseDto comment = itemService.createNewComment(item.getId(), getCommentDto(), user.getId());

        List<ItemResponseDto> result = itemService.getAllItemsByUserId(owner.getId(), 0, 10);

        assertThat(result, not(empty()));
        assertThat(result, hasItem(allOf(
                hasProperty("id", equalTo(item.getId())),
                hasProperty("name", equalTo(item.getName())),
                hasProperty("description", equalTo(item.getDescription())),
                hasProperty("available", equalTo(item.getAvailable())),
                hasProperty("nextBooking", notNullValue()),
                hasProperty("lastBooking", notNullValue()),
                hasProperty("comments", not(empty()))
        )));
    }

    @Test
    void getItemsByOwnerBookingsNullCommentsNull() {
        UserRequestDto owner = userService.createNewUser(getUserDto("fima@yandex.ru"));
        ItemShortResponseDto item = itemService.createNewItem(getItemDto(), owner.getId());

        List<ItemResponseDto> result = itemService.getAllItemsByUserId(owner.getId(), 0, 10);

        assertThat(result, not(empty()));
        assertThat(result, hasItem(allOf(
                hasProperty("id", equalTo(item.getId())),
                hasProperty("name", equalTo(item.getName())),
                hasProperty("description", equalTo(item.getDescription())),
                hasProperty("available", equalTo(item.getAvailable())),
                hasProperty("nextBooking", nullValue()),
                hasProperty("lastBooking", nullValue()),
                hasProperty("comments", nullValue())
        )));
    }

    @Test
    void searchFindItemWithIgnoringCaseAndSubStringInput() {
        UserRequestDto user = userService.createNewUser(getUserDto("dima@yandex.ru"));
        UserRequestDto owner = userService.createNewUser(getUserDto("fima@yandex.ru"));
        ItemShortResponseDto item = itemService.createNewItem(getItemDto(), owner.getId());
        GetSearchItem search = GetSearchItem.of("ГрАбЛи", user.getId(), 0, 10);

        List<ItemRequestDto> result = itemService.search(search);

        assertThat(result, not(empty()));
        assertThat(result, hasItem(allOf(
                hasProperty("name", containsStringIgnoringCase("Грабли")),
                hasProperty("description", containsStringIgnoringCase("Грабли"))
        )));
    }

    @Test
    void searchShouldReturnEmptyResultWhenSearchRequestNotFound() {
        UserRequestDto user = userService.createNewUser(getUserDto("dima@yandex.ru"));
        UserRequestDto owner = userService.createNewUser(getUserDto("fima@yandex.ru"));
        ItemShortResponseDto item = itemService.createNewItem(getItemDto(), owner.getId());
        GetSearchItem search = GetSearchItem.of("Лопата", user.getId(), 0, 10);

        List<ItemRequestDto> result = itemService.search(search);

        assertThat(result, empty());
    }

    @Test
    void searchShouldReturnEmptyResultWhenSearchRequestIsEmptyString() {
        UserRequestDto user = userService.createNewUser(getUserDto("dima@yandex.ru"));
        UserRequestDto owner = userService.createNewUser(getUserDto("fima@yandex.ru"));
        ItemShortResponseDto item = itemService.createNewItem(getItemDto(), owner.getId());
        GetSearchItem search = GetSearchItem.of("", user.getId(), 0, 10);

        List<ItemRequestDto> result = itemService.search(search);

        assertThat(result, empty());
    }

    @Test
    void searchCommentsByTextShouldReturnCommentsWhenSearchRequestIsFound() {
        LocalDateTime now = LocalDateTime.now();
        UserRequestDto user = userService.createNewUser(getUserDto("dima@yandex.ru"));
        UserRequestDto owner = userService.createNewUser(getUserDto("fima@yandex.ru"));
        ItemShortResponseDto item = itemService.createNewItem(getItemDto(), owner.getId());
        BookingResponseDto lastBooking = bookingService.createNewBooking(getBookingRequestDto(
                item.getId(), now.minusDays(4L), now.minusDays(1L)), user.getId());
        CommentResponseDto comment = itemService.createNewComment(item.getId(), getCommentDto(), user.getId());
        GetSearchItem search = GetSearchItem.of("Отличные", user.getId(), item.getId(), 0, 10);

        List<CommentResponseDto> result = itemService.searchCommentsByText(search);

        assertThat(result, not(empty()));
        assertThat(result, hasItem(allOf(
                hasProperty("id", equalTo(comment.getId())),
                hasProperty("text", containsStringIgnoringCase("Отличные грабли")),
                hasProperty("authorName", equalTo(user.getName())),
                hasProperty("created", notNullValue())
        )));
    }

    @Test
    void searchCommentsByTextShouldReturnEmptyListWhenSearchRequestIsEmptyString() {
        LocalDateTime now = LocalDateTime.now();
        UserRequestDto user = userService.createNewUser(getUserDto("dima@yandex.ru"));
        UserRequestDto owner = userService.createNewUser(getUserDto("fima@yandex.ru"));
        ItemShortResponseDto item = itemService.createNewItem(getItemDto(), owner.getId());
        BookingResponseDto lastBooking = bookingService.createNewBooking(getBookingRequestDto(
                item.getId(), now.minusDays(4L), now.minusDays(1L)), user.getId());
        CommentResponseDto comment = itemService.createNewComment(item.getId(), getCommentDto(), user.getId());
        GetSearchItem search = GetSearchItem.of("", user.getId(), item.getId(), 0, 10);

        List<CommentResponseDto> result = itemService.searchCommentsByText(search);

        assertThat(result, empty());
    }
}