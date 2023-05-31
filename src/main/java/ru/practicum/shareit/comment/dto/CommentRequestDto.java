package ru.practicum.shareit.comment.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Null;
import java.time.LocalDateTime;

//DONE!!!
@Data
@Value
@Builder
@Jacksonized
public class CommentRequestDto {
    @Null
    Long id;
    @NotBlank
    String text;
    Item item;
    User author;
    @Builder.Default
    LocalDateTime created = LocalDateTime.now();
}