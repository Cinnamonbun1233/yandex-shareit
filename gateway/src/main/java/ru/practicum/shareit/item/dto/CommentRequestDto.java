package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Null;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@Jacksonized
public class CommentRequestDto {
    @Null
    private Long id;
    @NotBlank
    private String text;
    @Builder.Default
    private LocalDateTime created = LocalDateTime.now();
}