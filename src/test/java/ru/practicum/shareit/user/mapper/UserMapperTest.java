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
                .name("Dima")
                .email(email)
                .build();
    }

    @Test
    void userToUserRequestDto() {
        // given
        User user = getUser(1L, "dima@yandex.ru");
        // when
        UserRequestDto result = UserMapper.userToUserRequestDto(user);
        // then
        assertThat(result, notNullValue());
        assertThat(result.getId(), equalTo(user.getId()));
        assertThat(result.getName(), equalTo(user.getName()));
        assertThat(result.getEmail(), equalTo(user.getEmail()));
    }

    @Test
    void userToUserShortResponseDto() {
        // given
        User user = getUser(1L, "dima@yandex.ru");
        // when
        UserShortResponseDto result = UserMapper.userToUserShortResponseDto(user);
        // then
        assertThat(result, notNullValue());
        assertThat(result.getId(), equalTo(user.getId()));
        assertThat(result.getName(), equalTo(user.getName()));
    }

    @Test
    void userRequestDtoToUser() {
        // given
        UserRequestDto userRequestDto = UserRequestDto.builder()
                .name("dima")
                .email("dima@yandex.ru")
                .build();
        // when
        User user = UserMapper.userRequestDtoToUser(userRequestDto);
        // then
        assertThat(user, notNullValue());
        assertThat(user.getId(), nullValue());
        assertThat(user.getName(), equalTo(userRequestDto.getName()));
        assertThat(user.getEmail(), equalTo(userRequestDto.getEmail()));
    }
}