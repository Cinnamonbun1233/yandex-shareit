package ru.practicum.shareit.request.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.model.RequestItem;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@DataJpaTest
class RequestItemRepositoryTest {
    @Autowired
    private TestEntityManager testEntityManager;
    @Autowired
    private RequestItemRepository repository;

    private static User getUser(String email) {
        return User.builder()
                .name("Dima")
                .email(email)
                .build();
    }

    private static RequestItem getRequest(User user) {
        return RequestItem.builder()
                .description("Предмет невероятной красоты")
                .requestor(user)
                .created(LocalDateTime.now())
                .build();
    }

    @Test
    void findAllByRequestorIdShouldReturnRequestsWhenUserIdIsCorrect() {
        // given
        User userOne = getUser("dima@yandex.ru");
        User userTwo = getUser("fima@yandex.ru");

        testEntityManager.persist(userOne);
        testEntityManager.persist(userTwo);

        RequestItem requestOne = getRequest(userOne);
        testEntityManager.persist(requestOne);
        // when
        List<RequestItem> requests = repository.findAllByRequestorId(userOne.getId());
        // then
        assertThat(requests, hasSize(1));
        assertThat(requests, hasItem(allOf(
                hasProperty("description", containsString("Предмет невероятной красоты")),
                hasProperty("id", equalTo(requestOne.getId())),
                hasProperty("requestor", equalTo(userOne)),
                hasProperty("created", notNullValue())
        )));
    }

    @Test
    void findAllByRequestorIdShouldReturnEmptyResultWhenUserIdIsInCorrect() {
        // given
        User userOne = getUser("dima@yandex.ru");
        User userTwo = getUser("fima@yandex.ru");

        testEntityManager.persist(userOne);
        testEntityManager.persist(userTwo);

        RequestItem requestOne = getRequest(userOne);
        testEntityManager.persist(requestOne);
        Long incorrectId = 1000L;
        // when
        List<RequestItem> requests = repository.findAllByRequestorId(incorrectId);
        // then
        assertThat(requests, empty());
    }

    @Test
    void findAllPagedShouldReturnRequestsWhenUserIsNotRequestor() {
        // given
        User userOne = getUser("dima@yandex.ru");
        User userTwo = getUser("fima@yandex.ru");

        testEntityManager.persist(userOne);
        testEntityManager.persist(userTwo);

        RequestItem requestOne = getRequest(userOne);
        testEntityManager.persist(requestOne);
        Pageable page = PageRequest.of(0, 10);
        // when
        List<RequestItem> requestsTwo = repository.findAllPaged(page, userTwo.getId()).getContent();
        // then
        assertThat(requestsTwo, hasSize(1));
        assertThat(requestsTwo, hasItem(allOf(
                hasProperty("description", containsString("Предмет невероятной красоты")),
                hasProperty("id", equalTo(requestOne.getId())),
                hasProperty("requestor", equalTo(userOne)),
                hasProperty("created", notNullValue())
        )));
    }

    @Test
    void findAllPagedShouldReturnEmptyListWhenUserIsRequestor() {
        // given
        User userOne = getUser("dima@yandex.ru");
        User userTwo = getUser("fima@yandex.ru");

        testEntityManager.persist(userOne);
        testEntityManager.persist(userTwo);

        RequestItem requestOne = getRequest(userOne);
        testEntityManager.persist(requestOne);
        Pageable page = PageRequest.of(0, 10);
        // when
        List<RequestItem> requests = repository.findAllPaged(page, userOne.getId()).getContent();
        // then
        assertThat(requests, empty());
    }
}