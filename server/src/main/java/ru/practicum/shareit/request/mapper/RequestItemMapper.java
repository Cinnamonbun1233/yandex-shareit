package ru.practicum.shareit.request.mapper;

import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.request.dto.RequestItemRequestDto;
import ru.practicum.shareit.request.dto.RequestItemResponseDto;
import ru.practicum.shareit.request.model.RequestItem;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RequestItemMapper {
    public static RequestItemRequestDto requestItemToRequestItemRequestDto(RequestItem requestItem) {
        return RequestItemRequestDto.builder()
                .id(requestItem.getId())
                .description(requestItem.getDescription())
                .created(requestItem.getCreated())
                .build();
    }

    public static RequestItem requestItemRequestDtoToRequestItem(RequestItemRequestDto requestItemRequestDto,
                                                                 User requestor) {
        return RequestItem.builder()
                .description(requestItemRequestDto.getDescription())
                .requestor(requestor)
                .created(LocalDateTime.now())
                .items(Collections.emptyList())
                .build();
    }

    public static RequestItemResponseDto requestItemToRequestItemResponseDto(RequestItem requestItem) {
        return RequestItemResponseDto.builder()
                .id(requestItem.getId())
                .description(requestItem.getDescription())
                .created(requestItem.getCreated())
                .items(ItemMapper.itemsToItemShortResponseDtoList(requestItem.getItems()))
                .build();

    }

    public static List<RequestItemResponseDto> requestsToRequestItemResponseDtoList(List<RequestItem> requests) {
        List<RequestItemResponseDto> requestItemResponseDtoList = new ArrayList<>();

        for (RequestItem request : requests) {
            requestItemResponseDtoList.add(requestItemToRequestItemResponseDto(request));
        }

        return requestItemResponseDtoList;
    }
}