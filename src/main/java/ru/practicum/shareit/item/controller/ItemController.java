package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.dto.CommentRequestDto;
import ru.practicum.shareit.comment.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.validation.NewItem;
import ru.practicum.shareit.item.validation.UpdateItem;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemServiceImpl;

    @PostMapping
    public ItemShortDto createNewItem(@RequestBody @Validated(NewItem.class) ItemRequestDto itemRequestDto,
                                      @RequestHeader("X-Sharer-User-Id") @NotNull Long ownerId) {
        return itemServiceImpl.createNewItem(itemRequestDto, ownerId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentResponseDto createNewComment(@PathVariable Long itemId,
                                               @RequestBody @Valid CommentRequestDto commentRequestDto,
                                               @RequestHeader("X-Sharer-User-Id") @NotNull Long userId) {
        return itemServiceImpl.createNewComment(itemId, commentRequestDto, userId);
    }

    @GetMapping("/{id}")
    public ItemResponseDto getItemById(@RequestHeader("X-Sharer-User-Id") @NotNull Long userId,
                                       @PathVariable("id") Long itemId) {
        return itemServiceImpl.getItemById(userId, itemId);
    }

    @GetMapping
    public List<ItemResponseDto> getItemsByOwner(@RequestHeader("X-Sharer-User-Id") @NotNull Long userId) {
        return itemServiceImpl.getItemsByOwner(userId);
    }

    @PatchMapping("/{id}")
    public ItemShortDto updateItemById(@Validated(UpdateItem.class) @RequestBody ItemRequestDto itemRequestDto,
                                       @RequestHeader("X-Sharer-User-Id") @NotNull Long ownerId,
                                       @PathVariable("id") Long itemId) {
        itemRequestDto.setId(itemId);
        return itemServiceImpl.updateItem(itemRequestDto, ownerId);
    }

    @GetMapping("/search")
    public List<ItemRequestDto> search(@RequestParam String text, @RequestHeader("X-Sharer-User-Id") @NotNull Long userId) {
        return itemServiceImpl.search(text, userId);
    }

    @GetMapping("/{itemId}/comment/search")
    public List<CommentResponseDto> searchCommentsByText(@PathVariable Long itemId,
                                                         @RequestHeader("X-Sharer-User-Id") @NotNull Long userId,
                                                         @RequestParam @NotBlank String text) {
        return itemServiceImpl.searchCommentsByText(itemId, userId, text);
    }
}