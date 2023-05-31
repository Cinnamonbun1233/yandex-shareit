package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Value;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

//DONE!!!
@Data
@Value
@Builder
public class ItemRequestDto {
    Long id;
    @NotBlank
    String name;
    @NotNull
    LocalDateTime created;
}