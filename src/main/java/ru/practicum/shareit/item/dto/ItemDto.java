package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.validation.Create;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Value
@Builder
public class ItemDto {
    Long id;
    @NotBlank(groups = Create.class, message = "Получен предмет с пустным названием")
    String name;
    @NotBlank(groups = Create.class, message = "Получен предмет с пустным описанием")
    String description;
    @NotNull(groups = Create.class, message = "Получен предмет без статуса доступности")
    Boolean available;
}