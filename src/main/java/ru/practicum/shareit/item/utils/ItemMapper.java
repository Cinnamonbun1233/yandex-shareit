package ru.practicum.shareit.item.utils;

import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;


public class ItemMapper {
    public static ItemRequestDto itemToDto(Item item) {
        return ItemRequestDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }

    public static List<ItemRequestDto> itemToDto(Iterable<Item> items) {
        List<ItemRequestDto> dtos = new ArrayList<>();
        for (Item item : items) {
            dtos.add(itemToDto(item));
        }
        return dtos;
    }

    public static ItemShortDto toItemShort(Item item) {
        return ItemShortDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }

    public static ItemResponseDto toItemResponseDto(Item item, BookingShortDto next, BookingShortDto last) {
        return ItemResponseDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .nextBooking(next)
                .lastBooking(last)
                .build();
    }

    public static ItemResponseDto toItemResponseDto(Item item,
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

    public static List<ItemResponseDto> toItemResponseDto(List<Item> items) {
        List<ItemResponseDto> dtos = new ArrayList<>();
        for (Item item : items) {
            dtos.add(toItemResponseDto(item, null, null));
        }
        return dtos;
    }

    public static Item dtoToItem(ItemRequestDto itemRequestDto, User owner) {
        return Item.builder()
                .name(itemRequestDto.getName())
                .description(itemRequestDto.getDescription())
                .available(itemRequestDto.getAvailable())
                .owner(owner)
                .build();
    }
}
