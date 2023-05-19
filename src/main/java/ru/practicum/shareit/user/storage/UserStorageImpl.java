package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Repository
public class UserStorageImpl implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();

    private long idCounter = 1;

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public Optional<User> getById(long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public UserDto create(UserDto userDto) {
        userDto.setId(idCounter++);
        users.put(userDto.getId(), UserMapper.toUser(userDto));
        return userDto;
    }

    @Override
    public User update(long id, User user) {
        if (users.containsKey(id)) {
            if (user.getName() != null) {
                users.get(id).setName(user.getName());
            }
            if (user.getEmail() != null) {
                users.get(id).setEmail(user.getEmail());
            }
            return users.get(id);
        } else  {
            throw new ObjectNotFoundException("Юзер не найден");
        }
    }

    @Override
    public void delete(long id) {
        users.remove(id);
    }
}