package ru.practicum.shareit.exception;

public class ItemValidationException extends RuntimeException {
    public ItemValidationException(String message) {
        super(message);
    }
}