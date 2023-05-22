package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.validation.Create;
import ru.practicum.shareit.validation.Update;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.dto.UserDTO;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<UserDTO> getAllUsers() {
        log.info("Получен запрос на получение всех пользователей");
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public UserDTO getUserById(@PathVariable Long id) {
        log.info("Получен запрос на получение пользователя c id: '{}'", id);
        return userService.getUserById(id);
    }

    @PostMapping
    public UserDTO createNewUser(@Validated(Create.class) @RequestBody UserDTO userDTO) {
        log.info("Получен запрос на добавление нового пользователя");
        return userService.createNewUser(UserMapper.dtoToUser(userDTO));
    }

    @PatchMapping("/{id}")
    public UserDTO updateUser(@PathVariable Long id, @Validated(Update.class) @RequestBody UserDTO userDTO) {
        log.info("Получен запрос на обновление пользователя с id: '{}'", id);
        return userService.updateUser(id, UserMapper.dtoToUser(userDTO));
    }

    @DeleteMapping("/{id}")
    public void deleteUserById(@PathVariable Long id) {
        log.info("Получен запрос на удаление пользователя с id: '{}'", id);
        userService.deleteUserById(id);
    }
}