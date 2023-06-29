package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.status.BookingStatus;
import ru.practicum.shareit.item.dto.ItemShortResponseDto;
import ru.practicum.shareit.user.dto.UserShortResponseDto;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class BookingResponseDto {
    private Long id;
    @JsonProperty("start")
    private LocalDateTime startDate;
    @JsonProperty("end")
    private LocalDateTime endDate;
    private ItemShortResponseDto item;
    private UserShortResponseDto booker;
    private BookingStatus status;
}