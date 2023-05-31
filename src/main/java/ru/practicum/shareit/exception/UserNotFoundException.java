package ru.practicum.shareit.exception;

//DONE!!!
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}