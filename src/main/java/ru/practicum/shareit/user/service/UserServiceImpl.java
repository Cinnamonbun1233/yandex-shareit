package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public List<UserRequestDto> getAllUsers() {
        return UserMapper.userToDto(userRepository.findAll());
    }

    @Override
    public UserRequestDto saveUser(UserRequestDto userRequestDto) {
        User user = UserMapper.dtoToUser(userRequestDto);
        return UserMapper.userToDto(userRepository.save(user));
    }

    @Override
    public UserRequestDto updateUser(UserRequestDto userRequestDto, Long id) {
        String email = userRequestDto.getEmail();
        String name = userRequestDto.getName();

        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(
                String.format("Пользователь с id: %s не обнаружен", id)));

        if (email != null) {
            user.setEmail(email);
        }
        if (name != null) {
            user.setName(name);
        }
        return UserMapper.userToDto(userRepository.save(user));
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public UserRequestDto getUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(
                String.format("Пользователь с id: %s не обнаружен", id)));
        return UserMapper.userToDto(user);
    }
}
