package ru.practicum.shareit.user.dto;

import lombok.*;
import ru.practicum.shareit.validation.Create;
import ru.practicum.shareit.validation.Update;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Value
@Builder
public class UserDto {
    Long id;
    @NotBlank(groups = Create.class, message = "Получен пользователь с пустым именем")
    String name;
    @Email(groups = {Create.class, Update.class}, message = "Полуен пользователь с некорректным email")
    @NotEmpty(groups = Create.class, message = "Получен пользователь без email")
    String email;
}