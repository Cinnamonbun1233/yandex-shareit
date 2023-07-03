package ru.practicum.shareit.user.mapper;

import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserShortResponseDto;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

public class UserMapper {
    public static UserRequestDto userToUserRequestDto(User user) {
        return UserRequestDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public static List<UserRequestDto> usersToUserRequestDtoList(Iterable<User> users) {
        List<UserRequestDto> userRequestDtoList = new ArrayList<>();

        for (User user : users) {
            userRequestDtoList.add(userToUserRequestDto(user));
        }

        return userRequestDtoList;
    }

    public static UserShortResponseDto userToUserShortResponseDto(User user) {
        return UserShortResponseDto.builder()
                .id(user.getId())
                .name(user.getName())
                .build();
    }

    public static User userRequestDtoToUser(UserRequestDto userRequestDto) {
        return User.builder()
                .name(userRequestDto.getName())
                .email(userRequestDto.getEmail())
                .build();
    }
}