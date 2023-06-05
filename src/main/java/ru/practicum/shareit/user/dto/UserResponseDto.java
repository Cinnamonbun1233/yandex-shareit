package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Value;

@Data
@Builder
public class UserResponseDto {
    private Long id;
    private String name;
}