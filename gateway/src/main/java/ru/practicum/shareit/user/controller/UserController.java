package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.client.UserClient;
import ru.practicum.shareit.user.mapper.UserRequestDto;
import ru.practicum.shareit.user.validation.CreateUser;
import ru.practicum.shareit.user.validation.UpdateUser;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> createNewUser(@RequestBody @Validated(CreateUser.class) UserRequestDto userRequestDto) {
        return userClient.createNewUser(userRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        return userClient.getAllUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUserById(@PathVariable Long id) {
        return userClient.getUserById(id);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateUserById(@RequestBody
                                                 @Validated(UpdateUser.class) UserRequestDto userRequestDto,
                                                 @PathVariable Long id) {
        return userClient.updateUserById(userRequestDto, id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteUserById(@PathVariable Long id) {
        return userClient.deleteUser(id);
    }
}