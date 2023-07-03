package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestItemRequestDto;
import ru.practicum.shareit.request.dto.RequestItemResponseDto;
import ru.practicum.shareit.request.service.RequestItemService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final RequestItemService requestItemService;

    @PostMapping
    public RequestItemRequestDto createNewRequest(@RequestBody RequestItemRequestDto requestItemRequestDto,
                                                  @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return requestItemService.createNewRequest(requestItemRequestDto, ownerId);
    }

    @GetMapping("/all")
    public List<RequestItemResponseDto> getAllRequestsByUserId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                               @RequestParam(defaultValue = "0") int from,
                                                               @RequestParam(defaultValue = "10") int size) {
        return requestItemService.getAllRequestsByUserId(userId, from, size);
    }

    @GetMapping
    public List<RequestItemResponseDto> getAllRequestsByUserId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return requestItemService.getAllRequestsByUserId(userId);
    }

    @GetMapping("/{requestId}")
    public RequestItemResponseDto getRequestByUserId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                     @PathVariable Long requestId) {
        return requestItemService.getRequestByUserId(userId, requestId);
    }
}