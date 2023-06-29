package ru.practicum.shareit.exception;

//Готово
public class BookingNotFoundException extends RuntimeException {
    public BookingNotFoundException(String message) {
        super(message);
    }
}