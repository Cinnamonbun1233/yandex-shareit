package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemStorage {
    List<ItemDto> findAll(long userId);

    Optional<ItemDto> findItem(long itemId);

    Optional<ItemDto> findItemForUpdate(long userId, long itemId);

    List<ItemDto> searchItem(String text);

    ItemDto create(long userId, ItemDto itemDto);

    ItemDto update(long userId, long itemId, Item item);
}