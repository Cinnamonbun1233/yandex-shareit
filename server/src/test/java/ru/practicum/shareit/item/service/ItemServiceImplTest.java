package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.status.BookingStatus;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.ItemUpdatingException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.RequestItem;
import ru.practicum.shareit.request.repository.RequestItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private RequestItemRepository requestItemRepository;
    @InjectMocks
    private ItemServiceImpl itemService;

    private static CommentRequestDto getCommentDto(User booker, Item item) {
        return CommentRequestDto.builder()
                .id(1L)
                .text("Отличные грабли")
                .item(item)
                .author(booker)
                .created(LocalDateTime.now())
                .build();
    }

    private static ItemRequestDto getItemRequestDto() {
        return ItemRequestDto.builder()
                .id(1L)
                .name("Грабли")
                .description("Грабли для уборки листвы")
                .available(true)
                .requestId(1L)
                .build();
    }

    private static Item getItem(User owner) {
        return Item.builder()
                .id(1L)
                .name("Грабли")
                .description("Грабли для уборки листвы")
                .available(true)
                .owner(owner)
                .build();
    }

    private static User getUser(String email) {
        return User.builder()
                .name("Дима")
                .email(email)
                .build();
    }

    private static RequestItem getRequest(User user) {
        return RequestItem.builder()
                .description("Грабли для уборки листвы")
                .requestor(user)
                .created(LocalDateTime.now())
                .build();
    }

    private static Booking getBooking(Item item, User booker) {
        return Booking.builder()
                .status(BookingStatus.WAITING)
                .startDate(LocalDateTime.now().plusMinutes(5))
                .endDate(LocalDateTime.now().plusDays(10))
                .item(item)
                .booker(booker)
                .build();
    }

    private static List<Comment> getComments(User user, Item item) {
        return List.of(Comment.builder()
                .id(1L)
                .text("Отличные грабли")
                .item(item)
                .author(user)
                .created(LocalDateTime.now())
                .build());
    }

    @Test
    void addNewItemShouldThrowUserNotFoundEx() {
        ItemRequestDto itemRequestDto = getItemRequestDto();

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, ()
                -> itemService.createNewItem(itemRequestDto, 1L));
    }

    @Test
    void addNewItemShouldReturnNotNullRequestId() {
        ItemRequestDto requestDto = getItemRequestDto();
        User owner = getUser("dima@yandex.ru");
        User user = getUser("fima@yandex.ru");
        RequestItem request = getRequest(user);
        Item item = getItem(owner);

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(owner));
        when(requestItemRepository.findById(anyLong()))
                .thenReturn(Optional.of(request));
        when(itemRepository.save(any()))
                .thenReturn(item);

        ItemShortResponseDto result = itemService.createNewItem(requestDto, 1L);

        assertThat(result.getRequestId(), equalTo(request.getId()));
        verify(userRepository, times(1)).findById(anyLong());
        verify(requestItemRepository, times(1)).findById(anyLong());
        verify(itemRepository, times(1)).save(any());
        verifyNoMoreInteractions(userRepository, requestItemRepository, itemRepository);
    }

    @Test
    void updateItemShouldThrowItemUpdatingEx() {
        ItemRequestDto itemRequestDto = getItemRequestDto();
        User owner = getUser("dima@yandex.ru");
        owner.setId(1L);
        User user = getUser("fima@yandex.ru");
        user.setId(2L);
        Item item = getItem(owner);

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        assertThrows(ItemUpdatingException.class, ()
                -> itemService.updateItemByUserId(itemRequestDto, 2L));
    }

    @Test
    void updateItemShouldThrowUserNotFoundEx() {
        ItemRequestDto itemRequestDto = getItemRequestDto();

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, ()
                -> itemService.updateItemByUserId(itemRequestDto, 2L));
    }

    @Test
    void updateItemShouldThrowItemNotFoundEx() {
        ItemRequestDto itemRequestDto = getItemRequestDto();
        User user = getUser("dima@yandex.ru");
        user.setId(1L);

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, ()
                -> itemService.updateItemByUserId(itemRequestDto, 2L));
    }

    @Test
    void updateItemShouldSetAttributes() {
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .name("Грабли")
                .description("Грабли для уборки листвы")
                .available(false)
                .requestId(1L)
                .build();
        User owner = getUser("dima@yandex.ru");
        owner.setId(1L);
        User user = getUser("fima@yandex.ru");
        user.setId(2L);
        Item item = getItem(owner);

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(owner));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(itemRepository.save(any()))
                .thenReturn(item);

        ItemShortResponseDto result = itemService.updateItemByUserId(itemRequestDto, owner.getId());

        assertThat(result.getName(), equalTo(itemRequestDto.getName()));
        assertThat(result.getDescription(), equalTo(itemRequestDto.getDescription()));
        assertFalse(result.getAvailable());
    }

    @Test
    void getItemByIdShouldReturnItemWithNoBookings() {
        User owner = getUser("dima@yandex.ru");
        owner.setId(1L);
        User user = getUser("fima@yandex.ru");
        user.setId(2L);
        Item item = getItem(owner);
        Booking booking = getBooking(item, user);
        List<Comment> comments = getComments(user, item);

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(bookingRepository.findNextBookingByItemId(anyLong(), any()))
                .thenReturn(Optional.of(booking));
        when(bookingRepository.findLastBookingByItemId(anyLong(), any()))
                .thenReturn(Optional.empty());
        when(commentRepository.findAllByItemIdOrderByCreatedDesc(anyLong()))
                .thenReturn(comments);

        ItemResponseDto result = itemService.getItemByUserId(2L, 1L);

        assertThat(result.getNextBooking(), nullValue());
        assertThat(result.getLastBooking(), nullValue());
        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).findLastBookingByItemId(anyLong(), any());
        verify(bookingRepository, times(1)).findNextBookingByItemId(anyLong(), any());
        verify(commentRepository, times(1)).findAllByItemIdOrderByCreatedDesc(anyLong());
        verifyNoMoreInteractions(userRepository, itemRepository, bookingRepository, commentRepository);
    }

    @Test
    void getItemByIdShouldThrowUserNotFound() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> itemService.getItemByUserId(1L, 1L));
    }

    @Test
    void getItemByIdShouldThrowItemNotFound() {
        User user = getUser("dima@yandex.ru");
        user.setId(1L);

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () -> itemService.getItemByUserId(1L, 1L));
    }

    @Test
    void getItemByIdShouldReturnItemWithBookings() {
        User owner = getUser("dima@yandex.ru");
        owner.setId(1L);
        User user = getUser("fima@yandex.ru");
        user.setId(2L);
        Item item = getItem(owner);
        Booking booking = getBooking(item, user);
        List<Comment> comments = getComments(user, item);

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(owner));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(bookingRepository.findNextBookingByItemId(anyLong(), any()))
                .thenReturn(Optional.of(booking));
        when(bookingRepository.findLastBookingByItemId(anyLong(), any()))
                .thenReturn(Optional.of(booking));
        when(commentRepository.findAllByItemIdOrderByCreatedDesc(anyLong()))
                .thenReturn(comments);

        ItemResponseDto result = itemService.getItemByUserId(1L, 1L);

        assertThat(result.getNextBooking(), notNullValue());
        assertThat(result.getLastBooking(), notNullValue());
        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).findLastBookingByItemId(anyLong(), any());
        verify(bookingRepository, times(1)).findNextBookingByItemId(anyLong(), any());
        verify(commentRepository, times(1)).findAllByItemIdOrderByCreatedDesc(anyLong());
        verifyNoMoreInteractions(userRepository, itemRepository, bookingRepository, commentRepository);
    }

    @Test
    void getItemsByOwnerShouldReturnItemsWithNotNullBookings() {
        User owner = getUser("dima@yandex.ru");
        owner.setId(1L);
        User user = getUser("fima@yandex.ru");
        user.setId(2L);
        Item item = getItem(owner);
        List<Item> items = List.of(item);
        Booking firstBook = getBooking(item, user);
        Booking secondBook = getBooking(item, user);
        secondBook.setStartDate(LocalDateTime.now().minusDays(1L));

        List<Booking> bookings = List.of(firstBook, secondBook);
        List<Comment> comments = getComments(user, item);

        when(itemRepository.findAllByOwnerId(anyLong(), any()))
                .thenReturn(items);
        when(bookingRepository.findAllByItemIdIn(anyList()))
                .thenReturn(bookings);
        when(commentRepository.findAllByItemIdIn(anyList()))
                .thenReturn(comments);

        List<ItemResponseDto> result = itemService.getAllItemsByUserId(1L, 0, 10);

        assertThat(result, hasSize(1));
        assertThat(result, hasItem(allOf(
                hasProperty("nextBooking", notNullValue()),
                hasProperty("lastBooking", notNullValue())
        )));
        verify(itemRepository, times(1)).findAllByOwnerId(anyLong(), any());
        verify(bookingRepository, times(1)).findAllByItemIdIn(anyList());
        verify(commentRepository, times(1)).findAllByItemIdIn(anyList());
        verifyNoMoreInteractions(itemRepository, bookingRepository, commentRepository);

    }

    @Test
    void getItemsByOwnerShouldReturnItemsWithNullBookings() {
        User owner = getUser("dima@yandex.ru");
        owner.setId(1L);
        User user = getUser("fima@yandex.ru");
        user.setId(2L);
        Item item = getItem(owner);
        List<Item> items = List.of(item);

        List<Booking> bookings = Collections.emptyList();
        List<Comment> comments = getComments(user, item);

        when(itemRepository.findAllByOwnerId(anyLong(), any()))
                .thenReturn(items);
        when(bookingRepository.findAllByItemIdIn(anyList()))
                .thenReturn(bookings);
        when(commentRepository.findAllByItemIdIn(anyList()))
                .thenReturn(comments);

        List<ItemResponseDto> result = itemService.getAllItemsByUserId(1L, 0, 10);

        assertThat(result, hasSize(1));
        assertThat(result, hasItem(allOf(
                hasProperty("nextBooking", nullValue()),
                hasProperty("lastBooking", nullValue())
        )));
        verify(itemRepository, times(1)).findAllByOwnerId(anyLong(), any());
        verify(bookingRepository, times(1)).findAllByItemIdIn(anyList());
        verify(commentRepository, times(1)).findAllByItemIdIn(anyList());
        verifyNoMoreInteractions(itemRepository, bookingRepository, commentRepository);
    }

    @Test
    void searchShouldReturnEmptyResult() {
        GetSearchItem search = GetSearchItem.of("", 1L, 0, 10);

        List<ItemRequestDto> result = itemService.search(search);

        assertThat(result, empty());
    }

    @Test
    void searchShouldReturnItem() {
        User owner = getUser("dima@yandex.ru");
        owner.setId(1L);
        User user = getUser("fima@yandex.ru");
        user.setId(2L);
        Item item = getItem(owner);
        List<Item> items = List.of(item);
        GetSearchItem search = GetSearchItem.of("Грабли", 1L, 0, 10);

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRepository.searchItemsByNameOrDescription(anyString(), any()))
                .thenReturn(items);
        List<ItemRequestDto> result = itemService.search(search);

        assertThat(result, hasSize(1));
        assertThat(result, hasItem(allOf(
                hasProperty("id", equalTo(item.getId())),
                hasProperty("name", equalTo(item.getName())),
                hasProperty("description", equalTo(item.getDescription()))
        )));
    }

    @Test
    void searchShouldThrowUserNotFoundEx() {
        GetSearchItem getSearchItem = GetSearchItem.of("Грабли", 1L, 0, 10);

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> itemService.search(getSearchItem));
    }

    @Test
    void searchCommentsByTextShouldReturnEmptyComments() {
        GetSearchItem getSearchItem = GetSearchItem.of("", 1L, 1L, 0, 10);

        List<CommentResponseDto> result = itemService.searchCommentsByText(getSearchItem);

        assertThat(result, empty());
    }

    @Test
    void searchCommentsByTextShouldThrowUserNotFound() {
        GetSearchItem search = GetSearchItem.of("Отл", 1L, 1L, 0, 10);

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> itemService.searchCommentsByText(search));
    }

    @Test
    void searchCommentsByTextShouldReturnNotEmptyResult() {
        User owner = getUser("dima@yandex.ru");
        owner.setId(1L);
        User user = getUser("fima@yandex.ru");
        user.setId(2L);
        Item item = getItem(owner);

        List<Comment> comments = getComments(user, item);
        GetSearchItem search = GetSearchItem.of("какой-то поиск", 1L, 1L, 0, 10);

        when(commentRepository.searchByText(anyLong(), anyString(), any()))
                .thenReturn(comments);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        List<CommentResponseDto> result = itemService.searchCommentsByText(search);

        assertThat(result, not(empty()));
        verify(commentRepository, times(1)).searchByText(anyLong(), anyString(), any());
        verify(userRepository, times(1)).findById(anyLong());
        verifyNoMoreInteractions(commentRepository, userRepository);
    }

    @Test
    void addCommentShouldThrowExWhenUserNeverBookedItem() {
        User booker = getUser("fima@yandex.ru");
        User owner = getUser("dima@yandex.ru");
        Item item = getItem(owner);
        CommentRequestDto commentRequestDto = getCommentDto(booker, item);

        when(bookingRepository.findFirstByBookerIdAndItemIdAndEndDateBefore(anyLong(), anyLong(), any()))
                .thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, ()
                -> itemService.createNewComment(item.getId(), commentRequestDto, 1L));
        verifyNoInteractions(commentRepository);
    }

    @Test
    void addCommentShouldReturnComment() {
        User booker = getUser("fima@yandex.ru");
        booker.setId(1L);
        User owner = getUser("dima@yandex.ru");
        owner.setId(2L);
        Item item = getItem(owner);
        Booking booking = getBooking(item, booker);
        CommentRequestDto commentRequestDto = getCommentDto(booker, item);
        Comment comment = getComments(booker, item).get(0);

        when(bookingRepository.findFirstByBookerIdAndItemIdAndEndDateBefore(anyLong(), anyLong(), any()))
                .thenReturn(Optional.of(booking));
        when(commentRepository.save(any()))
                .thenReturn(comment);
        CommentResponseDto result = itemService.createNewComment(item.getId(), commentRequestDto, booker.getId());

        assertThat(result, notNullValue());
        assertThat(result.getId(), equalTo(comment.getId()));
        assertThat(result.getText(), equalTo(comment.getText()));
        assertThat(result.getAuthorName(), equalTo(booker.getName()));
    }
}