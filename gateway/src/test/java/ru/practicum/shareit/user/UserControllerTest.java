package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.user.client.UserClient;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.mapper.UserRequestDto;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private UserClient userClient;
    @Autowired
    private MockMvc mockMvc;

    private static UserRequestDto getUserRequestDto(String email) {
        return UserRequestDto.builder()
                .name("Дима")
                .email(email)
                .build();
    }

    @Test
    @SneakyThrows
    void createUserBadRequestWhenEmailIsInvalid() {
        UserRequestDto userRequestDto = getUserRequestDto("dima.ru");

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDto)))
                .andExpectAll(
                        status().isBadRequest()
                );
        verify(userClient, never()).createNewUser(any());
    }

    @Test
    @SneakyThrows
    void createUserBadRequestWhenNameIsInvalid() {
        UserRequestDto userRequestDto = getUserRequestDto("dima@yandex.ru");
        userRequestDto.setName("");

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDto)))
                .andExpectAll(
                        status().isBadRequest()
                );
    }

    @Test
    @SneakyThrows
    void updateUserBadRequestWhenEmailIsInvalid() {
        UserRequestDto userRequestDto = getUserRequestDto("dimaru");

        mockMvc.perform(MockMvcRequestBuilders.patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void createUserBadRequestWhenEmailIsNull() {
        UserRequestDto userRequestDto = getUserRequestDto("dima.ru");
        userRequestDto.setEmail(null);

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDto)))
                .andExpect(status().isBadRequest());
        verify(userClient, never()).createNewUser(any());
    }
}