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

    @Transactional
    public ItemShortDto createNewItem(ItemRequestDto itemRequestDto, Long ownerId) {
        User user = userRepository.findById(ownerId).orElseThrow(()
                -> new UserNotFoundException("Пользователь с id: '" + ownerId + "' не найден"));
        Item item = ItemMapper.itemRequestDtoToItem(itemRequestDto, user);
        return ItemMapper.itemToItemShortDto(itemRepository.save(item));
    }

    @Override
    public CommentResponseDto createNewComment(Long itemId, CommentRequestDto commentRequestDto, Long userId) {
        Booking booking = getBooking(itemId, userId);
        Comment comment = CommentMapper.commentRequestDtoToComment(commentRequestDto, booking.getBooker(), booking.getItem());
        return CommentMapper.commentToCommentResponseDto(commentRepository.save(comment));
    }

    public ItemResponseDto getItemById(Long userId, Long itemId) {
        LocalDateTime now = LocalDateTime.now();
        User user = userRepository.findById(userId).orElseThrow(()
                -> new UserNotFoundException(String.format("Пользователь с id: %s не обнаружен", userId)));
        Item item = itemRepository.findById(itemId).orElseThrow(()
                -> new ItemNotFoundException(String.format("Вещь с id: %s не обнаружена", itemId)));

        BookingShortDto nextBooking = bookingRepository.findNextBookingByItemId(itemId, now)
                .map(BookingMapper::toShortDto).orElse(null);
        BookingShortDto lastBooking = bookingRepository.findLastBookingByItemId(itemId, now)
                .map(BookingMapper::toShortDto).orElse(null);

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
        User owner = userRepository.findById(ownerId).orElseThrow(()
                -> new UserNotFoundException(String.format("Пользователь с id: %s не обнаружен", ownerId)));
        Long itemId = itemRequestDto.getId();
        Item item = itemRepository.findById(itemId).orElseThrow(()
                -> new ItemNotFoundException(String.format("Вещь с id: %s не обнаружена", itemId)));
        checkOwner(item, ownerId);
        item.setOwner(owner);
        setAttributes(itemRequestDto, item);
        return ItemMapper.itemToItemShortDto(itemRepository.save(item));
    }

    private List<ItemResponseDto> connectBookingsAndComments(List<Booking> bookings, List<Item> items,
                                                             List<Comment> comments, LocalDateTime cur) {
        Map<Long, List<Booking>> bookingMap = bookings.stream().collect(Collectors.groupingBy(b -> b.getItem().getId()));
        Map<Long, List<Comment>> commentMap = comments.stream().collect(Collectors.groupingBy(c -> c.getItem().getId()));
        List<ItemResponseDto> result = new ArrayList<>();

        for (Item item : items) {
            BookingShortDto nextBooking = bookingMap.getOrDefault(item.getId(), Collections.emptyList()).stream()
                    .filter(b -> b.getStartDate().isAfter(cur))
                    .sorted(Comparator.comparing(Booking::getStartDate, Comparator.naturalOrder()))
                    .map(BookingMapper::toShortDto)
                    .findFirst().orElse(null);
            BookingShortDto lastBooking = bookingMap.getOrDefault(item.getId(), Collections.emptyList()).stream()
                    .filter(b -> b.getStartDate().isBefore(cur))
                    .sorted(Comparator.comparing(Booking::getStartDate, Comparator.reverseOrder()))
                    .map(BookingMapper::toShortDto)
                    .findFirst().orElse(null);
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

        userRepository.findById(userId).orElseThrow(()
                -> new UserNotFoundException(String.format("Пользователь с id: %s не обнаружен", userId)));
        return ItemMapper.itemToItemRequestDto(itemRepository.searchItemsByNameOrDescription(text));
    }

    @Override
    public List<CommentResponseDto> searchCommentsByText(Long itemId, Long userId, String text) {
        userRepository.findById(userId).orElseThrow(()
                -> new UserNotFoundException(String.format("Пользователь с id: %s не обнаружен", userId)));
        return CommentMapper.commentToCommentResponseDto(commentRepository.searchByText(itemId, text));
    }

    private void checkOwner(Item item, Long ownerId) {
        User owner = item.getOwner();
        if (owner == null || !ownerId.equals(owner.getId())) {
            throw new ItemUpdateException(String.format("Пользователь с id: %s не является владельцем вещи %s", ownerId, item.getName()));
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

    private Booking getBooking(Long itemId, Long userId) {
        return bookingRepository.findFirstByBooker_IdAndItem_IdAndEndDateBefore(userId, itemId, LocalDateTime.now())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        ("Пользователь с id: '" + userId + "' не брал в аренду предмет с id: '" + itemId + "'")));
    }
}