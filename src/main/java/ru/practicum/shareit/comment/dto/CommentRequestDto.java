package ru.practicum.shareit.comment.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.jackson.Jacksonized;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

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
    private Item item;
    private User author;
    @Builder.Default
    private LocalDateTime created = LocalDateTime.now();
}