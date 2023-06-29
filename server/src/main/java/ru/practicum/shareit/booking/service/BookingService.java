package ru.practicum.shareit.booking.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.GetBookingRequest;

import java.util.List;

public interface BookingService {
    BookingResponseDto createNewBooking(BookingRequestDto bookingRequestDto, Long userId);

    List<BookingResponseDto> getAllUserBookings(GetBookingRequest getBookingRequest, PageRequest pageRequest);

    BookingResponseDto getBookingByUserId(Long bookingId, Long userId);

    BookingResponseDto approveBooking(Long bookingId, Boolean approved, Long ownerId);
}