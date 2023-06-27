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

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = RequestItemController.class)
class RequestItemControllerTest {
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private RequestItemService requestItemService;
    @Autowired
    private MockMvc mvc;

    private static RequestItemRequestDto getRequestDto() {
        return RequestItemRequestDto.builder()
                .id(1L)
                .description("Предмет невероятной красоты")
                .created(LocalDateTime.now())
                .build();
    }

    private static RequestItemResponseDto getResponseDto() {
        return RequestItemResponseDto.builder()
                .id(1L)
                .description("Предмета ужаснее нет")
                .created(LocalDateTime.now())
                .items(Collections.emptyList())
                .build();
    }

    @Test
    @SneakyThrows
    void addNewRequestShouldAddRequestWhenRequestIsValid() {
        RequestItemRequestDto dto = getRequestDto();

        when(requestItemService.createNewRequest(any(), anyLong()))
                .thenReturn(dto);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(dto))
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id", is(dto.getId()), Long.class),
                        jsonPath("$.description", containsString("Предмет невероятной красоты")),
                        jsonPath("$.created").exists()
                );
    }

    @Test
    @SneakyThrows
    void addNewRequestWithEmptyDescription() {
        RequestItemRequestDto dto = getRequestDto();
        dto.setDescription("");

        when(requestItemService.createNewRequest(any(), anyLong()))
                .thenReturn(dto);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .locale(Locale.ENGLISH)
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(h -> System.out.println(h.getResponse().getContentAsString()));
    }

    @Test
    @SneakyThrows
    void getRequestByIdShouldReturnRequestWhenRequestIsValid() {
        RequestItemResponseDto dto = getResponseDto();

        when(requestItemService.getRequestByUserId(anyLong(), anyLong()))
                .thenReturn(dto);
        mvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", "1")
                        .accept(MediaType.APPLICATION_JSON))

                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id", is(dto.getId()), Long.class),
                        jsonPath("$.description", containsString("Предмета ужаснее нет")),
                        jsonPath("$.created", notNullValue()),
                        jsonPath("$.items", empty())
                );
    }

    @Test
    @SneakyThrows
    void getRequestByIdShouldReturnBadRequestWhenPathVariableIsIncorrect() {
        RequestItemResponseDto dto = getResponseDto();

        when(requestItemService.getRequestByUserId(anyLong(), anyLong()))
                .thenReturn(dto);
        mvc.perform(get("/requests/null")
                        .header("X-Sharer-User-Id", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(requestItemService, never()).getRequestByUserId(anyLong(), anyLong());
    }

    @Test
    @SneakyThrows
    void getRequestByIdShouldReturnBadRequestWhenUserIdHeaderIsAbsent() {
        RequestItemResponseDto dto = getResponseDto();

        when(requestItemService.getRequestByUserId(anyLong(), anyLong()))
                .thenReturn(dto);
        mvc.perform(get("/requests/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(requestItemService, never()).getRequestByUserId(anyLong(), anyLong());
    }

    @Test
    @SneakyThrows
    void getAllRequests() {
        List<RequestItemResponseDto> dtos = List.of(getResponseDto());


        when(requestItemService.getAllRequests(1L, 0, 1))
                .thenReturn(dtos);
        mvc.perform(get("/requests/all")
                        .param("from", "0")
                        .param("size", "1")
                        .header("X-Sharer-User-Id", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$", hasSize(dtos.size())),
                        jsonPath("$[0].id", is(1L), Long.class),
                        jsonPath("$[0].description", containsString("Предмета ужаснее нет")),
                        jsonPath("$[0].created", notNullValue()),
                        jsonPath("$[0].items", empty())
                );
    }

    @Test
    @SneakyThrows
    void getRequestsByUserId() {
        List<RequestItemResponseDto> dtos = List.of(getResponseDto());


        when(requestItemService.getAllRequestsByUserId(1L))
                .thenReturn(dtos);
        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$", hasSize(dtos.size())),
                        jsonPath("$[0].id", is(1L), Long.class),
                        jsonPath("$[0].description", containsString("Предмета ужаснее нет")),
                        jsonPath("$[0].created", notNullValue()),
                        jsonPath("$[0].items", empty())
                );
    }
}