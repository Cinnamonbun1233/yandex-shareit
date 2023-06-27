package ru.practicum.shareit.booking.validation;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.status.BookingStatus;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDateTime;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest
class BookingDateValidatorTest {
    private ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    private Validator validatorFactoryValidator = validatorFactory.getValidator();

    @Test
    void isValidWhenStartDateAfter() {
        BookingRequestDto dto = BookingRequestDto.builder()
                .id(1L)
                .status(BookingStatus.WAITING)
                .startDate(LocalDateTime.now().plusMonths(1))
                .endDate(LocalDateTime.now())
                .itemId(1L)
                .build();

        Set<ConstraintViolation<BookingRequestDto>> violations = validatorFactoryValidator.validate(dto);
        ConstraintViolation<BookingRequestDto> violation = violations.stream().findFirst().get();
        assertThat(violations, not(empty()));
        assertThat(violation.getMessage(), not(emptyString()));
    }

    @Test
    void isValidWhenStartEquals() {
        LocalDateTime now = LocalDateTime.now();
        BookingRequestDto dto = BookingRequestDto.builder()
                .id(1L)
                .status(BookingStatus.WAITING)
                .startDate(now)
                .endDate(now)
                .itemId(1L)
                .build();

        Set<ConstraintViolation<BookingRequestDto>> violations = validatorFactoryValidator.validate(dto);
        ConstraintViolation<BookingRequestDto> violation = violations.stream().findFirst().get();
        assertThat(violations, not(empty()));
        assertThat(violation.getMessage(), not(emptyString()));
    }
}