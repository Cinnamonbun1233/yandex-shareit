package ru.practicum.shareit.item.dto;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GetSearchItemTest {

    @Test
    void of_isBlankFieldShouldBeTrueFromShouldBeZero() {
        GetSearchItem search = GetSearchItem.of("", 1L, 5, 10);
        assertTrue(search.isBlank());
        assertThat(search.getFrom(), Matchers.is(0));
    }

    @Test
    void ofOverloaded_isBlankFieldShouldBeTrueFromShouldBeZero() {
        GetSearchItem search = GetSearchItem.of("", 1L, 1L, 5, 10);
        assertTrue(search.isBlank());
        assertThat(search.getFrom(), Matchers.is(0));
    }
}