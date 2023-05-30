package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.ItemValidationException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemDto getItemById(Long id) {
        Item item = itemRepository.getItemById(id).orElseThrow(() ->
                new ItemNotFoundException("Предмет с id: '" + id + "' не найден"));
        return ItemMapper.itemToDto(item);
    }

    @Override
    public List<ItemDto> getUserItems(Long userId) {
        List<Item> userItems = itemRepository.getAllItems().stream()
                .filter(item -> item.getOwner().equals(userId))
                .collect(Collectors.toList());
        return userItems.stream()
                .map(ItemMapper::itemToDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto createNewItem(Long userId, Item item) {
        userRepository.getUserById(userId).orElseThrow(() ->
                new UserNotFoundException("Пользователь с id: '" + userId + "' не найден"));
        itemValidator(item);
        return ItemMapper.itemToDto(itemRepository.createNewItem(userId, item));
    }

    @Override
    public ItemDto updateItem(Long id, Item item, Long ownerId) {
        Item itemInMemory = itemRepository.getItemById(id).orElseThrow(()
                -> new UserNotFoundException("Предмет с id: '" + id + "' не найден"));
        if (itemPatcher(item, ownerId, itemInMemory)) {
            return itemRepository.updateItem(itemInMemory);
        } else {
            throw new ItemValidationException("Невозможно обновить предмет, пренадлежащий пользователю c id: '"
                    + ownerId + "'");
        }
    }

    @Override
    public List<ItemDto> search(String text) {
        return itemRepository.search(text).stream()
                .map(ItemMapper::itemToDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteItemById(Long id) {
        itemRepository.deleteItemById(id);
    }

    private boolean itemPatcher(Item item, Long ownerId, Item reciveItem) {
        if (Objects.equals(ownerId, reciveItem.getOwner())) {
            if (item.getName() != null && !item.getName().isBlank()) {
                reciveItem.setName(item.getName());
            }
            if (item.getDescription() != null && !item.getDescription().isBlank()) {
                reciveItem.setDescription(item.getDescription());
            }
            if (item.getAvailable() != null) {
                reciveItem.setAvailable(item.getAvailable());
            }
            return true;
        } else {
            return false;
        }
    }

    private void itemValidator(Item item) {
        if (itemRepository.getAllItems().contains(item)) {
            throw new ItemValidationException("Предмет: '" + item.getName() + "' уже существует");
        }
    }
}