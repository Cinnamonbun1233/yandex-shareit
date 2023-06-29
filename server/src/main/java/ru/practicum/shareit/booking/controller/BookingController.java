package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.GetBookingRequest;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.status.State;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingResponseDto createNewBooking(@RequestBody BookingRequestDto bookingRequestDto,
                                               @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.createNewBooking(bookingRequestDto, userId);
    }

    @GetMapping
    public List<BookingResponseDto> getAllUserBookings(@RequestParam(defaultValue = "ALL") State state,
                                                       @RequestHeader("X-Sharer-User-Id") Long userId,
                                                       @RequestParam(required = false, defaultValue = "0") int from,
                                                       @RequestParam(required = false, defaultValue = "10") int size) {
        PageRequest pageRequest = PageRequest.of(from / size, size, Sort.Direction.DESC, "startDate");
        return bookingService.getAllUserBookings(GetBookingRequest.of(state, userId, false), pageRequest);
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getAllUserItemBookings(@RequestParam(defaultValue = "ALL") State state,
                                                           @RequestHeader("X-Sharer-User-Id") Long userId,
                                                           @RequestParam(required = false, defaultValue = "0") int from,
                                                           @RequestParam(required = false, defaultValue = "10") int size) {
        PageRequest pageRequest = PageRequest.of(from / size, size, Sort.Direction.DESC, "startDate");
        return bookingService.getAllUserBookings(GetBookingRequest.of(state, userId, true), pageRequest);
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto getBookingByUserId(@PathVariable Long bookingId,
                                                 @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.getBookingByUserId(bookingId, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto approveBooking(@PathVariable Long bookingId,
                                             @RequestParam Boolean approved,
                                             @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return bookingService.approveBooking(bookingId, approved, ownerId);
    }
}