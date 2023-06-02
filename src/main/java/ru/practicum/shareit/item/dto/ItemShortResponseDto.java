package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Value;

@Data
@Value
@Builder
public class ItemShortResponseDto {
    Long id;
    String name;
    String description;
    Boolean available;
}