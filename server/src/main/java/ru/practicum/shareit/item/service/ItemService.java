package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.*;

import java.util.List;

public interface ItemService {
    ItemShortResponseDto createNewItem(ItemRequestDto itemRequestDto, Long ownerId);

    CommentResponseDto createNewComment(Long itemId, CommentRequestDto commentRequestDto, Long userId);

    List<ItemResponseDto> getAllItemsByUserId(Long ownerId, int from, int size);

    ItemResponseDto getItemByUserId(Long userId, Long itemId);

    ItemShortResponseDto updateItemByUserId(ItemRequestDto itemRequestDto, Long ownerId);

    List<ItemRequestDto> search(GetSearchItem search);

    List<CommentResponseDto> searchCommentsByText(GetSearchItem search);
}