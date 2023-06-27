package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.item.validation.CreateItem;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

@Getter
@Setter
@Builder
public class ItemRequestDto {
    @Null(groups = {CreateItem.class})
    private Long id;
    @NotBlank(groups = {CreateItem.class})
    private String name;
    @NotBlank(groups = {CreateItem.class})
    private String description;
    @NotNull(groups = {CreateItem.class})
    private Boolean available;
    private Long requestId;
}