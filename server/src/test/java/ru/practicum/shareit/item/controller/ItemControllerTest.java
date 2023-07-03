package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ItemService itemService;

    private static CommentRequestDto getCommentRequestDto() {
        return CommentRequestDto.builder()
                .text("Отличные грабли")
                .build();
    }

    private static CommentResponseDto getCommentResponseDto() {
        return CommentResponseDto.builder()
                .id(1L)
                .text("Отличные грабли")
                .authorName("Дима")
                .created(LocalDateTime.now())
                .build();
    }

    private static ItemRequestDto getItemRequestDto() {
        return ItemRequestDto.builder()
                .name("Грабли")
                .description("Грабли для уборки листвы")
                .available(true)
                .requestId(1L)
                .build();
    }

    private static ItemShortResponseDto getItemShortDto() {
        return ItemShortResponseDto.builder()
                .id(1L)
                .name("Грабли")
                .description("Грабли для уборки листвы")
                .available(true)
                .requestId(1L)
                .build();
    }

    private static BookingShortDto getBookingShort(LocalDateTime start, LocalDateTime end) {
        return BookingShortDto.builder()
                .id(1L)
                .bookerId(1L)
                .start(start)
                .end(end)
                .build();
    }

    private static ItemResponseDto getItemResponseDto(LocalDateTime now) {
        return ItemResponseDto.builder()
                .id(1L)
                .name("Грабли")
                .description("Грабли для уборки листвы")
                .available(true)
                .nextBooking(getBookingShort(now.plusMinutes(1), now.plusDays(1)))
                .lastBooking(getBookingShort(now.minusDays(1), now.minusHours(1)))
                .comments(Collections.emptyList())
                .build();
    }

    @Test
    @SneakyThrows
    void addItemShouldAddItemWhenRequestIsCorrect() {
        ItemRequestDto itemRequestDto = getItemRequestDto();
        ItemShortResponseDto itemShortResponseDto = getItemShortDto();

        when(itemService.createNewItem(any(), anyLong()))
                .thenReturn(itemShortResponseDto);

        mockMvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.id", is(itemShortResponseDto.getId()), Long.class),
                        jsonPath("$.name", equalTo(itemShortResponseDto.getName())),
                        jsonPath("$.description", containsString("Грабли для уборки листвы")),
                        jsonPath("$.available", equalTo(itemShortResponseDto.getAvailable())),
                        jsonPath("$.requestId", notNullValue())
                );
    }

    @Test
    @SneakyThrows
    void addItemShouldReturnBadRequestUserIdHeaderIsNotPresent() {
        ItemRequestDto itemRequestDto = getItemRequestDto();
        ItemShortResponseDto itemShortResponseDto = getItemShortDto();

        when(itemService.createNewItem(any(), anyLong()))
                .thenReturn(itemShortResponseDto);

        mockMvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(itemService, never()).updateItemByUserId(any(), anyLong());
    }

    @Test
    @SneakyThrows
    void updateItemShouldUpdateItemWhenRequestIsCorrect() {
        ItemRequestDto itemRequestDto = getItemRequestDto();
        ItemShortResponseDto itemShortResponseDto = getItemShortDto();

        when(itemService.updateItemByUserId(any(), anyLong()))
                .thenReturn(itemShortResponseDto);

        mockMvc.perform(patch("/items/1")
                        .content(objectMapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.id", is(itemShortResponseDto.getId()), Long.class),
                        jsonPath("$.name", equalTo(itemShortResponseDto.getName())),
                        jsonPath("$.description", containsString("Грабли для уборки листвы")),
                        jsonPath("$.available", equalTo(itemShortResponseDto.getAvailable())),
                        jsonPath("$.requestId", notNullValue())
                );
        verify(itemService, Mockito.times(1)).updateItemByUserId(any(), anyLong());
        verifyNoMoreInteractions(itemService);
    }

    @Test
    @SneakyThrows
    void updateItemShouldReturnBadRequestUserIdHeaderIsNotPresent() {
        ItemRequestDto itemRequestDto = getItemRequestDto();
        ItemShortResponseDto itemShortResponseDto = getItemShortDto();

        when(itemService.updateItemByUserId(any(), anyLong()))
                .thenReturn(itemShortResponseDto);

        mockMvc.perform(patch("/items/1")
                        .content(objectMapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(itemService, never()).updateItemByUserId(any(), anyLong());
    }

    @Test
    @SneakyThrows
    void getItemByIdShouldReturnItemWhenRequestIsCorrect() {
        LocalDateTime now = LocalDateTime.now();
        ItemResponseDto itemResponseDto = getItemResponseDto(now);

        when(itemService.getItemByUserId(anyLong(), anyLong()))
                .thenReturn(itemResponseDto);

        mockMvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id", is(itemResponseDto.getId()), Long.class),
                        jsonPath("$.name", containsString("Грабли")),
                        jsonPath("$.description", containsString("Грабли для уборки листвы")),
                        jsonPath("$.available", equalTo(true)),
                        jsonPath("$.nextBooking", notNullValue()),
                        jsonPath("$.lastBooking", notNullValue()),
                        jsonPath("$.comments", empty())
                );
    }

    @Test
    @SneakyThrows
    void getItemByIdShouldReturnBadRequestWhenPathVariableIsNull() {
        LocalDateTime now = LocalDateTime.now();
        ItemResponseDto itemResponseDto = getItemResponseDto(now);

        when(itemService.getItemByUserId(anyLong(), anyLong()))
                .thenReturn(itemResponseDto);

        mockMvc.perform(get("/items/null")
                        .header("X-Sharer-User-Id", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(itemService, never()).getItemByUserId(anyLong(), anyLong());
    }

    @Test
    @SneakyThrows
    void getItemsByOwner() {
        LocalDateTime now = LocalDateTime.now();
        ItemResponseDto itemResponseDto = getItemResponseDto(now);

        when(itemService.getAllItemsByUserId(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(itemResponseDto));

        mockMvc.perform(get("/items/")
                        .param("from", "0")
                        .param("size", "10")
                        .header("X-Sharer-User-Id", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$", hasSize(1)),
                        jsonPath("$[0].id", is(itemResponseDto.getId()), Long.class),
                        jsonPath("$[0].name", containsString("Грабли")),
                        jsonPath("$[0].comments", empty())
                );
    }

    @Test
    @SneakyThrows
    void searchShouldReturnItemsWhenSearchRequestIsCorrect() {
        ItemRequestDto itemRequestDto = getItemRequestDto();

        when(itemService.search(any()))
                .thenReturn(List.of(itemRequestDto));

        mockMvc.perform(get("/items/search")
                        .param("text", "Грабли")
                        .param("from", "0")
                        .param("size", "10")
                        .header("X-Sharer-User-Id", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$", hasSize(1)),
                        jsonPath("$[0].requestId", is(itemRequestDto.getRequestId()), Long.class),
                        jsonPath("$[0].name", containsString("Грабли"))
                );
    }

    @Test
    @SneakyThrows
    void searchCommentsByTextShouldReturnCommentsWhenSearchRequestIsCorrect() {
        CommentResponseDto commentResponseDto = getCommentResponseDto();

        when(itemService.searchCommentsByText(any()))
                .thenReturn(List.of(commentResponseDto));

        mockMvc.perform(get("/items/1/comment/search")
                        .param("text", "goOD")
                        .param("from", "0")
                        .param("size", "10")
                        .header("X-Sharer-User-Id", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$", hasSize(1)),
                        jsonPath("$[0].id", is(commentResponseDto.getId()), Long.class),
                        jsonPath("$[0].text", containsString("Отличные грабли"))
                );
    }

    @Test
    @SneakyThrows
    void addCommentShouldAddCommentWhenRequestIsCorrect() {
        CommentResponseDto commentResponseDto = getCommentResponseDto();
        CommentRequestDto commentRequestDto = getCommentRequestDto();

        when(itemService.createNewComment(anyLong(), any(), anyLong()))
                .thenReturn(commentResponseDto);

        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", "1")
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.text", containsString("Отличные грабли")),
                        jsonPath("$.id", is(commentResponseDto.getId()), Long.class),
                        jsonPath("$.authorName", containsString("Дима")),
                        jsonPath("$.created", notNullValue())
                );
    }
}