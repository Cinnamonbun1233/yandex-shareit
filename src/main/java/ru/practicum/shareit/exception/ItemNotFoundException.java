package ru.practicum.shareit.exception;

//DONE!!!
public class ItemNotFoundException extends RuntimeException {
    public ItemNotFoundException(String message) {
        super(message);
    }
}