package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.status.BookingStatus;
import ru.practicum.shareit.item.dto.ItemShortResponseDto;
import ru.practicum.shareit.user.dto.UserShortResponseDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private BookingService bookingService;
    @Autowired
    private MockMvc mockMvc;

    private static BookingRequestDto getBookingRequestDto() {
        return BookingRequestDto.builder()
                .status(BookingStatus.WAITING)
                .startDate(LocalDateTime.now().plusMinutes(5))
                .endDate(LocalDateTime.now().plusDays(10))
                .itemId(1L)
                .build();
    }

    private static BookingResponseDto getBookingResponse() {
        ItemShortResponseDto item = ItemShortResponseDto.builder()
                .id(1L)
                .name("Грабли")
                .description("Грабли для уборки листвы")
                .available(true)
                .build();
        UserShortResponseDto booker = UserShortResponseDto.builder()
                .id(1L)
                .name("Alex")
                .build();
        return BookingResponseDto.builder()
                .id(1L)
                .status(BookingStatus.WAITING)
                .startDate(LocalDateTime.now().plusMinutes(5))
                .endDate(LocalDateTime.now().plusDays(10))
                .item(item)
                .booker(booker)
                .build();
    }

    @Test
    @SneakyThrows
    void addBookingShouldAddBookingWhenRequestIsCorrect() {
        BookingRequestDto bookingRequestDto = getBookingRequestDto();
        BookingResponseDto bookingResponse = getBookingResponse();

        when(bookingService.createNewBooking(any(), anyLong()))
                .thenReturn(bookingResponse);

        mockMvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(bookingRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id", is(bookingResponse.getId()), Long.class),
                        jsonPath("$.start", notNullValue()),
                        jsonPath("$.end", notNullValue()),
                        jsonPath("$.booker.id", is(bookingResponse.getBooker().getId()), Long.class),
                        jsonPath("$.status", equalTo(bookingResponse.getStatus().name()))
                );
    }

    @Test
    @SneakyThrows
    void addBookingShouldReturnBadRequestWhenUserIdHeaderIsAbsent() {
        BookingRequestDto bookingRequestDto = getBookingRequestDto();
        BookingResponseDto bookingResponse = getBookingResponse();

        when(bookingService.createNewBooking(any(), anyLong()))
                .thenReturn(bookingResponse);

        mockMvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(bookingRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(bookingService, never()).createNewBooking(any(), anyLong());
    }

    @Test
    @SneakyThrows
    void getBookingByIdShouldReturnBookingWhenRequestIsCorrect() {
        BookingResponseDto bookingResponseDto = getBookingResponse();

        when(bookingService.getBookingByUserId(anyLong(), anyLong()))
                .thenReturn(bookingResponseDto);

        mockMvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id", is(bookingResponseDto.getId()), Long.class),
                        jsonPath("$.start", notNullValue()),
                        jsonPath("$.end", notNullValue()),
                        jsonPath("$.booker.id", is(bookingResponseDto.getBooker().getId()), Long.class),
                        jsonPath("$.status", equalTo(bookingResponseDto.getStatus().name()))
                );
    }

    @Test
    @SneakyThrows
    void getBookingByIdShouldReturnBadRequestWhenPathVariableIsIncorrect() {
        BookingResponseDto bookingResponseDto = getBookingResponse();

        when(bookingService.getBookingByUserId(anyLong(), anyLong()))
                .thenReturn(bookingResponseDto);

        mockMvc.perform(get("/bookings/null")
                        .header("X-Sharer-User-Id", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(bookingService, never()).getBookingByUserId(anyLong(), anyLong());
    }

    @Test
    @SneakyThrows
    void approveBookingShouldApproveBookingWhenRequestIsCorrect() {
        BookingResponseDto bookingResponseDto = getBookingResponse();

        when(bookingService.approveBooking(anyLong(), anyBoolean(), anyLong()))
                .thenReturn(bookingResponseDto);

        mockMvc.perform(patch("/bookings/1")
                        .param("approved", "true")
                        .header("X-Sharer-User-Id", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id", is(bookingResponseDto.getId()), Long.class)
                );
    }

    @Test
    @SneakyThrows
    void approveBookingShouldReturnBadRequestWhenUserIdHeaderIsAbsent() {
        BookingResponseDto bookingResponseDto = getBookingResponse();

        when(bookingService.approveBooking(anyLong(), anyBoolean(), anyLong()))
                .thenReturn(bookingResponseDto);

        mockMvc.perform(patch("/bookings/1")
                        .param("approved", "true")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(bookingService, never()).approveBooking(anyLong(), anyBoolean(), anyLong());
    }

    @Test
    @SneakyThrows
    void getAllUserBookingsShouldReturnBookingWhenRequestIsCorrect() {
        List<BookingResponseDto> bookingResponseDtoList = List.of(getBookingResponse());

        when(bookingService.getAllUserBookings(any(), any()))
                .thenReturn(bookingResponseDtoList);

        mockMvc.perform(get("/bookings")
                        .param("state", "WAITING")
                        .header("X-Sharer-User-Id", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$", hasSize(1)),
                        jsonPath("$[0].id", is(bookingResponseDtoList.get(0).getId()), Long.class)
                );
    }

    @Test
    @SneakyThrows
    void getAllUserItemBookingsShouldReturnBookingWhenRequestIsCorrect() {
        List<BookingResponseDto> bookingResponseDtoList = List.of(getBookingResponse());

        when(bookingService.getAllUserBookings(any(), any()))
                .thenReturn(bookingResponseDtoList);

        mockMvc.perform(get("/bookings/owner")
                        .param("state", "WAITING")
                        .header("X-Sharer-User-Id", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$", hasSize(1)),
                        jsonPath("$[0].id", is(bookingResponseDtoList.get(0).getId()), Long.class)
                );
    }
}