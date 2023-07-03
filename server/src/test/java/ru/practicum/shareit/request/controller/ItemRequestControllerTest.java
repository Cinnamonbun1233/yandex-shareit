package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.RequestItemRequestDto;
import ru.practicum.shareit.request.dto.RequestItemResponseDto;
import ru.practicum.shareit.request.service.RequestItemService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private RequestItemService requestItemService;
    @Autowired
    private MockMvc mockMvc;

    private static RequestItemRequestDto getRequestDto() {
        return RequestItemRequestDto.builder()
                .id(1L)
                .description("Грабли для уборки листвы")
                .created(LocalDateTime.now())
                .build();
    }

    private static RequestItemResponseDto getResponseDto() {
        return RequestItemResponseDto.builder()
                .id(1L)
                .description("Грабли для уборки листвы")
                .created(LocalDateTime.now())
                .items(Collections.emptyList())
                .build();
    }

    @Test
    @SneakyThrows
    void addNewRequestShouldAddRequestWhenRequestIsValid() {
        RequestItemRequestDto requestItemRequestDto = getRequestDto();

        when(requestItemService.createNewRequest(any(), anyLong()))
                .thenReturn(requestItemRequestDto);

        mockMvc.perform(post("/requests")
                        .content(objectMapper.writeValueAsString(requestItemRequestDto))
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id", is(requestItemRequestDto.getId()), Long.class),
                        jsonPath("$.description", containsString("Грабли для уборки листвы")),
                        jsonPath("$.created").exists()
                );
    }

    @Test
    @SneakyThrows
    void getRequestByIdShouldReturnRequestWhenRequestIsValid() {
        RequestItemResponseDto requestItemResponseDto = getResponseDto();

        when(requestItemService.getRequestByUserId(anyLong(), anyLong()))
                .thenReturn(requestItemResponseDto);
        mockMvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id", is(requestItemResponseDto.getId()), Long.class),
                        jsonPath("$.description", containsString("Грабли для уборки листвы")),
                        jsonPath("$.created", notNullValue()),
                        jsonPath("$.items", empty())
                );
    }

    @Test
    @SneakyThrows
    void getRequestByIdShouldReturnBadRequestWhenPathVariableIsIncorrect() {
        RequestItemResponseDto requestItemResponseDto = getResponseDto();

        when(requestItemService.getRequestByUserId(anyLong(), anyLong()))
                .thenReturn(requestItemResponseDto);
        mockMvc.perform(get("/requests/null")
                        .header("X-Sharer-User-Id", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(requestItemService, never()).getRequestByUserId(anyLong(), anyLong());
    }

    @Test
    @SneakyThrows
    void getRequestByIdShouldReturnBadRequestWhenUserIdHeaderIsAbsent() {
        RequestItemResponseDto requestItemResponseDto = getResponseDto();

        when(requestItemService.getRequestByUserId(anyLong(), anyLong()))
                .thenReturn(requestItemResponseDto);
        mockMvc.perform(get("/requests/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(requestItemService, never()).getRequestByUserId(anyLong(), anyLong());
    }

    @Test
    @SneakyThrows
    void getAllRequests() {
        List<RequestItemResponseDto> requestItemResponseDtoList = List.of(getResponseDto());

        when(requestItemService.getAllRequestsByUserId(1L, 0, 1))
                .thenReturn(requestItemResponseDtoList);
        mockMvc.perform(get("/requests/all")
                        .param("from", "0")
                        .param("size", "1")
                        .header("X-Sharer-User-Id", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$", hasSize(requestItemResponseDtoList.size())),
                        jsonPath("$[0].id", is(1L), Long.class),
                        jsonPath("$[0].description", containsString("Грабли для уборки листвы")),
                        jsonPath("$[0].created", notNullValue()),
                        jsonPath("$[0].items", empty())
                );
    }

    @Test
    @SneakyThrows
    void getRequestsByUserId() {
        List<RequestItemResponseDto> requestItemResponseDtoList = List.of(getResponseDto());

        when(requestItemService.getAllRequestsByUserId(1L))
                .thenReturn(requestItemResponseDtoList);
        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$", hasSize(requestItemResponseDtoList.size())),
                        jsonPath("$[0].id", is(1L), Long.class),
                        jsonPath("$[0].description", containsString("Грабли для уборки листвы")),
                        jsonPath("$[0].created", notNullValue()),
                        jsonPath("$[0].items", empty())
                );
    }
}