package ru.practicum.shareit.item.service;

import ru.practicum.shareit.comment.dto.CommentRequestDto;
import ru.practicum.shareit.comment.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemShortResponseDto;

import java.util.List;

//DONE!!!
public interface ItemService {
    ItemShortResponseDto createNewItem(ItemRequestDto itemRequestDto, Long ownerId);

    CommentResponseDto createNewComment(Long itemId, CommentRequestDto dto, Long userId);

    ItemResponseDto getItemById(Long userId, Long itemId);

    List<ItemResponseDto> getItemsByOwner(Long ownerId);

    ItemShortResponseDto updateItemByOwner(ItemRequestDto itemRequestDto, Long ownerId);

    List<ItemRequestDto> searchItemsByText(String text, Long userId);

    List<CommentResponseDto> searchCommentsByText(Long itemId, Long userId, String text);
}