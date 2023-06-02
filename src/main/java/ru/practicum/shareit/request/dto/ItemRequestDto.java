package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Value;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Value
@Builder
public class ItemRequestDto {
    Long id;
    @NotBlank(message = "Получен запрос с пустым именем")
    String name;
    @NotNull(message = "Получен запрос с пустой датой создания")
    LocalDateTime created;
}