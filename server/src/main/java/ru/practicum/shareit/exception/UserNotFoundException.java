package ru.practicum.shareit.exception;

//Готово
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}