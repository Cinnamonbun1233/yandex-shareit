package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.Value;
import ru.practicum.shareit.booking.status.BookingStatus;
import ru.practicum.shareit.validation.DependentValidations;
import ru.practicum.shareit.validation.ValidBookingDate;

import javax.validation.GroupSequence;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.time.LocalDateTime;

@Data
@Builder
@Value
@ValidBookingDate(groups = DependentValidations.class)
@GroupSequence({BookingRequestDto.class, DependentValidations.class})
public class BookingRequestDto {
    @Null
    Long id;
    @FutureOrPresent
    @NotNull
    @JsonProperty("start")
    LocalDateTime startDate;
    @FutureOrPresent
    @NotNull
    @JsonProperty("end")
    LocalDateTime endDate;
    @NotNull
    Long itemId;
    BookingStatus status;

    @JsonIgnore
    public Boolean isDatesCorrect() {
        return !this.startDate.isAfter(endDate) && !this.startDate.isEqual(endDate);
    }
}