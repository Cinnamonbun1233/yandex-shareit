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
import ru.practicum.shareit.exception.ItemUpdatingException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemShortResponseDto;
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

    @Transactional
    public ItemShortResponseDto createNewItem(ItemRequestDto itemRequestDto, Long ownerId) {
        Item item = ItemMapper.itemRequestDtoToItem(itemRequestDto, getUser(ownerId));
        return ItemMapper.itemToItemShortResponseDto(itemRepository.save(item));
    }

    @Override
    public CommentResponseDto createNewComment(Long itemId, CommentRequestDto commentRequestDto, Long userId) {
        Booking booking = getBooking(itemId, userId);
        Comment comment = CommentMapper.commentRequestDtoToComment(commentRequestDto, booking.getBooker(), booking.getItem());
        return CommentMapper.commentToCommentResponseDto(commentRepository.save(comment));
    }

    public ItemResponseDto getItemById(Long userId, Long itemId) {
        BookingShortDto nextBooking = bookingRepository.findNextBookingByItemId(itemId, LocalDateTime.now())
                .map(BookingMapper::bookingToBookingShortDto).orElse(null);
        BookingShortDto lastBooking = bookingRepository.findLastBookingByItemId(itemId, LocalDateTime.now())
                .map(BookingMapper::bookingToBookingShortDto).orElse(null);
        List<CommentResponseDto> commentResponseDtoList = CommentMapper.commentsToCommentResponseDtoList(
                commentRepository.findAllByItemIdOrderByCreatedDesc(itemId));

        if (getUser(userId).equals(getItem(itemId).getOwner())) {
            return ItemMapper.itemToItemResponseDto(getItem(itemId), nextBooking, lastBooking, commentResponseDtoList);
        } else {
            return ItemMapper.itemToItemResponseDto(getItem(itemId), null, null, commentResponseDtoList);
        }
    }

    public List<ItemResponseDto> getItemsByOwner(Long ownerId) {
        List<Item> itemList = itemRepository.findAllByOwnerId(ownerId);
        List<Long> ids = itemList.stream()
                .map(Item::getId)
                .collect(Collectors.toList());
        List<Booking> bookingList = bookingRepository.findAllByItemIdIn(ids);
        List<Comment> commentList = commentRepository.findAllByItemIdIn(ids);

        if (!bookingList.isEmpty()) {
            return connectBookingsAndComments(bookingList, itemList, commentList, LocalDateTime.now());
        } else {
            return ItemMapper.itemToItemResponseDtoList(itemList);
        }
    }

    @Transactional
    public ItemShortResponseDto updateItemByOwner(ItemRequestDto itemRequestDto, Long ownerId) {
        User owner = userRepository.findById(ownerId).orElseThrow(()
                -> new UserNotFoundException("Пользователь с id: '" + ownerId + "' не найден"));
        Long itemId = itemRequestDto.getId();
        Item item = itemRepository.findById(itemId).orElseThrow(()
                -> new ItemNotFoundException("Предмет с id: '" + itemId + "' не найден"));
        checkOwner(item, ownerId);
        item.setOwner(owner);
        setAttributes(itemRequestDto, item);
        return ItemMapper.itemToItemShortResponseDto(itemRepository.save(item));
    }

    public List<ItemRequestDto> searchItemsByText(String text, Long userId) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        getUser(userId);
        return ItemMapper.itemsToItemRequestDtoList(itemRepository.searchItemsByNameOrDescription(text));
    }

    @Override
    public List<CommentResponseDto> searchCommentsByText(Long itemId, Long userId, String text) {
        getUser(userId);
        return CommentMapper.commentsToCommentResponseDtoList(commentRepository.searchByText(itemId, text));
    }

    private List<ItemResponseDto> connectBookingsAndComments(List<Booking> bookings, List<Item> items,
                                                             List<Comment> comments, LocalDateTime now) {
        Map<Long, List<Booking>> bookingMap = bookings.stream()
                .collect(Collectors.groupingBy(b -> b.getItem().getId()));
        Map<Long, List<Comment>> commentMap = comments.stream()
                .collect(Collectors.groupingBy(c -> c.getItem().getId()));
        List<ItemResponseDto> itemResponseDtoList = new ArrayList<>();

        for (Item item : items) {
            BookingShortDto nextBooking = getAfterBookingShortDto(now, bookingMap, item);
            BookingShortDto lastBooking = getBeforeBookingShortDto(now, bookingMap, item);
            List<CommentResponseDto> commentResponseDtoList = CommentMapper
                    .commentsToCommentResponseDtoList(commentMap.getOrDefault(item.getId(), Collections.emptyList()));
            itemResponseDtoList.add(ItemMapper.itemToItemResponseDto(item, nextBooking, lastBooking, commentResponseDtoList));
        }

        return itemResponseDtoList;
    }

    private void setAttributes(ItemRequestDto itemRequestDto, Item item) {
        if (itemRequestDto.getName() != null) {
            item.setName(itemRequestDto.getName());
        }
        if (itemRequestDto.getDescription() != null) {
            item.setDescription(itemRequestDto.getDescription());
        }
        if (itemRequestDto.getAvailable() != null) {
            item.setAvailable(itemRequestDto.getAvailable());
        }
    }

    private void checkOwner(Item item, Long ownerId) {
        User owner = item.getOwner();
        if (owner == null || !ownerId.equals(owner.getId())) {
            throw new ItemUpdatingException(
                    String.format("Пользователь с id: %s не является владельцем вещи %s", ownerId, item.getName()));
        }
    }

    private User getUser(Long ownerId) {
        return userRepository.findById(ownerId).orElseThrow(()
                -> new UserNotFoundException("Пользователь с id: '" + ownerId + "' не найден"));
    }

    private Item getItem(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(()
                -> new ItemNotFoundException("Предмет с id: '" + itemId + "' не найден"));
    }

    private Booking getBooking(Long itemId, Long userId) {
        return bookingRepository.findFirstByBookerIdAndItemIdAndEndDateBefore(userId, itemId, LocalDateTime.now())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        ("Пользователь с id: '" + userId + "' не брал в аренду предмет с id: '" + itemId + "'")));
    }

    private static BookingShortDto getBeforeBookingShortDto(LocalDateTime now, Map<Long, List<Booking>> bookingMap, Item item) {
        return bookingMap.getOrDefault(item.getId(), Collections.emptyList()).stream()
                .filter(b -> b.getStartDate().isBefore(now))
                .sorted(Comparator.comparing(Booking::getStartDate, Comparator.reverseOrder()))
                .map(BookingMapper::bookingToBookingShortDto)
                .findFirst()
                .orElse(null);
    }

    private static BookingShortDto getAfterBookingShortDto(LocalDateTime now, Map<Long, List<Booking>> bookingMap, Item item) {
        return bookingMap.getOrDefault(item.getId(), Collections.emptyList()).stream()
                .filter(b -> b.getStartDate().isAfter(now))
                .sorted(Comparator.comparing(Booking::getStartDate, Comparator.naturalOrder()))
                .map(BookingMapper::bookingToBookingShortDto)
                .findFirst()
                .orElse(null);
    }
}