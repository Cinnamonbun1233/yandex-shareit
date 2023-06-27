package ru.practicum.shareit.item.dto;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GetSearchItemTest {
    @Test
    void ofIsBlankFieldShouldBeTrueFromShouldBeZero() {
        GetSearchItem getSearchItem = GetSearchItem.of("", 1L, 5, 10);
        assertTrue(getSearchItem.isBlank());
        assertThat(getSearchItem.getFrom(), Matchers.is(0));
    }

    @Test
    void ofOverloadedIsBlankFieldShouldBeTrueFromShouldBeZero() {
        GetSearchItem getSearchItem = GetSearchItem.of("", 1L, 1L, 5, 10);
        assertTrue(getSearchItem.isBlank());
        assertThat(getSearchItem.getFrom(), Matchers.is(0));
    }
}