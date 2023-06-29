package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.RequestItemRequestDto;
import ru.practicum.shareit.request.dto.RequestItemResponseDto;

import java.util.List;

public interface RequestItemService {
    RequestItemRequestDto createNewRequest(RequestItemRequestDto requestItemRequestDto, Long userId);

    List<RequestItemResponseDto> getAllRequestsByUserId(Long userId);

    List<RequestItemResponseDto> getAllRequestsByUserId(Long userId, int from, int size);

    RequestItemResponseDto getRequestByUserId(Long userId, Long requestId);
}