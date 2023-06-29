package ru.practicum.shareit.request.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.RequestItemRequestDto;
import ru.practicum.shareit.request.dto.RequestItemResponseDto;
import ru.practicum.shareit.request.model.RequestItem;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class RequestItemMapperTest {
    private static RequestItem getRequest(User requestor) {
        return RequestItem.builder()
                .id(1L)
                .description("Грабли для уборки листвы")
                .requestor(requestor)
                .created(LocalDateTime.now())
                .build();
    }

    private static RequestItemRequestDto getRequestDto() {
        return RequestItemRequestDto.builder()
                .id(1L)
                .description("Грабли для уборки листвы")
                .created(LocalDateTime.now())
                .build();
    }

    private static User getUser(Long id, String email) {
        return User.builder()
                .id(id)
                .name("Дима")
                .email(email)
                .build();
    }

    private static Item getItem(User owner) {
        return Item.builder()
                .id(1L)
                .name("Грабли")
                .description("Грабли для уборки листвы")
                .available(true)
                .owner(owner)
                .build();
    }

    @Test
    void toRequestItemDto() {
        User requestor = getUser(1L, "dima@yandex.ru");
        RequestItem requestItem = getRequest(requestor);

        RequestItemRequestDto result = RequestItemMapper.requestItemToRequestItemRequestDto(requestItem);

        assertThat(result, notNullValue());
        assertThat(result.getId(), equalTo(requestItem.getId()));
        assertThat(result.getDescription(), equalTo(requestItem.getDescription()));
        assertThat(result.getCreated(), equalTo(requestItem.getCreated()));
    }

    @Test
    void dtoToRequest() {
        User requestor = getUser(1L, "dima@yandex.ru");
        RequestItemRequestDto requestItemRequestDto = getRequestDto();

        RequestItem result = RequestItemMapper.requestItemRequestDtoToRequestItem(requestItemRequestDto, requestor);

        assertThat(result, notNullValue());
        assertThat(result.getDescription(), equalTo(requestItemRequestDto.getDescription()));
        assertThat(result.getCreated(), notNullValue());
        assertThat(result.getItems(), empty());
    }

    @Test
    void toResponseDto() {
        User requestor = getUser(1L, "dima@yandex.ru");
        User owner = getUser(2L, "fima@yandex.ru");
        Item item = getItem(owner);
        RequestItem requestItem = getRequest(requestor);
        requestItem.setItems(List.of(item));

        RequestItemResponseDto result = RequestItemMapper.requestItemToRequestItemResponseDto(requestItem);

        assertThat(result, notNullValue());
        assertThat(result.getId(), equalTo(requestItem.getId()));
        assertThat(result.getDescription(), equalTo(requestItem.getDescription()));
        assertThat(result.getCreated(), equalTo(requestItem.getCreated()));
        assertThat(result.getItems(), not(empty()));
        assertThat(result.getItems(), hasItem(allOf(
                hasProperty("id", equalTo(item.getId())),
                hasProperty("name", equalTo(item.getName()))
        )));
    }
}