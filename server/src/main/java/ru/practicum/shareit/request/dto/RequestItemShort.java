package ru.practicum.shareit.request.dto;

import java.time.LocalDateTime;

//Готово
public interface RequestItemShort {
    Long getId();

    String getName();

    LocalDateTime getCreated();
}