package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.Value;
import ru.practicum.shareit.booking.status.BookingStatus;
import ru.practicum.shareit.item.dto.ItemShortResponseDto;
import ru.practicum.shareit.user.dto.UserResponseDto;

import java.time.LocalDateTime;

//DONE!!!
@Data
@Builder
@Value
public class BookingResponseDto {
    Long id;
    @JsonProperty("start")
    LocalDateTime startDate;
    @JsonProperty("end")
    LocalDateTime endDate;
    ItemShortResponseDto item;
    UserResponseDto booker;
    BookingStatus status;
}