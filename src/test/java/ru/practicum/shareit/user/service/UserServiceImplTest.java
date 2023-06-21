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

    private static UserRequestDto getUserDto(Long id, String email) {
        return UserRequestDto.builder()
                .id(id)
                .name("Dima")
                .email(email)
                .build();
    }

    private static User getUser(Long id, String email) {
        return User.builder()
                .id(id)
                .name("Dima")
                .email(email)
                .build();
    }

    @Test
    void getAllUsers() {
        // given
        List<User> users = List.of(getUser(1L, "dima@yandex.ru"),
                getUser(2L, "fima@yandex.ru"));
        // when
        when(userRepository.findAll()).thenReturn(users);

        List<UserRequestDto> result = userService.getAllUsers();
        // then
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
        // given
        User user = getUser(1L, "dima@yandex.ru");
        UserRequestDto dto = getUserDto(1L, "dima@yandex.ru");
        // when
        when(userRepository.save(ArgumentMatchers.any()))
                .thenReturn(user);
        UserRequestDto result = userService.createNewUser(dto);
        // then
        assertThat(result.getId(), equalTo(user.getId()));
        assertThat(result.getName(), equalTo(user.getName()));
        verify(userRepository, times(1)).save(ArgumentMatchers.any());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void updateUser_shouldThrowUserNotFoundEx() {
        // given
        User user = getUser(1L, "dima@yandex.ru");
        UserRequestDto dto = getUserDto(1L, "dima@yandex.ru");
        // when
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        // then
        assertThrows(UserNotFoundException.class,
                () -> userService.updateUserById(dto, 1L));
        verify(userRepository, times(1)).findById(anyLong());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void updateUser_shouldReturnEmailWhenItsPresent() {
        // given
        User user = getUser(1L, "dima@yandex.ru");
        UserRequestDto dto = getUserDto(1L, "fima@yandex.ru");
        // when
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(userRepository.save(any()))
                .thenReturn(user);
        UserRequestDto result = userService.updateUserById(dto, 1L);
        // then
        assertThat(result.getEmail(), equalTo(dto.getEmail()));
    }

    @Test
    void updateUser_shouldReturnNameWhenItsPresent() {
        // given
        User user = getUser(1L, "dima@yandex.ru");
        UserRequestDto dto = getUserDto(1L, null);
        dto.setName("Fima");
        // when
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(userRepository.save(any()))
                .thenReturn(user);
        UserRequestDto result = userService.updateUserById(dto, 1L);
        // then
        assertThat(result.getEmail(), equalTo(user.getEmail()));
        assertThat(result.getName(), equalTo(dto.getName()));
    }

    @Test
    void deleteUser_shouldDeleteUser() {
        // when
        userService.deleteUserById(1L);
        // then
        verify(userRepository, times(1)).deleteById(anyLong());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void getUserById_shouldThrowUserNotFoundEx() {
        // when
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        // then
        assertThrows(UserNotFoundException.class,
                () -> userService.getUserById(1L));
        verify(userRepository, times(1)).findById(anyLong());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void getUserById_shouldReturnUser() {
        // given
        User user = getUser(1L, "dima@yandex.ru");
        UserRequestDto dto = getUserDto(1L, "dima@yandex.ru");
        // when
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        UserRequestDto result = userService.getUserById(1L);
        // then
        assertThat(result.getId(), equalTo(user.getId()));
        assertThat(result.getName(), equalTo(user.getName()));
        verify(userRepository, times(1)).findById(anyLong());
        verifyNoMoreInteractions(userRepository);
    }
}