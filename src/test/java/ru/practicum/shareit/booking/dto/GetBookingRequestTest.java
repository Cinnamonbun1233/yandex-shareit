package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.status.State;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

class GetBookingRequestTest {
    @Test
    void of() {
        PageRequest pageRequest = PageRequest.of(1, 4);
        GetBookingRequest getBookingRequest = GetBookingRequest.of(State.ALL, 1L, true);
        assertThat(getBookingRequest.getFrom(), equalTo(0));
    }
}