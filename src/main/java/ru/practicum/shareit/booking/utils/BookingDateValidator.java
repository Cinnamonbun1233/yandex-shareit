package ru.practicum.shareit.booking.utils;

import ru.practicum.shareit.booking.dto.BookingRequestDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class BookingDateValidator implements ConstraintValidator<ValidBookingDate, BookingRequestDto> {
    @Override
    public boolean isValid(BookingRequestDto bookingRequestDto, ConstraintValidatorContext constraintValidatorContext) {
        if (bookingRequestDto.isDatesCorrect()) {
            return true;
        }
        return false;
    }
}
