package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Value;

@Data
@Value
@Builder
public class UserResponseDto {
    Long id;
    String name;
}