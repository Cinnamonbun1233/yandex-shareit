package ru.practicum.shareit.item.service;

import ru.practicum.shareit.comment.dto.CommentRequestDto;
import ru.practicum.shareit.comment.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemShortDto;

import java.util.List;

public interface ItemService {
    ItemShortDto createNewItem(ItemRequestDto itemRequestDto, Long ownerId);

    CommentResponseDto createNewComment(Long itemId, CommentRequestDto dto, Long userId);

    ItemResponseDto getItemById(Long userId, Long itemId);

    List<ItemResponseDto> getItemsByOwner(Long ownerId);

    ItemShortDto updateItem(ItemRequestDto itemRequestDto, Long ownerId);

    List<ItemRequestDto> search(String text, Long userId);

    List<CommentResponseDto> searchCommentsByText(Long itemId, Long userId, String text);
}