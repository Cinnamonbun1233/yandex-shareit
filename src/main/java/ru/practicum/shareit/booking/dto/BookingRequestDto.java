package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.status.BookingStatus;
import ru.practicum.shareit.booking.validation.DependentValidations;
import ru.practicum.shareit.booking.validation.ValidBookingDate;

import javax.validation.GroupSequence;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@ValidBookingDate(groups = DependentValidations.class)
@GroupSequence({BookingRequestDto.class, DependentValidations.class})
public class BookingRequestDto {
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
        return !this.startDate.isAfter(endDate) && !this.startDate.isEqual(endDate);
    }
}