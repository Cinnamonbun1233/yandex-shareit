package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.dto.ItemDTO;

import java.util.List;

public interface ItemService {
    ItemDTO getItemById(Long id);

    List<ItemDTO> getUserItems(Long userId);

    ItemDTO createNewItem(Long userId, Item item);

    ItemDTO updateItem(Long id, Item item, Long ownerId);

    void deleteItemById(Long id);

    List<ItemDTO> search(String text);
}