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
    private final TestEntityManager em;
    private final CommentRepository repository;
    private static Comment getComment(User author, Item item) {
        return Comment.builder()
                .text("very good item")
                .item(item)
                .author(author)
                .created(LocalDateTime.now())
                .build();
    }

    private static User getUser(String email) {
        return User.builder()
                .name("Alexandr")
                .email(email)
                .build();
    }

    private static Item getItem(User owner) {
        return Item.builder()
                .name("brush")
                .description("some brush")
                .available(true)
                .owner(owner)
                .build();
    }

    @Test
    void searchByText_shouldReturnCommentsWhenSearchRequestIsFound() {
        // given
        User owner = getUser("alex@mail.ru");
        User author = getUser("alexas@mail.ru");
        em.persist(owner);
        em.persist(author);
        Item item = getItem(owner);
        em.persist(item);
        Comment comment = getComment(author, item);
        em.persist(comment);
        Pageable page = PageRequest.of(0, 10);

        // when
        List<Comment> comments = repository.searchByText(item.getId(), "good item", page);
        // then
        assertThat(comments, hasSize(1));
        assertThat(comments, hasItem(comment));
    }

    @Test
    void searchByText_shouldReturnEmptyListWhenSearchRequestIsNotFound() {
        // given
        User owner = getUser("alex@mail.ru");
        User author = getUser("alexas@mail.ru");
        em.persist(owner);
        em.persist(author);
        Item item = getItem(owner);
        em.persist(item);
        Comment comment = getComment(author, item);
        em.persist(comment);
        Pageable page = PageRequest.of(0, 10);
        // when
        List<Comment> comments = repository.searchByText(item.getId(), "hj", page);
        // then
        assertThat(comments, empty());
    }

    @Test
    void findAllByItem_IdOrderByCreatedDesc_shouldReturnListOfComments() {
        // given
        User owner = getUser("alex@mail.ru");
        User author = getUser("alexas@mail.ru");
        em.persist(owner);
        em.persist(author);
        Item item = getItem(owner);
        em.persist(item);
        Comment comment = getComment(author, item);
        em.persist(comment);
        // when
        List<Comment> comments = repository.findAllByItem_IdOrderByCreatedDesc(item.getId());
        // then
        assertThat(comments, hasSize(1));
        assertThat(comments, hasItem(comment));
    }

    @Test
    void findAllByItem_IdOrderByCreatedDesc_shouldReturnEmptyListWhenCommentsNotFound() {
        // given
        User owner = getUser("alex@mail.ru");
        User author = getUser("alexas@mail.ru");
        em.persist(owner);
        em.persist(author);
        Item item = getItem(owner);
        em.persist(item);
        Comment comment = getComment(author, item);
        em.persist(comment);
        // when
        List<Comment> comments = repository.findAllByItem_IdOrderByCreatedDesc(10L);
        // then
        assertThat(comments, empty());
    }

    @Test
    void findAll_QueryDsl_shouldReturnListOfCommentsWhenQueryIsCorrect() {
        // given
        User owner = getUser("alex@mail.ru");
        User author = getUser("alexas@mail.ru");
        em.persist(owner);
        em.persist(author);
        Item item = getItem(owner);
        em.persist(item);
        Comment comment = getComment(author, item);
        em.persist(comment);
        Pageable page = PageRequest.of(0, 10);

        // when
        List<Comment> comments = repository.findAll(QComment.comment.id.eq(comment.getId()), page).getContent();
        // then
        assertThat(comments, hasSize(1));
        assertThat(comments, hasItem(comment));
    }

    @Test
    void findAll_QueryDsl_shouldReturnListOfCommentsWhenQueryIsNotCorrect() {
        // given
        User owner = getUser("alex@mail.ru");
        User author = getUser("alexas@mail.ru");
        em.persist(owner);
        em.persist(author);
        Item item = getItem(owner);
        em.persist(item);
        Comment comment = getComment(author, item);
        em.persist(comment);
        Pageable page = PageRequest.of(0, 10);

        // when
        List<Comment> comments = repository.findAll(
                QComment.comment.text.containsIgnoreCase("never found string"), page).getContent();
        // then
        assertThat(comments, empty());
    }

    @Test
    void findAllByItemIdIn_whenIdsExistAndCorrect() {
        // given
        User owner = getUser("alex@mail.ru");
        User author = getUser("alexas@mail.ru");
        em.persist(owner);
        em.persist(author);
        Item item = getItem(owner);
        em.persist(item);
        Comment comment = getComment(author, item);
        em.persist(comment);
        Pageable page = PageRequest.of(0, 10);
        List<Long> ids = List.of(item.getId());

        // when
        List<Comment> comments = repository.findAllByItemIdIn(ids);
        // then
        assertThat(comments, hasSize(1));
        assertThat(comments, hasItem(comment));
    }

    @Test
    void findAllByItemIdIn_shouldReturnEmptyListWhenArgumentIsEmptyList() {
        // given
        User owner = getUser("alex@mail.ru");
        User author = getUser("alexas@mail.ru");
        em.persist(owner);
        em.persist(author);
        Item item = getItem(owner);
        em.persist(item);
        Comment comment = getComment(author, item);
        em.persist(comment);
        // when
        List<Comment> comments = repository.findAllByItemIdIn(Collections.emptyList());
        // then
        assertThat(comments, empty());
    }

    @Test
    void findAllByItemIdIn_shouldNotThrowExceptionWhenArgumentIsEmptyList() {
        // given
        User owner = getUser("alex@mail.ru");
        User author = getUser("alexas@mail.ru");
        em.persist(owner);
        em.persist(author);
        Item item = getItem(owner);
        em.persist(item);
        Comment comment = getComment(author, item);
        em.persist(comment);
        // when + then
        Assertions.assertDoesNotThrow(() -> repository.findAllByItemIdIn(Collections.emptyList()));
    }
}