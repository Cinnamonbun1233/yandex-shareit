package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserRequestDto createNewUser(UserRequestDto userRequestDto) {
        User user = UserMapper.userRequestDtoToUser(userRequestDto);
        return UserMapper.userToUserRequestDto(userRepository.save(user));
    }

    @Override
    public List<UserRequestDto> getAllUsers() {
        return UserMapper.usersToUserRequestDtoList(userRepository.findAll());
    }

    @Override
    public UserRequestDto getUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(()
                -> new UserNotFoundException(String.format("Пользователь с id: %s не обнаружен", id)));
        return UserMapper.userToUserRequestDto(user);
    }

    @Override
    public UserRequestDto updateUserById(UserRequestDto userRequestDto, Long id) {
        String email = userRequestDto.getEmail();
        String name = userRequestDto.getName();
        User user = userRepository.findById(id).orElseThrow(()
                -> new UserNotFoundException(String.format("Пользователь с id: %s не обнаружен", id)));
        userPatcher(email, name, user);
        return UserMapper.userToUserRequestDto(userRepository.save(user));
    }

    @Override
    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }

    private static void userPatcher(String email, String name, User user) {
        if (email != null) {
            user.setEmail(email);
        }

        if (name != null) {
            user.setName(name);
        }
    }
}