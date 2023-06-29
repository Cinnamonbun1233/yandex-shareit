package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.client.ItemClient;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.validation.CreateItem;
import ru.practicum.shareit.item.validation.UpdateItem;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/items")
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createNewItem(@RequestBody
                                                @Validated(CreateItem.class) ItemRequestDto itemRequestDto,
                                                @RequestHeader("X-Sharer-User-Id") @NotNull Long ownerId) {
        return itemClient.createNewItem(itemRequestDto, ownerId);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateItemByUserId(@Validated(UpdateItem.class)
                                                     @RequestBody ItemRequestDto itemRequestDto,
                                                     @RequestHeader("X-Sharer-User-Id") @NotNull Long ownerId,
                                                     @PathVariable("id") Long itemId) {
        return itemClient.updateItemByUserId(itemRequestDto, ownerId, itemId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getItemByUserId(@RequestHeader("X-Sharer-User-Id") @NotNull Long userId,
                                                  @PathVariable("id") Long itemId) {
        return itemClient.getItemByUserId(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getItemsByOwner(@RequestHeader("X-Sharer-User-Id") @NotNull Long userId,
                                                  @RequestParam(required = false, defaultValue = "0") @PositiveOrZero int from,
                                                  @RequestParam(required = false, defaultValue = "10") @Positive int size) {
        return itemClient.getItemsByOwner(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestParam String text,
                                         @RequestHeader("X-Sharer-User-Id") @NotNull Long userId,
                                         @RequestParam(required = false, defaultValue = "0") @PositiveOrZero int from,
                                         @RequestParam(required = false, defaultValue = "10") @Positive int size) {
        return itemClient.search(text, userId, from, size);
    }

    @GetMapping("/{itemId}/comment/search")
    public ResponseEntity<Object> searchCommentsByText(@PathVariable Long itemId,
                                                       @RequestHeader("X-Sharer-User-Id") @NotNull Long userId,
                                                       @RequestParam @NotBlank String text,
                                                       @RequestParam(required = false, defaultValue = "0") @PositiveOrZero int from,
                                                       @RequestParam(required = false, defaultValue = "10") @Positive int size) {
        return itemClient.searchCommentsByText(text, userId, itemId, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@PathVariable Long itemId,
                                             @RequestBody @Valid CommentRequestDto dto,
                                             @RequestHeader("X-Sharer-User-Id") @NotNull Long userId) {
        return itemClient.addComment(itemId, dto, userId);
    }
}