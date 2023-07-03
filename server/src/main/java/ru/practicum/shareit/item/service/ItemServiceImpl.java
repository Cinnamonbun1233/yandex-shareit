package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.validation.BookingMapper;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.ItemUpdatingException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.request.repository.RequestItemRepository;
import ru.practicum.shareit.request.model.RequestItem;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.model.User;

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
    private final RequestItemRepository requestRepository;

    @Transactional
    public ItemShortResponseDto createNewItem(ItemRequestDto itemRequestDto, Long ownerId) {
        User user = userRepository.findById(ownerId).orElseThrow(()
                -> new UserNotFoundException(String.format("Пользователь с id: %s не обнаружен", ownerId)));
        RequestItem requestItem = null;

        if (itemRequestDto.getRequestId() != null) {
            requestItem = requestRepository.findById(itemRequestDto.getRequestId()).orElse(null);
        }

        Item item = ItemMapper.itemRequestDtoToItem(itemRequestDto, user, requestItem);
        return ItemMapper.itemToItemShortResponseDto(itemRepository.save(item));
    }

    @Override
    @Transactional
    public CommentResponseDto createNewComment(Long itemId, CommentRequestDto commentRequestDto, Long userId) {
        Booking booking = bookingRepository.findFirstByBookerIdAndItemIdAndEndDateBefore(userId, itemId, LocalDateTime.now())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        String.format("Пользователь с id: %s не брал в аренду вещь с id: %s", userId, itemId)));
        Comment comment = CommentMapper.commentRequestDtoToComment(commentRequestDto, booking.getBooker(), booking.getItem());
        return CommentMapper.commentToCommentResponseDto(commentRepository.save(comment));
    }

    public List<ItemResponseDto> getAllItemsByUserId(Long ownerId, int from, int size) {
        LocalDateTime now = LocalDateTime.now();
        Pageable page = PageRequest.of(from > 0 ? from / size : 0, size, Sort.by(Sort.Direction.ASC, "id"));
        List<Item> items = itemRepository.findAllByOwnerId(ownerId, page);
        List<Long> ids = items.stream().map(Item::getId).collect(Collectors.toList());
        List<Booking> bookings = bookingRepository.findAllByItemIdIn(ids);
        List<Comment> comments = commentRepository.findAllByItemIdIn(ids);

        if (!bookings.isEmpty()) {
            return connectBookingsAndComments(bookings, items, comments, now);
        } else {
            return ItemMapper.itemsToItemResponseDtoList(items);
        }
    }

    public ItemResponseDto getItemByUserId(Long userId, Long itemId) {
        LocalDateTime now = LocalDateTime.now();
        User user = userRepository.findById(userId).orElseThrow(()
                -> new UserNotFoundException(String.format("Пользователь с id: %s не обнаружен", userId)));
        Item item = itemRepository.findById(itemId).orElseThrow(()
                -> new ItemNotFoundException(String.format("Вещь с id: %s не обнаружена", itemId)));
        BookingShortDto nextBooking = bookingRepository.findNextBookingByItemId(itemId, now)
                .map(BookingMapper::bookingToBookingShortDto).orElse(null);
        BookingShortDto lastBooking = bookingRepository.findLastBookingByItemId(itemId, now)
                .map(BookingMapper::bookingToBookingShortDto).orElse(null);
        List<CommentResponseDto> comments = CommentMapper.commentsToCommentResponseDtoList(
                commentRepository.findAllByItemIdOrderByCreatedDesc(itemId));

        if (user.equals(item.getOwner())) {
            return ItemMapper.itemToItemResponseDto(item, nextBooking, lastBooking, comments);
        } else {
            return ItemMapper.itemToItemResponseDto(item, null, null, comments);
        }
    }

    @Transactional
    public ItemShortResponseDto updateItemByUserId(ItemRequestDto itemRequestDto, Long ownerId) {
        User owner = userRepository.findById(ownerId).orElseThrow(()
                -> new UserNotFoundException(String.format("Пользователь с id: %s не обнаружен", ownerId)));
        Long itemId = itemRequestDto.getId();
        Item item = itemRepository.findById(itemId).orElseThrow(()
                -> new ItemNotFoundException(String.format("Вещь с id: %s не обнаружена", itemId)));
        checkOwner(item, ownerId);
        item.setOwner(owner);
        setAttributes(itemRequestDto, item);
        return ItemMapper.itemToItemShortResponseDto(itemRepository.save(item));
    }

    public List<ItemRequestDto> search(GetSearchItem search) {

        if (search.isBlank()) {
            return Collections.emptyList();
        }

        Pageable page = PageRequest.of(search.getFrom(), search.getSize());
        userRepository.findById(search.getUserId()).orElseThrow(()
                -> new UserNotFoundException(String.format("Пользователь с id: %s не обнаружен", search.getUserId())));
        return ItemMapper.itemsToItemRequestDtoList(itemRepository.searchItemsByNameOrDescription(search.getText(), page));
    }

    @Override
    public List<CommentResponseDto> searchCommentsByText(GetSearchItem search) {
        if (search.isBlank()) {
            return Collections.emptyList();
        }

        userRepository.findById(search.getUserId()).orElseThrow(()
                -> new UserNotFoundException(String.format("Пользователь с id: %s не обнаружен", search.getUserId())));
        Pageable page = PageRequest.of(search.getFrom(), search.getSize());
        return CommentMapper.commentsToCommentResponseDtoList(commentRepository.searchByText(search.getItemId(), search.getText(), page));
    }

    private List<ItemResponseDto> connectBookingsAndComments(List<Booking> bookings, List<Item> items,
                                                             List<Comment> comments, LocalDateTime now) {
        Map<Long, List<Booking>> bookingMap = bookings.stream().collect(Collectors.groupingBy(b -> b.getItem().getId()));
        Map<Long, List<Comment>> commentMap = comments.stream().collect(Collectors.groupingBy(c -> c.getItem().getId()));
        List<ItemResponseDto> result = new ArrayList<>();

        for (Item item : items) {
            BookingShortDto nextBooking = bookingMap.getOrDefault(item.getId(), Collections.emptyList()).stream()
                    .filter(b -> b.getStartDate().isAfter(now))
                    .sorted(Comparator.comparing(Booking::getStartDate, Comparator.naturalOrder()))
                    .map(BookingMapper::bookingToBookingShortDto)
                    .findFirst().orElse(null);

            BookingShortDto lastBooking = bookingMap.getOrDefault(item.getId(), Collections.emptyList()).stream()
                    .filter(b -> b.getStartDate().isBefore(now))
                    .sorted(Comparator.comparing(Booking::getStartDate, Comparator.reverseOrder()))
                    .map(BookingMapper::bookingToBookingShortDto)
                    .findFirst().orElse(null);

            List<CommentResponseDto> responseComments = CommentMapper.commentsToCommentResponseDtoList(commentMap.getOrDefault(item.getId(),
                    Collections.emptyList()));

            result.add(ItemMapper.itemToItemResponseDto(item, nextBooking, lastBooking, responseComments));
        }
        return result;
    }

    private void checkOwner(Item item, Long ownerId) {
        User owner = item.getOwner();

        if (owner == null || !ownerId.equals(owner.getId())) {
            throw new ItemUpdatingException(
                    String.format("Пользователь с id: %s не является владельцем вещи %s", ownerId, item.getName()));
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
}