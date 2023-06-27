package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestItemRequestDto;
import ru.practicum.shareit.request.dto.RequestItemResponseDto;
import ru.practicum.shareit.request.service.RequestItemService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class RequestItemController {
    private final RequestItemService service;

    @PostMapping
    public RequestItemRequestDto createNewRequest(@Valid @RequestBody RequestItemRequestDto requestItemRequestDto,
                                                  @RequestHeader("X-Sharer-User-Id") @NotNull Long ownerId) {
        return service.createNewRequest(requestItemRequestDto, ownerId);
    }

    @GetMapping
    public List<RequestItemResponseDto> getAllRequestsByUserId(@RequestHeader("X-Sharer-User-Id") @NotNull Long userId) {
        return service.getAllRequestsByUserId(userId);
    }

    @GetMapping("/all")
    public List<RequestItemResponseDto> getAllRequests(@RequestHeader("X-Sharer-User-Id") @NotNull Long userId,
                                                       @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                       @RequestParam(defaultValue = "10") int size) {
        return service.getAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public RequestItemResponseDto getRequestByUserId(@RequestHeader("X-Sharer-User-Id") @NotNull Long userId,
                                                     @PathVariable Long requestId) {
        return service.getRequestByUserId(userId, requestId);
    }
}