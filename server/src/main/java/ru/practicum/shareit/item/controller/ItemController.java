package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.validation.CreateItem;
import ru.practicum.shareit.item.validation.UpdateItem;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Validated
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

    @GetMapping
    public List<ItemResponseDto> getAllItemsById(@RequestHeader("X-Sharer-User-Id") @NotNull Long userId,
                                                 @RequestParam(required = false, defaultValue = "0") @PositiveOrZero int from,
                                                 @RequestParam(required = false, defaultValue = "10") @Positive int size) {
        return itemService.getAllItemsById(userId, from, size);
    }

    @GetMapping("/{id}")
    public ItemResponseDto getItemById(@RequestHeader("X-Sharer-User-Id") @NotNull Long userId,
                                       @PathVariable("id") Long itemId) {
        return itemService.getItemById(userId, itemId);
    }

    @PatchMapping("/{id}")
    public ItemShortResponseDto updateItemById(@Validated(UpdateItem.class) @RequestBody ItemRequestDto itemRequestDto,
                                               @RequestHeader("X-Sharer-User-Id") @NotNull Long ownerId,
                                               @PathVariable("id") Long itemId) {
        itemRequestDto.setId(itemId);
        return itemService.updateItemById(itemRequestDto, ownerId);
    }

    @GetMapping("/search")
    public List<ItemRequestDto> search(@RequestParam String text,
                                       @RequestHeader("X-Sharer-User-Id") @NotNull Long userId,
                                       @RequestParam(required = false, defaultValue = "0") @PositiveOrZero int from,
                                       @RequestParam(required = false, defaultValue = "10") @Positive int size) {
        return itemService.search(GetSearchItem.of(text, userId, from, size));
    }

    @GetMapping("/{itemId}/comment/search")
    public List<CommentResponseDto> searchCommentsByText(@PathVariable Long itemId,
                                                         @RequestHeader("X-Sharer-User-Id") @NotNull Long userId,
                                                         @RequestParam @NotBlank String text,
                                                         @RequestParam(required = false, defaultValue = "0") @PositiveOrZero int from,
                                                         @RequestParam(required = false, defaultValue = "10") @Positive int size) {
        return itemService.searchCommentsByText(GetSearchItem.of(text, userId, itemId, from, size));
    }
}
