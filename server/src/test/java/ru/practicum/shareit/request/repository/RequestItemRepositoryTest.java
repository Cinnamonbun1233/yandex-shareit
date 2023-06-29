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
    private RequestItemRepository requestItemRepository;

    private static User getUser(String email) {
        return User.builder()
                .name("Дима")
                .email(email)
                .build();
    }

    private static RequestItem getRequest(User user) {
        return RequestItem.builder()
                .description("Грабли для уборки листвы")
                .requestor(user)
                .created(LocalDateTime.now())
                .build();
    }

    @Test
    void findAllByRequestorIdShouldReturnRequestsWhenUserIdIsCorrect() {
        User userOne = getUser("dima@yandex.ru");
        User userTwo = getUser("fima@yandex.ru");

        testEntityManager.persist(userOne);
        testEntityManager.persist(userTwo);

        RequestItem requestOne = getRequest(userOne);
        testEntityManager.persist(requestOne);

        List<RequestItem> requests = requestItemRepository.findAllByRequestorId(userOne.getId());

        assertThat(requests, hasSize(1));
        assertThat(requests, hasItem(allOf(
                hasProperty("description", containsString("Грабли для уборки листвы")),
                hasProperty("id", equalTo(requestOne.getId())),
                hasProperty("requestor", equalTo(userOne)),
                hasProperty("created", notNullValue())
        )));
    }

    @Test
    void findAllByRequestorIdShouldReturnEmptyResultWhenUserIdIsInCorrect() {
        User userOne = getUser("dima@yandex.ru");
        User userTwo = getUser("fima@yandex.ru");

        testEntityManager.persist(userOne);
        testEntityManager.persist(userTwo);

        RequestItem requestOne = getRequest(userOne);
        testEntityManager.persist(requestOne);
        Long incorrectId = 1000L;

        List<RequestItem> requests = requestItemRepository.findAllByRequestorId(incorrectId);

        assertThat(requests, empty());
    }

    @Test
    void findAllPagedShouldReturnRequestsWhenUserIsNotRequestor() {
        User userOne = getUser("dima@yandex.ru");
        User userTwo = getUser("fima@yandex.ru");

        testEntityManager.persist(userOne);
        testEntityManager.persist(userTwo);

        RequestItem requestOne = getRequest(userOne);
        testEntityManager.persist(requestOne);
        Pageable page = PageRequest.of(0, 10);

        List<RequestItem> requestsTwo = requestItemRepository.findAllPaged(page, userTwo.getId()).getContent();

        assertThat(requestsTwo, hasSize(1));
        assertThat(requestsTwo, hasItem(allOf(
                hasProperty("description", containsString("Грабли для уборки листвы")),
                hasProperty("id", equalTo(requestOne.getId())),
                hasProperty("requestor", equalTo(userOne)),
                hasProperty("created", notNullValue())
        )));
    }

    @Test
    void findAllPagedShouldReturnEmptyListWhenUserIsRequestor() {
        User userOne = getUser("dima@yandex.ru");
        User userTwo = getUser("fima@yandex.ru");

        testEntityManager.persist(userOne);
        testEntityManager.persist(userTwo);

        RequestItem requestOne = getRequest(userOne);
        testEntityManager.persist(requestOne);
        Pageable page = PageRequest.of(0, 10);

        List<RequestItem> requests = requestItemRepository.findAllPaged(page, userOne.getId()).getContent();

        assertThat(requests, empty());
    }
}