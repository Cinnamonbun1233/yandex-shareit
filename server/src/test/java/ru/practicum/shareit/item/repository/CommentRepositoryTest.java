package ru.practicum.shareit.item.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.QComment;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class CommentRepositoryTest {
    private final TestEntityManager testEntityManager;
    private final CommentRepository commentRepository;

    private static Comment getComment(User author, Item item) {
        return Comment.builder()
                .text("Отличные грабли")
                .item(item)
                .author(author)
                .created(LocalDateTime.now())
                .build();
    }

    private static User getUser(String email) {
        return User.builder()
                .name("Дима")
                .email(email)
                .build();
    }

    private static Item getItem(User owner) {
        return Item.builder()
                .name("Грабли")
                .description("Грабли для убоорки листвы")
                .available(true)
                .owner(owner)
                .build();
    }

    @Test
    void searchByTextShouldReturnCommentsWhenSearchRequestIsFound() {
        User owner = getUser("dima@yandex.ru");
        User author = getUser("fima@yandex.ru");
        testEntityManager.persist(owner);
        testEntityManager.persist(author);
        Item item = getItem(owner);
        testEntityManager.persist(item);
        Comment comment = getComment(author, item);
        testEntityManager.persist(comment);
        Pageable page = PageRequest.of(0, 10);

        List<Comment> comments = commentRepository.searchByText(item.getId(), "Отличные грабли", page);

        assertThat(comments, hasSize(1));
        assertThat(comments, hasItem(comment));
    }

    @Test
    void searchByTextShouldReturnEmptyListWhenSearchRequestIsNotFound() {
        User owner = getUser("dima@yandex.ru");
        User author = getUser("fima@yandex.ru");
        testEntityManager.persist(owner);
        testEntityManager.persist(author);
        Item item = getItem(owner);
        testEntityManager.persist(item);
        Comment comment = getComment(author, item);
        testEntityManager.persist(comment);
        Pageable page = PageRequest.of(0, 10);

        List<Comment> comments = commentRepository.searchByText(item.getId(), "hj", page);

        assertThat(comments, empty());
    }

    @Test
    void findAllByItemIdOrderByCreatedDescShouldReturnListOfComments() {
        User owner = getUser("dima@yandex.ru");
        User author = getUser("fima@yandex.ru");
        testEntityManager.persist(owner);
        testEntityManager.persist(author);
        Item item = getItem(owner);
        testEntityManager.persist(item);
        Comment comment = getComment(author, item);
        testEntityManager.persist(comment);

        List<Comment> comments = commentRepository.findAllByItemIdOrderByCreatedDesc(item.getId());

        assertThat(comments, hasSize(1));
        assertThat(comments, hasItem(comment));
    }

    @Test
    void findAllByItemIdOrderByCreatedDescShouldReturnEmptyListWhenCommentsNotFound() {
        User owner = getUser("dima@yandex.ru");
        User author = getUser("fima@yandex.ru");
        testEntityManager.persist(owner);
        testEntityManager.persist(author);
        Item item = getItem(owner);
        testEntityManager.persist(item);
        Comment comment = getComment(author, item);
        testEntityManager.persist(comment);

        List<Comment> comments = commentRepository.findAllByItemIdOrderByCreatedDesc(10L);

        assertThat(comments, empty());
    }

    @Test
    void findAllQueryDslShouldReturnListOfCommentsWhenQueryIsCorrect() {
        User owner = getUser("dima@yandex.ru");
        User author = getUser("fima@yandex.ru");
        testEntityManager.persist(owner);
        testEntityManager.persist(author);
        Item item = getItem(owner);
        testEntityManager.persist(item);
        Comment comment = getComment(author, item);
        testEntityManager.persist(comment);
        Pageable page = PageRequest.of(0, 10);

        List<Comment> comments = commentRepository.findAll(QComment.comment.id.eq(comment.getId()), page).getContent();

        assertThat(comments, hasSize(1));
        assertThat(comments, hasItem(comment));
    }

    @Test
    void findAllQueryDslShouldReturnListOfCommentsWhenQueryIsNotCorrect() {
        User owner = getUser("dima@yandex.ru");
        User author = getUser("fima@yandex.ru");
        testEntityManager.persist(owner);
        testEntityManager.persist(author);
        Item item = getItem(owner);
        testEntityManager.persist(item);
        Comment comment = getComment(author, item);
        testEntityManager.persist(comment);
        Pageable page = PageRequest.of(0, 10);

        List<Comment> comments = commentRepository.findAll(
                QComment.comment.text.containsIgnoreCase("Так себе грабли"), page).getContent();

        assertThat(comments, empty());
    }

    @Test
    void findAllByItemIdInWhenIdsExistAndCorrect() {
        User owner = getUser("dima@yandex.ru");
        User author = getUser("fima@yandex.ru");
        testEntityManager.persist(owner);
        testEntityManager.persist(author);
        Item item = getItem(owner);
        testEntityManager.persist(item);
        Comment comment = getComment(author, item);
        testEntityManager.persist(comment);
        Pageable page = PageRequest.of(0, 10);
        List<Long> ids = List.of(item.getId());

        List<Comment> comments = commentRepository.findAllByItemIdIn(ids);

        assertThat(comments, hasSize(1));
        assertThat(comments, hasItem(comment));
    }

    @Test
    void findAllByItemIdInShouldReturnEmptyListWhenArgumentIsEmptyList() {
        User owner = getUser("dima@yandex.ru");
        User author = getUser("fima@yandex.ru");
        testEntityManager.persist(owner);
        testEntityManager.persist(author);
        Item item = getItem(owner);
        testEntityManager.persist(item);
        Comment comment = getComment(author, item);
        testEntityManager.persist(comment);

        List<Comment> comments = commentRepository.findAllByItemIdIn(Collections.emptyList());

        assertThat(comments, empty());
    }

    @Test
    void findAllByItemIdInShouldNotThrowExceptionWhenArgumentIsEmptyList() {
        User owner = getUser("dima@yandex.ru");
        User author = getUser("fima@yandex.ru");
        testEntityManager.persist(owner);
        testEntityManager.persist(author);
        Item item = getItem(owner);
        testEntityManager.persist(item);
        Comment comment = getComment(author, item);
        testEntityManager.persist(comment);

        Assertions.assertDoesNotThrow(() -> commentRepository.findAllByItemIdIn(Collections.emptyList()));
    }
}