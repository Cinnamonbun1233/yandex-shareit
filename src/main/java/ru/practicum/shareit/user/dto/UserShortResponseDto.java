package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserShortResponseDto {
    private Long id;
    private String name;
}
