package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {
    List<Item> getAllItems();

    Optional<Item> getItemById(Long id);

    Item createNewItem(Long userId, Item item);
    ItemDTO updateItem(Item item);

    void deleteItemById(Long id);

    List<Item> search(String text);
}