package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.validation.CreateItem;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

@Data
@Builder
public class ItemRequestDto {
    @Null(groups = CreateItem.class)
    private Long id;
    @NotBlank(groups = CreateItem.class, message = "Получен предмет с пустным названием")
    private String name;
    @NotBlank(groups = CreateItem.class, message = "Получен предмет с пустным описанием")
    private String description;
    @NotNull(groups = CreateItem.class, message = "Получен предмет без статуса доступности")
    private Boolean available;
}