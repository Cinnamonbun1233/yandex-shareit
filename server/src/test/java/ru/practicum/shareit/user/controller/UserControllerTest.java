package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private UserService userService;
    @Autowired
    private MockMvc mockMvc;

    private static UserRequestDto getUserRequestDto(String email) {
        return UserRequestDto.builder()
                .name("Дима")
                .email(email)
                .build();
    }

    private static UserRequestDto getUserResponseDto(String email) {
        return UserRequestDto.builder()
                .id(1L)
                .name("Дима")
                .email(email)
                .build();
    }

    @Test
    @SneakyThrows
    void createUserShouldReturnUser() {
        UserRequestDto requestDto = getUserRequestDto("dima@yandex.ru");
        UserRequestDto responseDto = getUserResponseDto("dima@yandex.ru");

        when(userService.createNewUser(any()))
                .thenReturn(responseDto);
        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id", is(responseDto.getId()), Long.class),
                        jsonPath("$.name", equalTo(responseDto.getName())),
                        jsonPath("$.email", equalTo(responseDto.getEmail()))
                );
    }

    @Test
    @SneakyThrows
    void getAllUsersShouldReturnUsersWhenRequestIsCorrect() {
        List<UserRequestDto> userRequestDtoList = List.of(getUserRequestDto("dima@yandex.ru"),
                getUserRequestDto("fima@yandex.ru"));

        when(userService.getAllUsers())
                .thenReturn(userRequestDtoList);

        mockMvc.perform(get("/users")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$", hasSize(2)),
                        jsonPath("$..email", hasItems("fima@yandex.ru", "dima@yandex.ru")),
                        jsonPath("$..name", hasSize(2))
                );
    }

    @Test
    @SneakyThrows
    void getUserByIdShouldReturnUserWhenRequestIsCorrect() {
        UserRequestDto userRequestDto = getUserRequestDto("fima@yandex.ru");

        when(userService.getUserById(anyLong()))
                .thenReturn(userRequestDto);

        mockMvc.perform(get("/users/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.name", equalTo(userRequestDto.getName())),
                        jsonPath("$.email", equalTo(userRequestDto.getEmail()))
                );
    }

    @Test
    @SneakyThrows
    void getUserByIdShouldReturnBadRequestPathVarIsNull() {
        UserRequestDto userRequestDto = getUserRequestDto("fima@yandex.ru");

        when(userService.getUserById(anyLong()))
                .thenReturn(userRequestDto);

        mockMvc.perform(get("/users/null")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(userService, never()).getUserById(anyLong());
    }

    @Test
    @SneakyThrows
    void updateUserShouldReturnUpdatedUser() {
        UserRequestDto requestDto = getUserRequestDto("dima@yandex.ru");
        UserRequestDto responseDto = getUserResponseDto("fima@yandex.ru");

        when(userService.updateUserById(any(), anyLong()))
                .thenReturn(responseDto);

        mockMvc.perform(MockMvcRequestBuilders.patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.id", is(responseDto.getId()), Long.class),
                        jsonPath("$.email", equalTo(responseDto.getEmail()))
                );
    }

    @Test
    @SneakyThrows
    void deleteUserShouldDeleteUserWhenRequestIsCorrect() {
        mockMvc.perform(MockMvcRequestBuilders.delete("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk()
                );
        verify(userService, Mockito.times(1)).deleteUserById(anyLong());
    }
}