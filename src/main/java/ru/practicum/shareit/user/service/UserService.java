package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserRequestDto;

import java.util.List;

//DONE!!!
public interface UserService {
    UserRequestDto createNewUser(UserRequestDto userRequestDto);

    List<UserRequestDto> getAllUsers();

    UserRequestDto getUserById(Long id);

    UserRequestDto updateUserById(Long id, UserRequestDto userRequestDto);

    void deleteUserById(Long id);
}