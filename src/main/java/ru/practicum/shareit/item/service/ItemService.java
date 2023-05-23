package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto getItemById(Long id);

    List<ItemDto> getUserItems(Long userId);

    ItemDto createNewItem(Long userId, Item item);

    ItemDto updateItem(Long id, Item item, Long ownerId);

    void deleteItemById(Long id);

    List<ItemDto> search(String text);
}