package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserServiceImpl userService;

    private static User getUser(Long id, String email) {
        return User.builder()
                .id(id)
                .name("Дима")
                .email(email)
                .build();
    }

    private static UserRequestDto getUserDto(Long id, String email) {
        return UserRequestDto.builder()
                .id(id)
                .name("Дима")
                .email(email)
                .build();
    }

    @Test
    void getAllUsers() {
        List<User> users = List.of(getUser(1L, "dima@yandex.ru"),
                getUser(2L, "fima@yandex.ru"));

        when(userRepository.findAll()).thenReturn(users);

        List<UserRequestDto> result = userService.getAllUsers();

        assertThat(result, hasSize(2));
        for (User user : users) {
            assertThat(result, hasItem(allOf(
                    hasProperty("id", equalTo(user.getId())),
                    hasProperty("email", equalTo(user.getEmail())),
                    hasProperty("name", equalTo(user.getName()))
            )));
        }
    }

    @Test
    void saveUser() {
        User user = getUser(1L, "dima@yandex.ru");
        UserRequestDto userRequestDto = getUserDto(1L, "dima@yandex.ru");

        when(userRepository.save(ArgumentMatchers.any()))
                .thenReturn(user);
        UserRequestDto result = userService.createNewUser(userRequestDto);

        assertThat(result.getId(), equalTo(user.getId()));
        assertThat(result.getName(), equalTo(user.getName()));
        verify(userRepository, times(1)).save(ArgumentMatchers.any());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void updateUserShouldThrowUserNotFoundEx() {
        User user = getUser(1L, "dima@yandex.ru");
        UserRequestDto userRequestDto = getUserDto(1L, "dima@yandex.ru");

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, ()
                -> userService.updateUserById(userRequestDto, 1L));
        verify(userRepository, times(1)).findById(anyLong());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void updateUserShouldReturnEmailWhenItsPresent() {
        User user = getUser(1L, "dima@yandex.ru");
        UserRequestDto userRequestDto = getUserDto(1L, "fima@yandex.ru");

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(userRepository.save(any()))
                .thenReturn(user);
        UserRequestDto result = userService.updateUserById(userRequestDto, 1L);

        assertThat(result.getEmail(), equalTo(userRequestDto.getEmail()));
    }

    @Test
    void updateUserShouldReturnNameWhenItsPresent() {
        User user = getUser(1L, "dima@yandex.ru");
        UserRequestDto userRequestDto = getUserDto(1L, null);
        userRequestDto.setName("Фима");

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(userRepository.save(any()))
                .thenReturn(user);
        UserRequestDto result = userService.updateUserById(userRequestDto, 1L);

        assertThat(result.getEmail(), equalTo(user.getEmail()));
        assertThat(result.getName(), equalTo(userRequestDto.getName()));
    }

    @Test
    void deleteUserShouldDeleteUser() {
        userService.deleteUserById(1L);

        verify(userRepository, times(1)).deleteById(anyLong());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void getUserByIdShouldThrowUserNotFoundEx() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, ()
                -> userService.getUserById(1L));
        verify(userRepository, times(1)).findById(anyLong());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void getUserByIdShouldReturnUser() {
        User user = getUser(1L, "dima@yandex.ru");
        UserRequestDto userRequestDto = getUserDto(1L, "dima@yandex.ru");

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        UserRequestDto result = userService.getUserById(1L);

        assertThat(result.getId(), equalTo(user.getId()));
        assertThat(result.getName(), equalTo(user.getName()));
        verify(userRepository, times(1)).findById(anyLong());
        verifyNoMoreInteractions(userRepository);
    }
}