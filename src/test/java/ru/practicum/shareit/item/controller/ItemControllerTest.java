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
import ru.practicum.shareit.booking.dto.BookingShortResponseDto;
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
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;
    @MockBean
    private ItemService itemService;

    private static CommentRequestDto getCommentRequestDto() {
        return CommentRequestDto.builder()
                .text("very good")
                .build();
    }

    private static CommentResponseDto getCommentResponseDto() {
        return CommentResponseDto.builder()
                .id(1L)
                .text("very good")
                .authorName("Alex")
                .created(LocalDateTime.now())
                .build();
    }

    private static ItemRequestDto getItemRequestDto() {
        return ItemRequestDto.builder()
                .name("item")
                .description("some Item")
                .available(true)
                .requestId(1L)
                .build();
    }

    private static ItemShortResponseDto getItemShortDto() {
        return ItemShortResponseDto.builder()
                .id(1L)
                .name("item")
                .description("some item")
                .available(true)
                .requestId(1L)
                .build();
    }

    private static BookingShortResponseDto getBookingShort(LocalDateTime start, LocalDateTime end) {
        return BookingShortResponseDto.builder()
                .id(1L)
                .bookerId(1L)
                .start(start)
                .end(end)
                .build();
    }

    private static ItemResponseDto getItemResponseDto(LocalDateTime now) {
        return ItemResponseDto.builder()
                .id(1L)
                .name("brush")
                .description("good brush")
                .available(true)
                .nextBooking(getBookingShort(now.plusMinutes(1), now.plusDays(1)))
                .lastBooking(getBookingShort(now.minusDays(1), now.minusHours(1)))
                .comments(Collections.emptyList())
                .build();
    }

    @Test
    @SneakyThrows
    void addItem_shouldAddItem_whenRequestIsCorrect() {
        // given
        ItemRequestDto dto = getItemRequestDto();
        ItemShortResponseDto item = getItemShortDto();
        // when
        when(itemService.createNewItem(any(), anyLong()))
                .thenReturn(item);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                // then
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.id", is(item.getId()), Long.class),
                        jsonPath("$.name", equalTo(item.getName())),
                        jsonPath("$.description", containsString("some item")),
                        jsonPath("$.available", equalTo(item.getAvailable())),
                        jsonPath("$.requestId", notNullValue())
                );
    }

    @Test
    @SneakyThrows
    void addItem_shouldReturnBadRequest_UserIdHeaderIsNotPresent() {
        // given
        ItemRequestDto dto = getItemRequestDto();
        ItemShortResponseDto item = getItemShortDto();
        // when
        when(itemService.createNewItem(any(), anyLong()))
                .thenReturn(item);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                // then
                .andExpect(status().isBadRequest());
        verify(itemService, never()).updateItemById(any(), anyLong());
    }

    @Test
    @SneakyThrows
    void updateItem_shouldUpdateItem_whenRequestIsCorrect() {
        // given
        ItemRequestDto dto = getItemRequestDto();
        ItemShortResponseDto item = getItemShortDto();
        // when
        when(itemService.updateItemById(any(), anyLong()))
                .thenReturn(item);

        mvc.perform(patch("/items/1")
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                // then
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.id", is(item.getId()), Long.class),
                        jsonPath("$.name", equalTo(item.getName())),
                        jsonPath("$.description", containsString("some item")),
                        jsonPath("$.available", equalTo(item.getAvailable())),
                        jsonPath("$.requestId", notNullValue())
                );
        verify(itemService, Mockito.times(1)).updateItemById(any(), anyLong());
        verifyNoMoreInteractions(itemService);
    }

    @Test
    @SneakyThrows
    void updateItem_shouldReturnBadRequest_UserIdHeaderIsNotPresent() {
        // given
        ItemRequestDto dto = getItemRequestDto();
        ItemShortResponseDto item = getItemShortDto();
        // when
        when(itemService.updateItemById(any(), anyLong()))
                .thenReturn(item);

        mvc.perform(patch("/items/1")
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                // then
                .andExpect(status().isBadRequest());
        verify(itemService, never()).updateItemById(any(), anyLong());
    }

    @Test
    @SneakyThrows
    void getItemById_shouldReturnItem_whenRequestIsCorrect() {
        LocalDateTime now = LocalDateTime.now();
        ItemResponseDto responseDto = getItemResponseDto(now);

        when(itemService.getItemById(anyLong(), anyLong()))
                .thenReturn(responseDto);

        mvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id", is(responseDto.getId()), Long.class),
                        jsonPath("$.name", containsString("brush")),
                        jsonPath("$.description", containsString("good brush")),
                        jsonPath("$.available", equalTo(true)),
                        jsonPath("$.nextBooking", notNullValue()),
                        jsonPath("$.lastBooking", notNullValue()),
                        jsonPath("$.comments", empty())
                );
    }

    @Test
    @SneakyThrows
    void getItemById_shouldReturnBadRequest_whenPathVariableIsNull() {
        LocalDateTime now = LocalDateTime.now();
        ItemResponseDto responseDto = getItemResponseDto(now);

        when(itemService.getItemById(anyLong(), anyLong()))
                .thenReturn(responseDto);

        mvc.perform(get("/items/null")
                        .header("X-Sharer-User-Id", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(itemService, never()).getItemById(anyLong(), anyLong());
    }

    @Test
    @SneakyThrows
    void getItemsByOwner() {
        LocalDateTime now = LocalDateTime.now();
        ItemResponseDto responseDto = getItemResponseDto(now);

        when(itemService.getAllItemsById(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(responseDto));

        mvc.perform(get("/items/")
                        .param("from", "0")
                        .param("size", "10")
                        .header("X-Sharer-User-Id", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$", hasSize(1)),
                        jsonPath("$[0].id", is(responseDto.getId()), Long.class),
                        jsonPath("$[0].name", containsString("brush")),
                        jsonPath("$[0].comments", empty())
                );
    }

    @Test
    @SneakyThrows
    void search_shouldReturnItems_whenSearchRequestIsCorrect() {
        ItemRequestDto requestDto = getItemRequestDto();

        when(itemService.search(any()))
                .thenReturn(List.of(requestDto));

        mvc.perform(get("/items/search")
                        .param("text", "bRusH")
                        .param("from", "0")
                        .param("size", "10")
                        .header("X-Sharer-User-Id", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$", hasSize(1)),
                        jsonPath("$[0].requestId", is(requestDto.getRequestId()), Long.class),
                        jsonPath("$[0].name", containsString("item"))
                );
    }

    @Test
    @SneakyThrows
    void searchCommentsByText_shouldReturnComments_whenSearchRequestIsCorrect() {
        CommentResponseDto comment = getCommentResponseDto();

        when(itemService.searchCommentsByText(any()))
                .thenReturn(List.of(comment));

        mvc.perform(get("/items/1/comment/search")
                        .param("text", "goOD")
                        .param("from", "0")
                        .param("size", "10")
                        .header("X-Sharer-User-Id", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$", hasSize(1)),
                        jsonPath("$[0].id", is(comment.getId()), Long.class),
                        jsonPath("$[0].text", containsString("very good"))
                );
    }

    @Test
    @SneakyThrows
    void addComment_shouldAddComment_whenRequestIsCorrect() {
        CommentResponseDto comment = getCommentResponseDto();
        CommentRequestDto requestDto = getCommentRequestDto();

        when(itemService.createNewComment(anyLong(), any(), anyLong()))
                .thenReturn(comment);

        mvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", "1")
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.text", containsString("very good")),
                        jsonPath("$.id", is(comment.getId()), Long.class),
                        jsonPath("$.authorName", containsString("Alex")),
                        jsonPath("$.created", notNullValue())
                );
    }

    @Test
    @SneakyThrows
    void addComment_shouldReturnBadRequest_whenTextIsBlank() {
        CommentResponseDto comment = getCommentResponseDto();
        CommentRequestDto requestDto = getCommentRequestDto();
        requestDto.setText("");

        when(itemService.createNewComment(anyLong(), any(), anyLong()))
                .thenReturn(comment);

        mvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", "1")
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(itemService, never()).createNewComment(anyLong(), any(), anyLong());
    }
}