package ru.practicum.shareit.exception;

//DONE!!!
public class ItemNotAvailableException extends RuntimeException {
    public ItemNotAvailableException(String message) {
        super(message);
    }
}