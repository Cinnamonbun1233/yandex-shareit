package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.model.Comment;
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

    public static List<CommentResponseDto> commentsToCommentResponseDtoList(List<Comment> comments) {
        List<CommentResponseDto> commentResponseDtoList = new ArrayList<>();

        for (Comment comment : comments) {
            commentResponseDtoList.add(commentToCommentResponseDto(comment));
        }

        return commentResponseDtoList;
    }
}