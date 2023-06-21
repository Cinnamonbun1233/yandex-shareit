package ru.practicum.shareit.item.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRepositoryTest {
    private final TestEntityManager em;
    private final ItemRepository repository;

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
    void searchItemsByNameOrDescription_shouldReturnResult_WhenSearchRequestFound() {
        // given
        User owner = getUser("alex@mail.ru");
        em.persist(owner);
        Item item = getItem(owner);
        em.persist(item);
        Pageable page = PageRequest.of(0, 10);
        // when
        List<Item> result = repository.searchItemsByNameOrDescription("br", page);
        // then
        assertThat(result, hasSize(1));
        assertThat(result, hasItem(allOf(
                hasProperty("name", equalTo(item.getName())),
                hasProperty("id", equalTo(item.getId()))
        )));
    }

    @Test
    void searchItemsByNameOrDescription_shouldReturnEmptyResult_WhenSearchRequestNotFound() {
        // given
        User owner = getUser("alex@mail.ru");
        em.persist(owner);
        Item item = getItem(owner);
        em.persist(item);
        Pageable page = PageRequest.of(0, 10);
        // when
        List<Item> result = repository.searchItemsByNameOrDescription("txt", page);
        // then
        assertThat(result, empty());
    }

    @Test
    void findAllByOwner_Id_shouldReturnItemList_WhenIdIsCorrect() {
        // given
        User owner = getUser("alex@mail.ru");
        em.persist(owner);
        Item item = getItem(owner);
        em.persist(item);
        // when
        List<Item> result = repository.findAllByOwner_Id(owner.getId(), Pageable.unpaged());
        // then
        assertThat(result, hasItem(allOf(
                hasProperty("id", equalTo(item.getId()))
        )));
    }

    @Test
    void findAllByOwner_Id_ShouldReturnEmptyList_WhenIdIsInCorrect() {
        // given
        User owner = getUser("alex@mail.ru");
        em.persist(owner);
        Item item = getItem(owner);
        em.persist(item);
        Long incorrectId = owner.getId() + 10;
        // when
        List<Item> result = repository.findAllByOwner_Id(incorrectId, Pageable.unpaged());
        // then
        assertThat(result, empty());
    }
}