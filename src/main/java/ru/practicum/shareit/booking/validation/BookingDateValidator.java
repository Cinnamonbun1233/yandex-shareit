package ru.practicum.shareit.booking.validation;

import ru.practicum.shareit.booking.dto.BookingRequestDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class BookingDateValidator implements ConstraintValidator<ValidBookingDate, BookingRequestDto> {
    @Override
    public boolean isValid(BookingRequestDto bookingRequestDto, ConstraintValidatorContext constraintValidatorContext) {
        return bookingRequestDto.isDatesCorrect();
    }
}