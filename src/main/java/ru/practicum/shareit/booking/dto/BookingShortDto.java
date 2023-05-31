package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Value;

import java.time.LocalDateTime;

//DONE!!!
@Data
@Builder
@Value
public class BookingShortDto {
    Long id;
    Long bookerId;
    LocalDateTime start;
    LocalDateTime end;
}