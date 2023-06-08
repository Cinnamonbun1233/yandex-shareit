package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice(basePackages = "ru.practicum.shareit")
@Slf4j
public class ErrorHandler {
    @ExceptionHandler({ItemNotAvailableException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handle(final RuntimeException ex) {
        log.warn("Ошибка запроса: {}", ex.getMessage());
        return Map.of("Ошибка запроса", ex.getMessage());
    }

    @ExceptionHandler(UnknownStateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleUnsupportedEx(final RuntimeException ex) {
        return Map.of("error", "Unknown state: " + ex.getMessage());
    }

    @ExceptionHandler({UserNotFoundException.class, ItemNotFoundException.class,
            ItemUpdatingException.class, BookingNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFoundException(final RuntimeException ex) {
        log.warn("Ошибка запроса: {}", ex.getMessage());
        return Map.of("Ошибка запроса", ex.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationEx(final MethodArgumentNotValidException ex) {
        log.warn("Ошибка валидации: {}", ex.getFieldError().getDefaultMessage());
        return Map.of("Ошибка валидации", ex.getFieldError().getDefaultMessage());
    }
}
