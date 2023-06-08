package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.utils.ValidBookingDate;

import javax.validation.GroupSequence;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Data
@Builder
@ValidBookingDate(groups = BookingRequestDto.DependentValidations.class)
@GroupSequence({BookingRequestDto.class, BookingRequestDto.DependentValidations.class})
public class BookingRequestDto {
    interface DependentValidations {
    }

    @Null
    private Long id;
    @FutureOrPresent
    @NotNull
    @JsonProperty("start")
    private LocalDateTime startDate;
    @FutureOrPresent
    @NotNull
    @JsonProperty("end")
    private LocalDateTime endDate;
    @NotNull
    private Long itemId;
    private BookingStatus status;

    @JsonIgnore
    public Boolean isDatesCorrect() {
        if (this.startDate.isAfter(endDate) || this.startDate.isEqual(endDate)) {
            return false;
        }
        return true;
    }
}
