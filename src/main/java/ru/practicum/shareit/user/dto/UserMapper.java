package ru.practicum.shareit.user.dto;

import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

public class UserMapper {
    public static UserRequestDto userToDto(User user) {
        return UserRequestDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public static List<UserRequestDto> userToDto(Iterable<User> users) {
        List<UserRequestDto> dtos = new ArrayList<>();
        for (User user : users) {
            dtos.add(userToDto(user));
        }
        return dtos;
    }

    public static UserShortResponseDto toUserShort(User user) {
        return UserShortResponseDto.builder()
                .id(user.getId())
                .name(user.getName())
                .build();
    }

    public static User dtoToUser(UserRequestDto userRequestDto) {
        return User.builder()
                .name(userRequestDto.getName())
                .email(userRequestDto.getEmail())
                .build();
    }
}
