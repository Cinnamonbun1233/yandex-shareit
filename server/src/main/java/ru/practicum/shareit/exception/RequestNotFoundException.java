package ru.practicum.shareit.exception;

//Готово
public class RequestNotFoundException extends RuntimeException {
    public RequestNotFoundException(String message) {
        super(message);
    }
}