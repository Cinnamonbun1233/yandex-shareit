package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.storage.UserStorage;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceImpl implements UserService {

    private final UserStorage repository;

    @Override
    public List<User> findAll() {
        log.info("Пользователи отправлены");
        return repository.findAll();
    }

    @Override
    public User getById(long id) {
        log.info("Пользователь с id{} отправлен", id);
        return repository.getById(id).orElseThrow(() -> {
            log.warn("User with id {} not found", id);
            throw new ObjectNotFoundException("User not found");
        });
    }

    @Override
    public UserDto create(UserDto userDto) {
        validator(userDto.getEmail());
        log.info("Пользователь создан");
        return repository.create(userDto);
    }

    @Override
    public User update(long id, User user) {
        if (user.getEmail() != null) {
            validator(user.getEmail());
        }
        return repository.update(id, user);
    }

    @Override
    public void delete(long id) {
        log.info("Пользователь с id {} удалён", id);
        repository.delete(id);
    }

    private void validator(String email) {
        List<User> users = repository.findAll();
        if (checker(users, email)) {
            log.warn("Пользователь с таким e-mail уже существует");
            throw new ValidationException("Пользователь с таким e-mail уже существует");
        }
    }

    private boolean checker(List<User> users,String email) {
        boolean flag = users.stream()
                .anyMatch(repoUser -> repoUser.getEmail().equals(email));
        return flag;
    }
}