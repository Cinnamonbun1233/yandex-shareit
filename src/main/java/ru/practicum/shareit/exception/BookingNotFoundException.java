package ru.practicum.shareit.exception;

//DONE!!!
public class BookingNotFoundException extends RuntimeException {
    public BookingNotFoundException(String message) {
        super(message);
    }
}