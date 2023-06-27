package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserRequestDto;

import java.util.List;

public interface UserService {
    UserRequestDto createNewUser(UserRequestDto userRequestDto);

    List<UserRequestDto> getAllUsers();

    UserRequestDto getUserById(Long id);

    UserRequestDto updateUserById(UserRequestDto userRequestDto, Long id);

    void deleteUserById(Long id);
}