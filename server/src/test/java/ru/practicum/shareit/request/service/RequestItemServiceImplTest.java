package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import ru.practicum.shareit.exception.RequestNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.request.dto.RequestItemRequestDto;
import ru.practicum.shareit.request.dto.RequestItemResponseDto;
import ru.practicum.shareit.request.mapper.RequestItemMapper;
import ru.practicum.shareit.request.model.RequestItem;
import ru.practicum.shareit.request.repository.RequestItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class RequestItemServiceImplTest {
    @Mock
    private RequestItemRepository requestItemRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private RequestItemServiceImpl requestItemService;
    private RequestItemRequestDto requestItemRequestDto;
    private User requestor;

    private static User getUser() {
        return User.builder()
                .id(1L)
                .name("Дима")
                .email("dima@yandex.ru")
                .build();
    }

    private static RequestItemRequestDto getRequestDto() {
        return RequestItemRequestDto.builder()
                .id(1L)
                .description("Грабли для уборки листвы")
                .created(LocalDateTime.now())
                .build();
    }

    @BeforeEach
    void init() {
        requestItemRequestDto = getRequestDto();
        requestor = getUser();
    }

    @Test
    void addNewRequestShouldThrowUserNotFoundException() {
        when(userRepository.findById(anyLong()))
                .thenThrow(new UserNotFoundException(String.format("Пользователь с id: %s не обнаружен", 1L)));

        final UserNotFoundException userNotFoundException = assertThrows(UserNotFoundException.class, ()
                -> requestItemService.createNewRequest(requestItemRequestDto, 1L));

        assertThat(userNotFoundException.getMessage(), containsString("Пользователь с id: 1 не обнаружен"));
    }

    @Test
    void addNewRequestShouldReturnRequestDto() {
        RequestItem requestItem = RequestItemMapper.requestItemRequestDtoToRequestItem(requestItemRequestDto, requestor);

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(requestor));
        when(requestItemRepository.save(Mockito.any()))
                .thenReturn(requestItem);
        RequestItemRequestDto requestItemRequestDtoAfter = requestItemService.createNewRequest(requestItemRequestDto, 1L);

        assertThat(requestItemRequestDtoAfter.getDescription(), equalTo("Грабли для уборки листвы"));
        assertThat(requestItemRequestDtoAfter, instanceOf(RequestItemRequestDto.class));

        verify(userRepository, Mockito.times(1)).findById(1L);
        verify(requestItemRepository, Mockito.times(1)).save(any());
        verify(userRepository, never()).existsById(anyLong());
        verifyNoMoreInteractions(requestItemRepository, userRepository);
    }

    @Test
    void getAllRequestsShouldThrowUserNotFoundException() {
        when(userRepository.existsById(anyLong()))
                .thenReturn(false);

        final UserNotFoundException userNotFoundException = assertThrows(UserNotFoundException.class, ()
                -> requestItemService.getAllRequestsByUserId(1L, 0, 10));

        assertThat(userNotFoundException.getMessage(), containsString("Пользователь с id: 1 не обнаружен"));
    }

    @Test
    void getAllRequestsShouldReturnRequestList() {
        RequestItem requestItem = RequestItemMapper.requestItemRequestDtoToRequestItem(requestItemRequestDto, requestor);
        Page<RequestItem> page = new PageImpl<>(List.of(requestItem));

        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(requestItemRepository.findAllPaged(any(), anyLong()))
                .thenReturn(page);
        List<RequestItemResponseDto> requestItemResponseDtoList =
                requestItemService.getAllRequestsByUserId(1L, 0, 10);

        assertThat(requestItemResponseDtoList, hasSize(requestItemResponseDtoList.size()));
        assertThat(requestItemResponseDtoList, hasItem(allOf(
                hasProperty("description", equalTo("Грабли для уборки листвы")),
                hasProperty("created", notNullValue())
        )));
        assertThat(requestItemResponseDtoList, instanceOf(List.class));

        verify(userRepository, Mockito.times(1)).existsById(anyLong());
        verify(requestItemRepository, Mockito.times(1)).findAllPaged(any(), anyLong());
        verifyNoMoreInteractions(userRepository, requestItemRepository);
    }

    @Test
    void getRequestsShouldThrowUserNotFoundException() {
        when(userRepository.existsById(anyLong()))
                .thenReturn(false);

        final UserNotFoundException userNotFoundException = assertThrows(UserNotFoundException.class, ()
                -> requestItemService.getAllRequestsByUserId(1L));
        assertThat(userNotFoundException.getMessage(), containsString("Пользователь с id: 1 не обнаружен"));
    }

    @Test
    void getRequestsShouldReturnRequestsList() {
        RequestItem requestItem = RequestItemMapper.requestItemRequestDtoToRequestItem(requestItemRequestDto, requestor);

        when(userRepository.existsById(anyLong()))
                .thenReturn(true);

        when(requestItemRepository.findAllByRequestorId(anyLong()))
                .thenReturn(List.of(requestItem));
        List<RequestItemResponseDto> requestItemResponseDtoList = requestItemService.getAllRequestsByUserId(1L);

        assertThat(requestItemResponseDtoList, hasSize(requestItemResponseDtoList.size()));
        assertThat(requestItemResponseDtoList, hasItem(allOf(
                hasProperty("description", equalTo("Грабли для уборки листвы")),
                hasProperty("created", notNullValue())
        )));
        assertThat(requestItemResponseDtoList, instanceOf(List.class));

        verify(userRepository, Mockito.times(1)).existsById(anyLong());
        verify(requestItemRepository, Mockito.times(1)).findAllByRequestorId(1L);
        verifyNoMoreInteractions(userRepository, requestItemRepository);
    }

    @Test
    void getRequestByIdShouldThrowUserNotFoundException() {
        when(userRepository.existsById(anyLong()))
                .thenReturn(false);

        final UserNotFoundException userNotFoundException = assertThrows(UserNotFoundException.class, ()
                -> requestItemService.getRequestByUserId(1L, 1L));
        assertThat(userNotFoundException.getMessage(), containsString("Пользователь с id: 1 не обнаружен"));
    }

    @Test
    void getRequestByIdShouldThrowRequestNotFoundException() {
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(requestItemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        final RequestNotFoundException requestNotFoundException = assertThrows(RequestNotFoundException.class, ()
                -> requestItemService.getRequestByUserId(1L, 1L));
        assertThat(requestNotFoundException.getMessage(), containsString("Запрос с id: 1 не обнаружен"));
    }

    @Test
    void getRequestByIdShouldReturnRequestResponseDto() {
        RequestItem requestItem = RequestItemMapper.requestItemRequestDtoToRequestItem(requestItemRequestDto, requestor);
        requestItem.setId(1L);

        when(userRepository.existsById(anyLong()))
                .thenReturn(true);

        when(requestItemRepository.findById(anyLong()))
                .thenReturn(Optional.of(requestItem));
        RequestItemResponseDto requestItemResponseDto = requestItemService.getRequestByUserId(1L, 1L);

        assertThat(requestItemResponseDto, notNullValue());
        assertThat(requestItemResponseDto, allOf(
                hasProperty("id", equalTo(1L)),
                hasProperty("description", containsStringIgnoringCase("Грабли для уборки листвы")),
                hasProperty("created", notNullValue())
        ));
        assertThat(requestItemResponseDto, instanceOf(RequestItemResponseDto.class));

        verify(userRepository, Mockito.times(1)).existsById(1L);
        verify(requestItemRepository, Mockito.times(1)).findById(1L);
        verifyNoMoreInteractions(userRepository, requestItemRepository);
    }
}