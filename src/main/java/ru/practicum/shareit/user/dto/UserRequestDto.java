package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Null;

@Data
@Builder
public class UserRequestDto {
    @Null(groups = CreateUser.class)
    private Long id;
    @NotBlank(groups = CreateUser.class, message = "Получен пользователь с пустым именем")
    private String name;
    @Email(groups = {UpdateUser.class, CreateUser.class}, message = "Полуен пользователь с некорректным email")
    @NotBlank(groups = CreateUser.class, message = "Получен пользователь с пустым email")
    private String email;

    public interface CreateUser {

    }

    public interface UpdateUser {

    }
}