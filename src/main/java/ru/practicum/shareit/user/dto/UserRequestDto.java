package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Null;

@Data
@Builder
public class UserRequestDto {
    public interface NewUser {
    }

    public interface UpdateUser {
    }

    @Null(groups = NewUser.class)
    private Long id;
    @NotBlank(groups = {NewUser.class})
    private String name;
    @NotBlank(groups = {NewUser.class})
    @Email(groups = {UpdateUser.class, NewUser.class})
    private String email;
}
