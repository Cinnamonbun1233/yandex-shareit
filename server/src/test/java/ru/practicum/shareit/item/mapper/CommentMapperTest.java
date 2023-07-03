package ru.practicum.shareit.item.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class CommentMapperTest {
    private static CommentRequestDto getCommentRequestDto() {
        return CommentRequestDto.builder()
                .text("Отличные грабли")
                .build();
    }

    private static Comment getComment(Long id, User author, Item item) {
        return Comment.builder()
                .id(1L)
                .author(author)
                .item(item)
                .text("Отличные грабли")
                .created(LocalDateTime.now())
                .build();
    }

    private static User getUser(Long id, String email) {
        return User.builder()
                .id(id)
                .name("Дима")
                .email(email)
                .build();
    }

    private static Item getItem(User owner) {
        return Item.builder()
                .id(1L)
                .name("Грабли")
                .description("Грабли для уборки листвы")
                .available(true)
                .owner(owner)
                .build();
    }

    @Test
    void dtoToComment() {
        User user = getUser(1L, "dima@yandex.ru");
        User author = getUser(2L, "fima@yandex.ru");
        Item item = getItem(user);
        CommentRequestDto commentRequestDto = getCommentRequestDto();

        Comment result = CommentMapper.commentRequestDtoToComment(commentRequestDto, author, item);
        assertThat(result, notNullValue());
        assertThat(result, instanceOf(Comment.class));
        assertThat(result.getText(), equalTo(commentRequestDto.getText()));
        assertThat(result.getItem(), equalTo(item));
        assertThat(result.getAuthor(), equalTo(author));
    }

    @Test
    void toResponseDto() {
        User user = getUser(1L, "dima@yandex.ru");
        User author = getUser(2L, "fima@yandex.ru");
        Item item = getItem(user);
        Comment comment = getComment(1L, author, item);

        CommentResponseDto result = CommentMapper.commentToCommentResponseDto(comment);

        assertThat(result, notNullValue());
        assertThat(result.getId(), equalTo(comment.getId()));
        assertThat(result.getText(), equalTo(comment.getText()));
        assertThat(result.getAuthorName(), equalTo(comment.getAuthor().getName()));
        assertThat(result.getCreated(), notNullValue());
    }
}