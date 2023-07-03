package ru.practicum.shareit.booking.validation;

import ru.practicum.shareit.booking.status.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

public class BookingMapper {
    public static Booking bookingRequestDtoToBooking(BookingRequestDto bookingRequestDto, Item item, User user) {
        return Booking.builder()
                .id(bookingRequestDto.getId())
                .startDate(bookingRequestDto.getStartDate())
                .endDate(bookingRequestDto.getEndDate())
                .item(item)
                .booker(user)
                .status(bookingRequestDto.getStatus() != null ? bookingRequestDto.getStatus() : BookingStatus.WAITING)
                .build();
    }

    public static BookingResponseDto bookingToBookingResponseDto(Booking booking) {
        return BookingResponseDto.builder()
                .id(booking.getId())
                .startDate(booking.getStartDate())
                .endDate(booking.getEndDate())
                .item(ItemMapper.itemToItemShortResponseDto(booking.getItem()))
                .booker(UserMapper.userToUserShortResponseDto(booking.getBooker()))
                .status(booking.getStatus())
                .build();
    }

    public static List<BookingResponseDto> bookingsToBookingResponseDtoList(Iterable<Booking> bookings) {
        List<BookingResponseDto> bookingResponseDtoList = new ArrayList<>();

        for (Booking booking : bookings) {
            bookingResponseDtoList.add(bookingToBookingResponseDto(booking));
        }

        return bookingResponseDtoList;
    }

    public static BookingShortDto bookingToBookingShortDto(Booking booking) {
        return BookingShortDto.builder()
                .id(booking.getId())
                .bookerId(booking.getBooker().getId())
                .start(booking.getStartDate())
                .end(booking.getEndDate())
                .build();
    }
}