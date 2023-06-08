package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.comment.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

public class ItemMapper {
    public static ItemRequestDto itemToItemRequestDto(Item item) {
        return ItemRequestDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }

    public static List<ItemRequestDto> itemToItemRequestDto(Iterable<Item> items) {
        List<ItemRequestDto> itemRequestDtoList = new ArrayList<>();
        for (Item item : items) {
            itemRequestDtoList.add(itemToItemRequestDto(item));
        }
        return itemRequestDtoList;
    }

    public static ItemShortDto itemToItemShortDto(Item item) {
        return ItemShortDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }

    public static ItemResponseDto itemToItemResponseDto(Item item, BookingShortDto next, BookingShortDto last) {
        return ItemResponseDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .nextBooking(next)
                .lastBooking(last)
                .build();
    }

    public static ItemResponseDto itemToItemResponseDto(Item item,
                                                        BookingShortDto next,
                                                        BookingShortDto last,
                                                        List<CommentResponseDto> comments) {
        return ItemResponseDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .nextBooking(next)
                .lastBooking(last)
                .comments(comments)
                .build();
    }

    public static List<ItemResponseDto> itemToItemResponseDto(List<Item> items) {
        List<ItemResponseDto> itemResponseDtoList = new ArrayList<>();
        for (Item item : items) {
            itemResponseDtoList.add(itemToItemResponseDto(item, null, null));
        }
        return itemResponseDtoList;
    }

    public static Item itemRequestDtoToItem(ItemRequestDto itemRequestDto, User owner) {
        return Item.builder()
                .name(itemRequestDto.getName())
                .description(itemRequestDto.getDescription())
                .available(itemRequestDto.getAvailable())
                .owner(owner)
                .build();
    }
}