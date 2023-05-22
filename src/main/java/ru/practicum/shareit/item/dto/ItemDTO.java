package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.validation.Create;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemDTO {
    private Long id;
    @NotBlank(groups = Create.class, message = "Получен предмет с пустным названием")
    private String name;
    @NotBlank(groups = Create.class, message = "Получен предмет с пустным описанием")
    private String description;
    @NotNull(groups = Create.class, message = "Получен предмет без статуса доступности")
    private Boolean available;
}