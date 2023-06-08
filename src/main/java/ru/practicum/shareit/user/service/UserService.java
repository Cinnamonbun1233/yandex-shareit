package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserRequestDto;

import java.util.List;

public interface UserService {
    List<UserRequestDto> getAllUsers();

    UserRequestDto saveUser(UserRequestDto userRequestDto);

    UserRequestDto updateUser(UserRequestDto userRequestDto, Long id);

    void deleteUser(Long id);

    UserRequestDto getUserById(Long id);
}
