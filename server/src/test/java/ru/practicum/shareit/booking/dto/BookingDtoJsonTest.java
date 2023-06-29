package ru.practicum.shareit.booking.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.status.BookingStatus;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingDtoJsonTest {
    @Autowired
    private JacksonTester<BookingRequestDto> bookingRequestDtoJacksonTester;

    @Test
    @SneakyThrows
    void testBookingRequestDto() {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = now.plusMinutes(5);
        LocalDateTime end = now.plusDays(1);

        BookingRequestDto requestDto = BookingRequestDto.builder()
                .id(1L)
                .startDate(start)
                .endDate(end)
                .itemId(1L)
                .status(BookingStatus.WAITING)
                .build();
        JsonContent<BookingRequestDto> result = bookingRequestDtoJacksonTester.write(requestDto);
        System.out.println(result);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).doesNotHaveJsonPath("$.startDate");
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo(formatter.format(start));
        assertThat(result).doesNotHaveJsonPath("$.endDate");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo(formatter.format(end));
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo(BookingStatus.WAITING.name());
        assertThat(result).doesNotHaveJsonPath("$.isDatesCorrect");
    }
}