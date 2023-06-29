package ru.practicum.shareit.exception.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exception.*;

import java.util.Map;
import java.util.Objects;

@Slf4j
@RestControllerAdvice(basePackages = "ru.practicum.shareit")
public class ErrorHandler {
    @ExceptionHandler({ItemNotAvailableException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handle(final RuntimeException exception) {
        log.warn("Ошибка запроса: {}", exception.getMessage());
        return Map.of("Ошибка запроса", exception.getMessage());
    }

    @ExceptionHandler(UnknownStateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleUnsupportedException(final RuntimeException exception) {
        return Map.of("error", "Unknown state: " + exception.getMessage());
    }

    @ExceptionHandler({UserNotFoundException.class, ItemNotFoundException.class,
            ItemUpdatingException.class, BookingNotFoundException.class, RequestNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFoundException(final RuntimeException exception) {
        log.warn("Ошибка запроса: {}", exception.getMessage());
        return Map.of("Ошибка запроса", exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationException(final MethodArgumentNotValidException exception) {
        log.warn("Ошибка валидации: {}", Objects.requireNonNull(exception.getFieldError()).getDefaultMessage());
        return Map.of("Ошибка валидации", Objects.requireNonNull(exception.getFieldError().getDefaultMessage()));
    }
}