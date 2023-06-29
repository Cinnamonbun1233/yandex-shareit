package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.client.RequestItemClient;
import ru.practicum.shareit.request.dto.RequestItemDto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {
    private final RequestItemClient requestClient;

    @PostMapping
    public ResponseEntity<Object> addNewRequest(@Valid @RequestBody RequestItemDto request,
                                                @RequestHeader("X-Sharer-User-Id") @NotNull Long ownerId) {
        return requestClient.createNewRequest(request, ownerId);
    }

    @GetMapping
    public ResponseEntity<Object> getRequestsByUserId(@RequestHeader("X-Sharer-User-Id") @NotNull Long userId) {
        return requestClient.getRequestsByUserId(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequestsByUserId(@RequestHeader("X-Sharer-User-Id") @NotNull Long userId,
                                                         @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                         @RequestParam(defaultValue = "10") int size) {
        return requestClient.getAllRequestsByUserId(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestByUserId(@RequestHeader("X-Sharer-User-Id") @NotNull Long userId,
                                                     @PathVariable Long requestId) {
        return requestClient.getRequestByUserId(userId, requestId);
    }
}