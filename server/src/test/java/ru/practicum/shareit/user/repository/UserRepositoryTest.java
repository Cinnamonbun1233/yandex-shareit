package ru.practicum.shareit.user.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.user.model.QUser;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserRepositoryTest {
    private final UserRepository userRepository;
    private final TestEntityManager testEntityManager;

    private static User getUser(String email) {
        return User.builder()
                .name("Дима")
                .email(email)
                .build();
    }

    @Test
    void deleteUserShouldDeleteUserWhenIdIsCorrect() {
        User userOne = getUser("dima@yandex.ru");
        User userTwo = getUser("fima@yandex.ru");
        testEntityManager.persist(userOne);
        testEntityManager.persist(userTwo);
        EntityManager entityManager = testEntityManager.getEntityManager();

        userRepository.deleteById(userOne.getId());
        List<User> resultList = entityManager.createQuery("SELECT u FROM User AS u").getResultList();

        assertThat(resultList, hasSize(1));
        assertThat(resultList, hasItem(allOf(
                hasProperty("id", equalTo(userTwo.getId())),
                hasProperty("name", equalTo(userTwo.getName()))
        )));
    }

    @Test
    void deleteUserShouldThrowDataAccessExceptionWhenIdIsInCorrect() {
        User userOne = getUser("dima@yandex.ru");
        User userTwo = getUser("fima@yandex.ru");
        testEntityManager.persist(userOne);
        testEntityManager.persist(userTwo);
        EntityManager entityManager = testEntityManager.getEntityManager();
        List<User> resultList = entityManager.createQuery("SELECT u FROM User AS u").getResultList();

        assertThrows(org.springframework.dao.EmptyResultDataAccessException.class, ()
                -> userRepository.deleteById(100L));
        assertThat(resultList, hasSize(2));
    }

    @Test
    void saveUserQUserTest() {
        User userOne = getUser("dima@yandex.ru");
        userRepository.save(userOne);

        List<User> result = userRepository.findAll(QUser.user.id.eq(userOne.getId()), Pageable.unpaged()).getContent();
        assertThat(result, not(empty()));
        assertThat(result.get(0).getId(), equalTo(userOne.getId()));
    }

    @Test
    void saveUserWithDuplicateEmailShouldThrowEx() {
        User userOne = getUser("dima@yandex.ru");
        userRepository.save(userOne);
        User userTwo = getUser("dima@yandex.ru");

        assertThrows(org.springframework.dao.DataIntegrityViolationException.class, ()
                -> userRepository.save(userTwo));
    }

    @Test
    void findByIdShouldReturnNullWhenUserNotFound() {
        User userOne = getUser("dima@yandex.ru");
        User savedUser = userRepository.save(userOne);
        Long userId = savedUser.getId();

        Optional<User> result = userRepository.findById(++userId);

        assertTrue(result.isEmpty());
    }
}