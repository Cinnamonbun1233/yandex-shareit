package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.client.RequestItemClient;
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.dto.RequestItemDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Locale;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private RequestItemClient requestItemClient;
    @Autowired
    private MockMvc mockMvc;

    private static RequestItemDto getRequestDto() {
        return RequestItemDto.builder()
                .id(1L)
                .description("Грабли для уборки листьев")
                .created(LocalDateTime.now())
                .build();
    }

    @Test
    @SneakyThrows
    void addNewRequestWithEmptyDescription() {
        RequestItemDto requestItemDto = getRequestDto();
        requestItemDto.setDescription("");

        mockMvc.perform(post("/requests")
                        .content(objectMapper.writeValueAsString(requestItemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .locale(Locale.ENGLISH)
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(h -> System.out.println(h.getResponse().getContentAsString()));
        verify(requestItemClient, never()).createNewRequest(any(), anyLong());
    }
}