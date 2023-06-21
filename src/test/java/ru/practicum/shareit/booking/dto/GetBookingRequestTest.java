package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.status.State;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

class GetBookingRequestTest {
    @Test
    void of() {
        GetBookingRequest getBookingRequest = GetBookingRequest.of(State.ALL, 1L, true, 1, 4);
        assertThat(getBookingRequest.getFrom(), equalTo(0));
    }
}