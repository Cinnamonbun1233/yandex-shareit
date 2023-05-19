package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {

    List<User> findAll();

    User getById(long id);

    UserDto create(UserDto userDto);

    User update(long id, User user);

    void delete(long id);
}