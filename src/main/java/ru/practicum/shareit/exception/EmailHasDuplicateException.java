package ru.practicum.shareit.exception;

public class EmailHasDuplicateException extends RuntimeException {
    public EmailHasDuplicateException(String message) {
        super(message);
    }
}
