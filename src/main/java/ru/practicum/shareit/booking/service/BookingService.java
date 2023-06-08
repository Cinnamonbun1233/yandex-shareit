package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.status.State;

import java.util.List;

public interface BookingService {
    BookingResponseDto createNewBooking(BookingRequestDto bookingRequestDto, Long userId);

    List<BookingResponseDto> getAllUserBookings(State state, Long userId, Boolean owner);

    BookingResponseDto getBookingById(Long bookingId, Long userId);

    BookingResponseDto approveBooking(Long bookingId, Boolean approved, Long ownerId);
}