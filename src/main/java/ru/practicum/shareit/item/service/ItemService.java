package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.*;

import java.util.List;

public interface ItemService {
    ItemShortDto addNewItem(ItemRequestDto itemRequestDto, Long ownerId);

    ItemShortDto updateItem(ItemRequestDto itemRequestDto, Long ownerId);

    ItemResponseDto getItemById(Long userId, Long itemId);

    List<ItemResponseDto> getItemsByOwner(Long ownerId);

    List<ItemRequestDto> search(String text, Long userId);

    List<CommentResponseDto> searchCommentsByText(Long itemId, Long userId, String text);

    CommentResponseDto addComment(Long itemId, CommentRequestDto dto, Long userId);
}
