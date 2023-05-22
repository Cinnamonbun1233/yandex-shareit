package ru.practicum.shareit.user.dto;

import lombok.*;
import ru.practicum.shareit.validation.Create;
import ru.practicum.shareit.validation.Update;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class UserDTO {
    private Long id;
    @NotBlank(groups = Create.class, message = "Получен пользователь с пустым именем")
    private String name;
    @Email(groups = {Create.class, Update.class}, message = "Полуен пользователь с некорректным email")
    @NotEmpty(groups = Create.class, message = "Получен пользователь без email")
    @EqualsAndHashCode.Include
    private String email;
}