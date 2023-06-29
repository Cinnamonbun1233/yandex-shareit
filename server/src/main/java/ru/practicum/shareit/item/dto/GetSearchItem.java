package ru.practicum.shareit.item.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GetSearchItem {
    private String text;
    private Long userId;
    private Long itemId;
    private int from;
    private int size;
    private boolean isBlank;

    public static GetSearchItem of(String text, Long userId, int from, int size) {
        GetSearchItem search = new GetSearchItem();
        search.setBlank(text.isBlank());
        search.setUserId(userId);
        search.setFrom(from > 0 ? from / size : 0);
        search.setSize(size);
        search.setText(text);
        return search;
    }

    public static GetSearchItem of(String text, Long userId, Long itemId, int from, int size) {
        GetSearchItem search = new GetSearchItem();
        search.setBlank(text.isBlank());
        search.setUserId(userId);
        search.setItemId(itemId);
        search.setFrom(from > 0 ? from / size : 0);
        search.setSize(size);
        search.setText(text);
        return search;
    }
}