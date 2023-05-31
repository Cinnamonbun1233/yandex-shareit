package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Value;

//DONE!!!
@Data
@Value
@Builder
public class UserResponseDto {
    Long id;
    String name;
}