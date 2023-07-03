package ru.practicum.shareit.user.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserShortResponseDto;
import ru.practicum.shareit.user.model.User;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class UserMapperTest {
    private static User getUser(Long id, String email) {
        return User.builder()
                .id(id)
                .name("Дима")
                .email(email)
                .build();
    }

    @Test
    void userToDto() {
        User user = getUser(1L, "dima@yandex.ru");

        UserRequestDto result = UserMapper.userToUserRequestDto(user);

        assertThat(result, notNullValue());
        assertThat(result.getId(), equalTo(user.getId()));
        assertThat(result.getName(), equalTo(user.getName()));
        assertThat(result.getEmail(), equalTo(user.getEmail()));
    }

    @Test
    void toUserShort() {
        User user = getUser(1L, "dima@yandex.ru");

        UserShortResponseDto result = UserMapper.userToUserShortResponseDto(user);

        assertThat(result, notNullValue());
        assertThat(result.getId(), equalTo(user.getId()));
        assertThat(result.getName(), equalTo(user.getName()));
    }

    @Test
    void dtoToUser() {
        UserRequestDto userRequestDto = UserRequestDto.builder()
                .name("Дима")
                .email("dima@yandex.ru")
                .build();

        User user = UserMapper.userRequestDtoToUser(userRequestDto);

        assertThat(user, notNullValue());
        assertThat(user.getId(), nullValue());
        assertThat(user.getName(), equalTo(userRequestDto.getName()));
        assertThat(user.getEmail(), equalTo(userRequestDto.getEmail()));
    }
}