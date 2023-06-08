package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.validation.CreateUser;
import ru.practicum.shareit.validation.UpdateUser;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserRequestDto createNewUser(@Validated(CreateUser.class) @RequestBody UserRequestDto userRequestDto) {
        log.info("Получен запрос на добавление нового пользователя");
        return userService.createNewUser(userRequestDto);
    }

    @GetMapping
    public List<UserRequestDto> getAllUsers() {
        log.info("Получен запрос на получение всех пользователей");
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public UserRequestDto getUserById(@PathVariable Long id) {
        log.info("Получен запрос на получение пользователя c id: '{}'", id);
        return userService.getUserById(id);
    }

    @PatchMapping("/{id}")
    public UserRequestDto updateUser(@PathVariable Long id, @Validated(UpdateUser.class) @RequestBody UserRequestDto userRequestDto) {
        log.info("Получен запрос на обновление пользователя с id: '{}'", id);
        return userService.updateUserById(id, userRequestDto);
    }

    @DeleteMapping("/{id}")
    public void deleteUserById(@PathVariable Long id) {
        log.info("Получен запрос на удаление пользователя с id: '{}'", id);
        userService.deleteUserById(id);
    }
}