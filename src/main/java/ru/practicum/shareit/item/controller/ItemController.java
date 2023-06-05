package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.dto.CommentRequestDto;
import ru.practicum.shareit.comment.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemShortResponseDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.validation.CreateItem;
import ru.practicum.shareit.validation.UpdateItem;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemShortResponseDto createNewItem(@RequestBody @Validated(CreateItem.class) ItemRequestDto itemRequestDto,
                                              @RequestHeader("X-Sharer-User-Id") @NotNull Long ownerId) {
        return itemService.createNewItem(itemRequestDto, ownerId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentResponseDto createNewComment(@PathVariable Long itemId,
                                               @RequestBody @Valid CommentRequestDto commentRequestDto,
                                               @RequestHeader("X-Sharer-User-Id") @NotNull Long userId) {
        return itemService.createNewComment(itemId, commentRequestDto, userId);
    }

    @GetMapping("/{id}")
    public ItemResponseDto getItemById(@RequestHeader("X-Sharer-User-Id") @NotNull Long userId,
                                       @PathVariable("id") Long itemId) {
        return itemService.getItemById(userId, itemId);
    }

    @GetMapping
    public List<ItemResponseDto> getItemsByOwner(@RequestHeader("X-Sharer-User-Id") @NotNull Long userId) {
        return itemService.getItemsByOwner(userId);
    }

    @PatchMapping("/{id}")
    public ItemShortResponseDto updateItemByOwner(@Validated(UpdateItem.class) @RequestBody ItemRequestDto itemRequestDto,
                                                  @RequestHeader("X-Sharer-User-Id") @NotNull Long ownerId,
                                                  @PathVariable("id") Long itemId) {
        itemRequestDto.setId(itemId);
        return itemService.updateItemByOwner(itemRequestDto, ownerId);
    }

    @GetMapping("/search")
    public List<ItemRequestDto> searchItemsByText(@RequestParam String text, @RequestHeader("X-Sharer-User-Id") @NotNull Long userId) {
        return itemService.searchItemsByText(text, userId);
    }

    @GetMapping("/{itemId}/comment/search")
    public List<CommentResponseDto> searchCommentsByText(@PathVariable Long itemId,
                                                         @RequestHeader("X-Sharer-User-Id") @NotNull Long userId,
                                                         @RequestParam @NotBlank String text) {
        return itemService.searchCommentsByText(itemId, userId, text);
    }
}