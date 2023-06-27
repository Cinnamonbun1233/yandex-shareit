package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.request.dto.RequestItemRequestDto;
import ru.practicum.shareit.request.dto.RequestItemResponseDto;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class RequestItemServiceImplTestIT {
    private final RequestItemService requestItemService;
    private final UserService userService;

    private static UserRequestDto getUserDto(String email) {
        return UserRequestDto.builder()
                .name("Dima")
                .email(email)
                .build();
    }

    private static RequestItemRequestDto getRequestDto() {
        return RequestItemRequestDto.builder()
                .description("Предмет невероятной красоты")
                .build();
    }

    @Test
    void addNewRequestShouldCreateRequest() {
        RequestItemRequestDto dto = getRequestDto();
        UserRequestDto firstUser = userService.createNewUser(getUserDto("dima@yandex.ru"));

        RequestItemRequestDto result = requestItemService.createNewRequest(dto, firstUser.getId());

        assertThat(result, allOf(
                hasProperty("id", equalTo(result.getId())),
                hasProperty("description", containsStringIgnoringCase("Предмет невероятной красоты")),
                hasProperty("created", equalTo(result.getCreated()))
        ));
    }

    @Test
    @DisplayName("getRequests should return requests of requestor")
    void getRequests() {
        UserRequestDto userDto = userService.createNewUser(getUserDto("dima@yandex.ru"));
        RequestItemRequestDto requestDto = requestItemService.createNewRequest(getRequestDto(), userDto.getId());

        List<RequestItemResponseDto> requests = requestItemService.getAllRequestsByUserId(userDto.getId());

        assertThat(requests, hasSize(1));
        assertThat(requests, hasItem(allOf(
                hasProperty("id", equalTo(requestDto.getId())),
                hasProperty("description", containsStringIgnoringCase("Предмет невероятной красоты")),
                hasProperty("created", equalTo(requestDto.getCreated())),
                hasProperty("items", empty())
        )));
    }

    @Test
    void getRequestsShouldThrowUserNotFound() {
        UserRequestDto userDto = userService.createNewUser(getUserDto("dima@yandex.ru"));
        RequestItemRequestDto requestDto = requestItemService.createNewRequest(getRequestDto(), userDto.getId());

        UserNotFoundException ex = assertThrows(UserNotFoundException.class,
                () -> requestItemService.getAllRequestsByUserId(2L));

        assertThat(ex.getMessage(), equalTo(String.format("Пользователь с id: '%s' не найден", 2L)));
    }

    @Test
    @DisplayName("getAllRequests should return requests of other user (not requestor)")
    void getAllRequests() {
        UserRequestDto firstUser = userService.createNewUser(getUserDto("dima@yandex.ru"));
        UserRequestDto secondUser = userService.createNewUser(getUserDto("fima@yandex.ru"));
        RequestItemRequestDto requestDto = requestItemService.createNewRequest(getRequestDto(), firstUser.getId());

        List<RequestItemResponseDto> requests = requestItemService.getAllRequests(secondUser.getId(), 0, 10);

        assertThat(requests, hasSize(1));
        assertThat(requests, hasItem(allOf(
                hasProperty("id", equalTo(requestDto.getId())),
                hasProperty("description", containsStringIgnoringCase("Предмет невероятной красоты")),
                hasProperty("created", equalTo(requestDto.getCreated())),
                hasProperty("items", empty())
        )));
    }

    @Test
    void getAllRequestsShouldThrowUserNotFound() {
        UserRequestDto firstUser = userService.createNewUser(getUserDto("dima@yandex.ru"));

        UserNotFoundException ex = assertThrows(UserNotFoundException.class,
                () -> requestItemService.getAllRequests(2L, 0, 10));

        assertThat(ex.getMessage(), equalTo(String.format("Пользователь с id: '%s' не найден", 2L)));
    }

    @Test
    @DisplayName("getAllRequests should return empty requests of requestor")
    void getAllRequestsRequestor() {
        UserRequestDto userDto = userService.createNewUser(getUserDto("dima@yandex.ru"));

        List<RequestItemResponseDto> requests = requestItemService.getAllRequests(userDto.getId(), 0, 10);

        assertThat(requests, empty());
    }

    @Test
    void getAllRequestByIdShouldThrowUserNotFound() {
        UserRequestDto firstUser = userService.createNewUser(getUserDto("dima@yandex.ru"));

        UserNotFoundException ex = assertThrows(UserNotFoundException.class,
                () -> requestItemService.getRequestByUserId(2L, 1L));

        assertThat(ex.getMessage(), equalTo(String.format("Пользователь с id: '%s' не найден", 2L)));
    }
}