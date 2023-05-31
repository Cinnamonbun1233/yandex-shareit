package ru.practicum.shareit.comment.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Value;

import java.time.LocalDateTime;

//DONE!!!
@Data
@Value
@Builder
public class CommentResponseDto {
    Long id;
    String text;
    String authorName;
    LocalDateTime created;
}