package ru.practicum.shareit.exception;

public class UnknownStateException extends RuntimeException {
    public UnknownStateException(String message) {
        super(message);
    }
}