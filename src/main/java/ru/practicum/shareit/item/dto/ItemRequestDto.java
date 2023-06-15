package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.item.validation.NewItem;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

@Getter
@Setter
@Builder
public class ItemRequestDto {
    @Null(groups = {NewItem.class})
    private Long id;
    @NotBlank(groups = {NewItem.class}, message = "Получен предмет с пустным названием")
    private String name;
    @NotBlank(groups = {NewItem.class}, message = "Получен предмет с пустным описанием")
    private String description;
    @NotNull(groups = {NewItem.class}, message = "Получен предмет без статуса доступности")
    private Boolean available;
}