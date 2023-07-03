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
    private final TestEntityManager testEntityManager;
    private final ItemRepository itemRepository;

    private static User getUser(String email) {
        return User.builder()
                .name("Дима")
                .email(email)
                .build();
    }

    private static Item getItem(User owner) {
        return Item.builder()
                .name("Грабли")
                .description("Грабли для уборки листвы")
                .available(true)
                .owner(owner)
                .build();
    }

    @Test
    void searchItemsByNameOrDescriptionShouldReturnResultWhenSearchRequestFound() {
        User owner = getUser("dima@yandex.ru");
        testEntityManager.persist(owner);
        Item item = getItem(owner);
        testEntityManager.persist(item);
        Pageable page = PageRequest.of(0, 10);

        List<Item> result = itemRepository.searchItemsByNameOrDescription("Гр", page);

        assertThat(result, hasSize(1));
        assertThat(result, hasItem(allOf(
                hasProperty("name", equalTo(item.getName())),
                hasProperty("id", equalTo(item.getId()))
        )));
    }

    @Test
    void searchItemsByNameOrDescriptionShouldReturnEmptyResultWhenSearchRequestNotFound() {
        User owner = getUser("dima@yandex.ru");
        testEntityManager.persist(owner);
        Item item = getItem(owner);
        testEntityManager.persist(item);
        Pageable page = PageRequest.of(0, 10);

        List<Item> result = itemRepository.searchItemsByNameOrDescription("Обли", page);

        assertThat(result, empty());
    }

    @Test
    void findAllByOwnerIdShouldReturnItemListWhenIdIsCorrect() {
        User owner = getUser("dima@yandex.ru");
        testEntityManager.persist(owner);
        Item item = getItem(owner);
        testEntityManager.persist(item);

        List<Item> result = itemRepository.findAllByOwnerId(owner.getId(), Pageable.unpaged());

        assertThat(result, hasItem(allOf(
                hasProperty("id", equalTo(item.getId()))
        )));
    }

    @Test
    void findAllByOwnerIdShouldReturnEmptyListWhenIdIsInCorrect() {

        User owner = getUser("dima@yandex.ru");
        testEntityManager.persist(owner);
        Item item = getItem(owner);
        testEntityManager.persist(item);
        Long incorrectId = owner.getId() + 10;

        List<Item> result = itemRepository.findAllByOwnerId(incorrectId, Pageable.unpaged());

        assertThat(result, empty());
    }
}