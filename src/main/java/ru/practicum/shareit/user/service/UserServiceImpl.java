package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EmailValidationException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.dto.UserDTO;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public List<UserDTO> getAllUsers() {
        return userRepository.getAllUsers().stream()
                .map(UserMapper::userToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UserDTO getUserById(Long id) {
        return UserMapper.userToDTO(userRepository.getUserById(id).orElseThrow(()
                -> new UserNotFoundException("Пользователь с id: '" + id + "' не найден")));
    }

    @Override
    public UserDTO createNewUser(User user) {
        emailValidator(user);
        return UserMapper.userToDTO(userRepository.createNewUser(user));
    }

    @Override
    public UserDTO updateUser(Long id, User user) {
        User userInMemory = userRepository.getUserById(id).orElseThrow(()
                -> new UserNotFoundException("Пользователь с id: '" + id + "' не найден"));
        userPatcher(user, userInMemory);
        return userRepository.updateUser(userInMemory);
    }

    @Override
    public void deleteUserById(Long id) {
        userRepository.deleteUserById(id);
    }

    private void userPatcher(User user, User userInMemory) {
        if (!Objects.isNull(user.getEmail()) && !user.getEmail().isBlank()
                && !userInMemory.getEmail().equals(user.getEmail())) {
            emailValidator(user);
            userInMemory.setEmail(user.getEmail());
        }
        if (!Objects.isNull(user.getName()) && !user.getName().isBlank()) {
            userInMemory.setName(user.getName());
        }
    }

    private void emailValidator(User user) {
        if (userRepository.getAllUsers().contains(user)) {
            throw new EmailValidationException("Пользователь с email: '" + user.getEmail() + "' уже существует");
        }
    }
}