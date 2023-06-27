package ru.practicum.shareit.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.error.ErrorHandler;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ErrorHandlerTest {
    private ErrorHandler errorHandler;

    @BeforeEach
    void setUp() {
        errorHandler = new ErrorHandler();
    }

    @Test
    void handle() {
        ItemNotAvailableException ex = new ItemNotAvailableException("Какая-то ошибка");
        Map<String, String> result = errorHandler.handle(ex);
        assertNotNull(result);
        assertThat(result.get("Ошибка запроса"), equalTo(ex.getMessage()));
    }

    @Test
    void handleUnsupportedException() {
        UnknownStateException exception = new UnknownStateException("Какая-то ошибка");
        Map<String, String> result = errorHandler.handleUnsupportedException(exception);
        assertNotNull(result);
        assertThat(result.get("error"), equalTo("Unknown state: " + exception.getMessage()));
    }

    @Test
    void handleNotFoundException() {
        BookingNotFoundException exception = new BookingNotFoundException("Какая-то ошибка");
        Map<String, String> stringStringMap = errorHandler.handleNotFoundException(exception);
        assertNotNull(stringStringMap);
        assertThat(stringStringMap.get("Ошибка запроса"), equalTo(exception.getMessage()));
    }
}