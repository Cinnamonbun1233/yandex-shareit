package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.*;

import java.util.List;

public interface ItemService {
    ItemShortResponseDto createNewItem(ItemRequestDto itemRequestDto, Long ownerId);

    CommentResponseDto createNewComment(Long itemId, CommentRequestDto dto, Long userId);

    ItemResponseDto getItemById(Long userId, Long itemId);

    List<ItemResponseDto> getAllItemsById(Long ownerId, int from, int size);

    ItemShortResponseDto updateItemById(ItemRequestDto itemRequestDto, Long ownerId);

    List<ItemRequestDto> search(GetSearchItem search);

    List<CommentResponseDto> searchCommentsByText(GetSearchItem search);
}