package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.comment.dto.CommentResponseDto;

import java.util.List;

@Getter
@Setter
@Builder
public class ItemResponseDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private BookingShortDto nextBooking;
    private BookingShortDto lastBooking;
    private List<CommentResponseDto> comments;
}