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
    private RequestItemRepository reqRepo;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private RequestItemServiceImpl requestItemService;
    private RequestItemRequestDto requestItemRequestDto;
    private User requestor;

    private static User getUser() {
        return User.builder()
                .id(1L)
                .name("Dima")
                .email("dima@yandex.ru")
                .build();
    }

    private static RequestItemRequestDto getRequestDto() {
        return RequestItemRequestDto.builder()
                .id(1L)
                .description("Предмет невероятной красоты")
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
                .thenThrow(new UserNotFoundException(String.format("Пользователь с id: '%s' не найден", 1L)));

        final UserNotFoundException ex = assertThrows(UserNotFoundException.class,
                () -> requestItemService.createNewRequest(requestItemRequestDto, 1L));

        assertThat(ex.getMessage(), containsString("Пользователь с id: '1' не найден"));
    }

    @Test
    void addNewRequestShouldReturnRequestDto() {
        RequestItem requestItem = RequestItemMapper.requestItemRequestDtoToRequestItem(requestItemRequestDto, requestor);

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(requestor));
        when(reqRepo.save(Mockito.any()))
                .thenReturn(requestItem);
        RequestItemRequestDto requestItemRequestDtoAfter = requestItemService.createNewRequest(requestItemRequestDto, 1L);

        assertThat(requestItemRequestDtoAfter.getDescription(), equalTo("Предмет невероятной красоты"));
        assertThat(requestItemRequestDtoAfter, instanceOf(RequestItemRequestDto.class));

        verify(userRepository, Mockito.times(1)).findById(1L);
        verify(reqRepo, Mockito.times(1)).save(any());
        verify(userRepository, never()).existsById(anyLong());
        verifyNoMoreInteractions(reqRepo, userRepository);
    }

    @Test
    void getAllRequestsShouldThrowUserNotFoundException() {
        when(userRepository.existsById(anyLong()))
                .thenReturn(false);

        final UserNotFoundException ex = assertThrows(UserNotFoundException.class,
                () -> requestItemService.getAllRequests(1L, 0, 10));

        assertThat(ex.getMessage(), containsString("Пользователь с id: '1' не найден"));
    }

    @Test
    void getAllRequestsShouldReturnRequestList() {
        RequestItem requestItem = RequestItemMapper.requestItemRequestDtoToRequestItem(requestItemRequestDto, requestor);
        Page<RequestItem> page = new PageImpl<>(List.of(requestItem));

        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(reqRepo.findAllPaged(any(), anyLong()))
                .thenReturn(page);
        List<RequestItemResponseDto> dtos = requestItemService.getAllRequests(1L, 0, 10);

        assertThat(dtos, hasSize(dtos.size()));
        assertThat(dtos, hasItem(allOf(
                hasProperty("description", equalTo("Предмет невероятной красоты")),
                hasProperty("created", notNullValue())
        )));
        assertThat(dtos, instanceOf(List.class));

        verify(userRepository, Mockito.times(1)).existsById(anyLong());
        verify(reqRepo, Mockito.times(1)).findAllPaged(any(), anyLong());
        verifyNoMoreInteractions(userRepository, reqRepo);
    }

    @Test
    void getRequestsShouldThrowUserNotFoundException() {
        when(userRepository.existsById(anyLong()))
                .thenReturn(false);

        final UserNotFoundException ex = assertThrows(UserNotFoundException.class,
                () -> requestItemService.getAllRequestsByUserId(1L));
        assertThat(ex.getMessage(), containsString("Пользователь с id: '1' не найден"));
    }

    @Test
    void getRequestsShouldReturnRequestsList() {
        RequestItem requestItem = RequestItemMapper.requestItemRequestDtoToRequestItem(requestItemRequestDto, requestor);

        when(userRepository.existsById(anyLong()))
                .thenReturn(true);

        when(reqRepo.findAllByRequestorId(anyLong()))
                .thenReturn(List.of(requestItem));
        List<RequestItemResponseDto> dtos = requestItemService.getAllRequestsByUserId(1L);

        assertThat(dtos, hasSize(dtos.size()));
        assertThat(dtos, hasItem(allOf(
                hasProperty("description", equalTo("Предмет невероятной красоты")),
                hasProperty("created", notNullValue())
        )));
        assertThat(dtos, instanceOf(List.class));

        verify(userRepository, Mockito.times(1)).existsById(anyLong());
        verify(reqRepo, Mockito.times(1)).findAllByRequestorId(1L);
        verifyNoMoreInteractions(userRepository, reqRepo);
    }

    @Test
    void getRequestByIdShouldThrowUserNotFoundException() {
        when(userRepository.existsById(anyLong()))
                .thenReturn(false);

        final UserNotFoundException ex = assertThrows(UserNotFoundException.class,
                () -> requestItemService.getRequestByUserId(1L, 1L));
        assertThat(ex.getMessage(), containsString("Пользователь с id: '1' не найден"));
    }

    @Test
    void getRequestByIdShouldThrowRequestNotFoundException() {
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(reqRepo.findById(anyLong()))
                .thenReturn(Optional.empty());

        final RequestNotFoundException ex = assertThrows(RequestNotFoundException.class,
                () -> requestItemService.getRequestByUserId(1L, 1L));
        assertThat(ex.getMessage(), containsString("Запрос с id: '1' не найден"));
    }

    @Test
    void getRequestByIdShouldReturnRequestResponseDto() {
        RequestItem requestItem = RequestItemMapper.requestItemRequestDtoToRequestItem(requestItemRequestDto, requestor);
        requestItem.setId(1L);

        when(userRepository.existsById(anyLong()))
                .thenReturn(true);

        when(reqRepo.findById(anyLong()))
                .thenReturn(Optional.of(requestItem));
        RequestItemResponseDto dto = requestItemService.getRequestByUserId(1L, 1L);

        assertThat(dto, notNullValue());
        assertThat(dto, allOf(
                hasProperty("id", equalTo(1L)),
                hasProperty("description", containsStringIgnoringCase("Предмет невероятной красоты")),
                hasProperty("created", notNullValue())
        ));
        assertThat(dto, instanceOf(RequestItemResponseDto.class));

        verify(userRepository, Mockito.times(1)).existsById(1L);
        verify(reqRepo, Mockito.times(1)).findById(1L);
        verifyNoMoreInteractions(userRepository, reqRepo);
    }
}