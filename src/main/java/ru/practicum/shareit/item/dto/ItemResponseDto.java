package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Value;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.comment.dto.CommentResponseDto;

import java.util.List;

//DONE!!!
@Data
@Value
@Builder
public class ItemResponseDto {
    Long id;
    String name;
    String description;
    Boolean available;
    BookingShortDto nextBooking;
    BookingShortDto lastBooking;
    List<CommentResponseDto> comments;
}