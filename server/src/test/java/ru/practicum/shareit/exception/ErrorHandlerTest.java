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
        ItemNotAvailableException exception = new ItemNotAvailableException("some ex");
        Map<String, String> result = errorHandler.handle(exception);
        assertNotNull(result);
        assertThat(result.get("Ошибка запроса"), equalTo(exception.getMessage()));
    }

    @Test
    void handleUnsupportedException() {
        UnknownStateException unknownStateException = new UnknownStateException("some ex");
        Map<String, String> result = errorHandler.handleUnsupportedException(unknownStateException);
        assertNotNull(result);
        assertThat(result.get("error"), equalTo("Unknown state: " + unknownStateException.getMessage()));
    }

    @Test
    void handleNotFoundException() {
        BookingNotFoundException bookingNotFoundException = new BookingNotFoundException("some ex");
        Map<String, String> stringStringMap = errorHandler.handleNotFoundException(bookingNotFoundException);
        assertNotNull(stringStringMap);
        assertThat(stringStringMap.get("Ошибка запроса"), equalTo(bookingNotFoundException.getMessage()));
    }
}