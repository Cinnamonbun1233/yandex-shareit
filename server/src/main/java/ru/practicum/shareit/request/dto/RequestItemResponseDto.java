package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.item.dto.ItemShortResponseDto;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class RequestItemResponseDto {
    private Long id;
    private String description;
    private LocalDateTime created;
    private List<ItemShortResponseDto> items;
}