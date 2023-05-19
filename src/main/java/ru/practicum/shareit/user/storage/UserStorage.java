package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {
    List<User> findAll();

    Optional<User> getById(long id);

    UserDto create(UserDto userDto);

    User update(long id, User user);

    void delete(long id);
}