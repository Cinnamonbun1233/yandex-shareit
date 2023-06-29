package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemShortResponseDto createNewItem(@RequestBody ItemRequestDto itemRequestDto,
                                              @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return itemService.createNewItem(itemRequestDto, ownerId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentResponseDto createNewComment(@PathVariable Long itemId,
                                               @RequestBody CommentRequestDto commentRequestDto,
                                               @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.createNewComment(itemId, commentRequestDto, userId);
    }

    @GetMapping
    public List<ItemResponseDto> getAllItemsByUserId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                     @RequestParam(required = false, defaultValue = "0") int from,
                                                     @RequestParam(required = false, defaultValue = "10") int size) {
        return itemService.getAllItemsByUserId(userId, from, size);
    }

    @GetMapping("/{id}")
    public ItemResponseDto getItemByUserId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                           @PathVariable("id") Long itemId) {
        return itemService.getItemByUserId(userId, itemId);
    }

    @PatchMapping("/{id}")
    public ItemShortResponseDto updateItemByUserId(@RequestBody ItemRequestDto itemRequestDto,
                                                   @RequestHeader("X-Sharer-User-Id") Long ownerId,
                                                   @PathVariable("id") Long itemId) {
        itemRequestDto.setId(itemId);
        return itemService.updateItemByUserId(itemRequestDto, ownerId);
    }

    @GetMapping("/search")
    public List<ItemRequestDto> search(@RequestParam String text,
                                       @RequestHeader("X-Sharer-User-Id") Long userId,
                                       @RequestParam(required = false, defaultValue = "0") int from,
                                       @RequestParam(required = false, defaultValue = "10") int size) {
        return itemService.search(GetSearchItem.of(text, userId, from, size));
    }

    @GetMapping("/{itemId}/comment/search")
    public List<CommentResponseDto> searchCommentsByText(@PathVariable Long itemId,
                                                         @RequestHeader("X-Sharer-User-Id") Long userId,
                                                         @RequestParam String text,
                                                         @RequestParam(required = false, defaultValue = "0") int from,
                                                         @RequestParam(required = false, defaultValue = "10") int size) {
        return itemService.searchCommentsByText(GetSearchItem.of(text, userId, itemId, from, size));
    }
}