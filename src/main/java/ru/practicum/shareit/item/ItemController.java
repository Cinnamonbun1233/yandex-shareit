package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemServiceImpl;

    @PostMapping
    public ItemShortDto addItem(@RequestBody @Validated(ItemRequestDto.NewItem.class) ItemRequestDto itemRequestDto,
                                @RequestHeader("X-Sharer-User-Id") @NotNull Long ownerId) {
        return itemServiceImpl.addNewItem(itemRequestDto, ownerId);
    }

    @PatchMapping("/{id}")
    public ItemShortDto updateItem(@Validated(ItemRequestDto.UpdateItem.class) @RequestBody ItemRequestDto itemRequestDto,
                                   @RequestHeader("X-Sharer-User-Id") @NotNull Long ownerId,
                                   @PathVariable("id") Long itemId) {
        itemRequestDto.setId(itemId);
        return itemServiceImpl.updateItem(itemRequestDto, ownerId);
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

    @PostMapping("/{itemId}/comment")
    public CommentResponseDto addComment(@PathVariable Long itemId,
                                         @RequestBody @Valid CommentRequestDto dto,
                                         @RequestHeader("X-Sharer-User-Id") @NotNull Long userId) {
        return itemServiceImpl.addComment(itemId, dto, userId);
    }
}
