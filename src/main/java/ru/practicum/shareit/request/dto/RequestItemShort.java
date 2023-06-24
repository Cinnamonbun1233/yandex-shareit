package ru.practicum.shareit.request.dto;

import java.time.LocalDateTime;

public interface RequestItemShort {
    Long getId();

    String getName();

    LocalDateTime getCreated();
}