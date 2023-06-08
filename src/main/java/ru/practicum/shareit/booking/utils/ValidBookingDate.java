package ru.practicum.shareit.booking.utils;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = {BookingDateValidator.class})
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidBookingDate {
    String message() default "Дата начала не может быть позже или равна дате окончания";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
