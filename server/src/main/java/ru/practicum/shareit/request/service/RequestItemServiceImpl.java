package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.RequestNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.request.repository.RequestItemRepository;
import ru.practicum.shareit.request.dto.RequestItemRequestDto;
import ru.practicum.shareit.request.dto.RequestItemResponseDto;
import ru.practicum.shareit.request.model.RequestItem;
import ru.practicum.shareit.request.mapper.RequestItemMapper;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RequestItemServiceImpl implements RequestItemService {
    private final RequestItemRepository requestItemRepository;
    private final UserRepository userRepository;

    @Transactional
    public RequestItemRequestDto createNewRequest(RequestItemRequestDto requestItemRequestDto, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(()
                -> new UserNotFoundException(String.format("Пользователь с id: %s не обнаружен", userId)));
        RequestItem requestItem = RequestItemMapper.requestItemRequestDtoToRequestItem(requestItemRequestDto, user);
        return RequestItemMapper.requestItemToRequestItemRequestDto(requestItemRepository.save(requestItem));
    }

    public List<RequestItemResponseDto> getAllRequestsByUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(String.format("Пользователь с id: %s не обнаружен", userId));
        }

        List<RequestItem> requests = requestItemRepository.findAllByRequestorId(userId);
        return RequestItemMapper.requestsToRequestItemResponseDtoList(requests);
    }

    public List<RequestItemResponseDto> getAllRequestsByUserId(Long userId, int from, int size) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(String.format("Пользователь с id: %s не обнаружен", userId));
        }

        return requestItemRepository.findAllPaged(PageRequest.of(from > 0 ? from / size : 0, size,
                        Sort.by(Sort.Direction.DESC, "created")), userId)
                .map(RequestItemMapper::requestItemToRequestItemResponseDto)
                .getContent();
    }

    public RequestItemResponseDto getRequestByUserId(Long userId, Long requestId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(String.format("Пользователь с id: %s не обнаружен", userId));
        }

        return RequestItemMapper.requestItemToRequestItemResponseDto(requestItemRepository.findById(requestId)
                .orElseThrow(() -> new RequestNotFoundException(String.format("Запрос с id: %s не обнаружен", requestId))));
    }
}