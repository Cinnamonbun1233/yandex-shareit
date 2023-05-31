package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

//DONE!!!
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
                -> new UserNotFoundException("Пользователь с id: '" + id + "' не найден"));
        return UserMapper.userToUserRequestDto(user);
    }

    @Override
    public UserRequestDto updateUserById(Long id, UserRequestDto userRequestDto) {
        User user = userRepository.findById(id).orElseThrow(()
                -> new UserNotFoundException("Пользователь с id: '" + id + "' не найден"));
        userPatcher(userRequestDto, user);
        return UserMapper.userToUserRequestDto(userRepository.save(user));
    }

    @Override
    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }

    private static void userPatcher(UserRequestDto userRequestDto, User user) {
        if (userRequestDto.getEmail() != null) {
            user.setEmail(userRequestDto.getEmail());
        }
        if (userRequestDto.getName() != null) {
            user.setName(userRequestDto.getName());
        }
    }
}