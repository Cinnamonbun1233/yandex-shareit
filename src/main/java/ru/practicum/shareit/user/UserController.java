package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserRequestDto createUser(@RequestBody @Validated(UserRequestDto.NewUser.class) UserRequestDto userRequestDto) {
        return userService.saveUser(userRequestDto);
    }

    @GetMapping
    public List<UserRequestDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public UserRequestDto getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @PatchMapping("/{id}")
    public UserRequestDto updateUser(@RequestBody @Validated(UserRequestDto.UpdateUser.class) UserRequestDto userRequestDto, @PathVariable Long id) {
        return userService.updateUser(userRequestDto, id);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}
