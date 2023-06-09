package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.dto.CommentRequestDto;
import ru.practicum.shareit.comment.dto.CommentResponseDto;
import ru.practicum.shareit.comment.mapper.CommentMapper;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.ItemUpdateException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public ItemShortDto createNewItem(ItemRequestDto itemRequestDto, Long ownerId) {
        User user = getUserRepo(ownerId);
        Item item = ItemMapper.itemRequestDtoToItem(itemRequestDto, user);
        return ItemMapper.itemToItemShortDto(itemRepository.save(item));
    }

    @Override
    public CommentResponseDto createNewComment(Long itemId, CommentRequestDto commentRequestDto, Long userId) {
        Booking booking = getBookingRepo(itemId, userId);
        Comment comment = CommentMapper.commentRequestDtoToComment(commentRequestDto, booking.getBooker(), booking.getItem());
        return CommentMapper.commentToCommentResponseDto(commentRepository.save(comment));
    }

    public ItemResponseDto getItemById(Long userId, Long itemId) {
        LocalDateTime now = LocalDateTime.now();
        User user = getUserRepo(userId);
        Item item = getItemRepo(itemId);

        BookingShortDto nextBooking = bookingRepository.findNextBookingByItemId(itemId, now)
                .map(BookingMapper::bookingToBookingShortDto).orElse(null);
        BookingShortDto lastBooking = bookingRepository.findLastBookingByItemId(itemId, now)
                .map(BookingMapper::bookingToBookingShortDto).orElse(null);
        List<CommentResponseDto> comments = CommentMapper.commentToCommentResponseDto(
                commentRepository.findAllByItem_IdOrderByCreatedDesc(itemId));

        if (user.equals(item.getOwner())) {
            return ItemMapper.itemToItemResponseDto(item, nextBooking, lastBooking, comments);
        } else {
            return ItemMapper.itemToItemResponseDto(item, null, null, comments);
        }
    }


    public List<ItemResponseDto> getItemsByOwner(Long ownerId) {
        LocalDateTime now = LocalDateTime.now();
        List<Item> items = itemRepository.findAllByOwner_Id(ownerId);
        List<Long> ids = items.stream().map(Item::getId).collect(Collectors.toList());
        List<Booking> bookings = bookingRepository.findAllByItem_IdIn(ids);
        List<Comment> comments = commentRepository.findAllByItemIdIn(ids);

        if (!bookings.isEmpty()) {
            return connectBookingsAndComments(bookings, items, comments, now);
        } else {
            return ItemMapper.itemToItemResponseDto(items);
        }
    }

    @Transactional
    public ItemShortDto updateItem(ItemRequestDto itemRequestDto, Long ownerId) {
        User owner = getUserRepo(ownerId);
        Long itemId = itemRequestDto.getId();
        Item item = getItemRepo(itemId);
        checkOwner(item, ownerId);
        item.setOwner(owner);
        setAttributes(itemRequestDto, item);
        return ItemMapper.itemToItemShortDto(itemRepository.save(item));
    }

    private List<ItemResponseDto> connectBookingsAndComments(List<Booking> bookings, List<Item> items,
                                                             List<Comment> comments, LocalDateTime now) {
        Map<Long, List<Booking>> bookingMap = bookings.stream().collect(Collectors.groupingBy(b -> b.getItem().getId()));
        Map<Long, List<Comment>> commentMap = comments.stream().collect(Collectors.groupingBy(c -> c.getItem().getId()));
        List<ItemResponseDto> result = new ArrayList<>();

        for (Item item : items) {
            BookingShortDto nextBooking = getBookingShortDtoNext(now, bookingMap, item);
            BookingShortDto lastBooking = getBookingShortDtoLast(now, bookingMap, item);
            List<CommentResponseDto> responseComments = CommentMapper.commentToCommentResponseDto(commentMap.getOrDefault(item.getId(),
                    Collections.emptyList()));
            result.add(ItemMapper.itemToItemResponseDto(item, nextBooking, lastBooking, responseComments));
        }

        return result;
    }

    public List<ItemRequestDto> search(String text, Long userId) {

        if (text.isBlank()) {
            return Collections.emptyList();
        }

        getUserRepo(userId);
        return ItemMapper.itemToItemRequestDto(itemRepository.searchItemsByNameOrDescription(text));
    }

    @Override
    public List<CommentResponseDto> searchCommentsByText(Long itemId, Long userId, String text) {
        getUserRepo(userId);
        return CommentMapper.commentToCommentResponseDto(commentRepository.searchByText(itemId, text));
    }

    private void checkOwner(Item item, Long ownerId) {
        User owner = item.getOwner();

        if (owner == null || !ownerId.equals(owner.getId())) {
            throw new ItemUpdateException("Пользователь с id: '" + ownerId + "' не является владельцем вещи: '"
                    + item.getName() + "'");
        }
    }

    private void setAttributes(ItemRequestDto itemRequestDto, Item item) {
        String name = itemRequestDto.getName();
        String description = itemRequestDto.getDescription();
        Boolean available = itemRequestDto.getAvailable();

        if (name != null) {
            item.setName(name);
        }
        if (description != null) {
            item.setDescription(description);
        }
        if (available != null) {
            item.setAvailable(available);
        }
    }

    private Booking getBookingRepo(Long itemId, Long userId) {
        return bookingRepository.findFirstByBooker_IdAndItem_IdAndEndDateBefore(userId, itemId, LocalDateTime.now())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        ("Пользователь с id: '" + userId + "' не брал в аренду предмет с id: '" + itemId + "'")));
    }

    private User getUserRepo(Long userId) {
        return userRepository.findById(userId).orElseThrow(()
                -> new UserNotFoundException("Пользователь с id: '" + userId + "' не найден"));
    }

    private Item getItemRepo(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(()
                -> new ItemNotFoundException("Предмет с id: '" + itemId + "' не найден"));
    }

    private static BookingShortDto getBookingShortDtoLast(LocalDateTime now, Map<Long, List<Booking>> bookingMap, Item item) {
        return bookingMap.getOrDefault(item.getId(), Collections.emptyList()).stream()
                .filter(b -> b.getStartDate().isBefore(now))
                .sorted(Comparator.comparing(Booking::getStartDate, Comparator.reverseOrder()))
                .map(BookingMapper::bookingToBookingShortDto)
                .findFirst().orElse(null);
    }

    private static BookingShortDto getBookingShortDtoNext(LocalDateTime now, Map<Long, List<Booking>> bookingMap, Item item) {
        return bookingMap.getOrDefault(item.getId(), Collections.emptyList()).stream()
                .filter(b -> b.getStartDate().isAfter(now))
                .sorted(Comparator.comparing(Booking::getStartDate, Comparator.naturalOrder()))
                .map(BookingMapper::bookingToBookingShortDto)
                .findFirst().orElse(null);
    }
}