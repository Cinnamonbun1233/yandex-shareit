package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.validation.CreateUser;
import ru.practicum.shareit.user.validation.UpdateUser;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Null;

@Data
@Builder
public class UserRequestDto {
    @Null(groups = {CreateUser.class})
    private Long id;
    @NotBlank(groups = {CreateUser.class})
    private String name;
    @NotBlank(groups = {CreateUser.class})
    @Email(groups = {UpdateUser.class, CreateUser.class})
    private String email;
}