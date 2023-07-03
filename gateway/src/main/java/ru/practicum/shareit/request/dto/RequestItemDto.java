package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class RequestItemDto {
    private Long id;
    @NotBlank
    private String description;
    @PastOrPresent
    private LocalDateTime created;
}