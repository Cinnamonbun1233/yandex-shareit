package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.validation.Create;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collections;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @GetMapping("{itemId}")
    public ItemDTO getItemById(@PathVariable Long itemId) {
        log.info("Получени запрос на получение предмета с id: '{}'", itemId);
        return itemService.getItemById(itemId);
    }

    @GetMapping
    public List<ItemDTO> getUserItems(@RequestHeader(name = "X-Sharer-User-Id") Long ownerId) {
        log.info("Получен запрос на получение предметов пользователя с id: '{}'", ownerId);
        return itemService.getUserItems(ownerId);
    }

    @PostMapping
    public ItemDTO createNewItem(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                 @Validated(Create.class) @RequestBody ItemDTO itemDTO) {
        log.info("Получен запрос на добавление нового предмета");
        return itemService.createNewItem(userId, ItemMapper.dtoToItem(itemDTO));
    }

    @PatchMapping("/{itemId}")
    public ItemDTO updateItem(@PathVariable Long itemId, @RequestBody ItemDTO itemDTO,
                              @RequestHeader(name = "X-Sharer-User-Id") Long ownerId) {
        log.info("Получен запрос на обновление предмета с id: '{}'", itemId);
        return itemService.updateItem(itemId, ItemMapper.dtoToItem(itemDTO), ownerId);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItemById(@PathVariable Long itemId) {
        log.info("Получен запрос на удаление предмета с id: '{}'", itemId);
        itemService.deleteItemById(itemId);
    }

    @GetMapping("/search")
    public List<ItemDTO> search(@RequestParam String text) {
        log.info("Получен запрос на поиск предмета по названию или описанию: '{}'", text);
        if (text.isBlank()) {
            return Collections.emptyList();
        } else {
            return itemService.search(text);
        }
    }
}