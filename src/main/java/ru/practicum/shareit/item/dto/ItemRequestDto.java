package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

@Data
@Builder
public class ItemRequestDto {
    public interface NewItem {
    }

    public interface UpdateItem {
    }

    @Null(groups = {NewItem.class})
    private Long id;
    @NotBlank(message = "Имя вещи не может быть пустым", groups = {NewItem.class})
    private String name;
    @NotBlank(message = "Поле описания не должно быть пустым", groups = {NewItem.class})
    private String description;
    @NotNull(message = "Поле доступность к аренде должно присутствовать", groups = {NewItem.class})
    private Boolean available;
}
