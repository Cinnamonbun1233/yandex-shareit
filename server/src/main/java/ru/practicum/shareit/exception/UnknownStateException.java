package ru.practicum.shareit.exception;

//Готово
public class UnknownStateException extends RuntimeException {
    public UnknownStateException(String message) {
        super(message);
    }
}