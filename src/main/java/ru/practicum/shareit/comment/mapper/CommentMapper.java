package ru.practicum.shareit.comment.mapper;

import ru.practicum.shareit.comment.dto.CommentRequestDto;
import ru.practicum.shareit.comment.dto.CommentResponseDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

public class CommentMapper {
    public static Comment commentRequestDtoToComment(CommentRequestDto commentRequestDto, User author, Item item) {
        return Comment.builder()
                .text(commentRequestDto.getText())
                .item(item)
                .author(author)
                .created(commentRequestDto.getCreated())
                .build();
    }

    public static CommentResponseDto commentToCommentResponseDto(Comment comment) {
        return CommentResponseDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated())
                .build();
    }

    public static List<CommentResponseDto> commentToCommentResponseDto(List<Comment> comments) {
        List<CommentResponseDto> dtos = new ArrayList<>();
        for (Comment comment : comments) {
            dtos.add(commentToCommentResponseDto(comment));
        }
        return dtos;
    }
}