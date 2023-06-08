package ru.practicum.shareit.booking.utils;

import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.utils.ItemMapper;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

public class BookingMapper {
    public static Booking dtoToBooking(BookingRequestDto bookingRequestDto, Item item, User user) {
        return Booking.builder()
                .id(bookingRequestDto.getId())
                .startDate(bookingRequestDto.getStartDate())
                .endDate(bookingRequestDto.getEndDate())
                .item(item)
                .booker(user)
                .status(bookingRequestDto.getStatus() != null ? bookingRequestDto.getStatus() : BookingStatus.WAITING)
                .build();
    }

    public static BookingResponseDto toResponseDto(Booking booking) {
        return BookingResponseDto.builder()
                .id(booking.getId())
                .startDate(booking.getStartDate())
                .endDate(booking.getEndDate())
                .item(ItemMapper.toItemShort(booking.getItem()))
                .booker(UserMapper.toUserShort(booking.getBooker()))
                .status(booking.getStatus())
                .build();
    }

    public static List<BookingResponseDto> toResponseDto(Iterable<Booking> bookings) {
        List<BookingResponseDto> dtos = new ArrayList<>();
        for (Booking booking : bookings) {
            dtos.add(toResponseDto(booking));
        }
        return dtos;
    }

    public static BookingShortDto toShortDto(Booking booking) {
        return BookingShortDto.builder()
                .id(booking.getId())
                .bookerId(booking.getBooker().getId())
                .start(booking.getStartDate())
                .end(booking.getEndDate())
                .build();
    }
}
