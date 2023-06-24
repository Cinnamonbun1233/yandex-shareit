package ru.practicum.shareit.booking.validation;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.status.State;

import static org.hamcrest.MatcherAssert.assertThat;

class StateConverterTest {
    private StateConverter stateConverter = new StateConverter();

    @Test
    void convertWithUnsupported() {
        State state = stateConverter.convert("Something");
        assertThat(state, Matchers.equalTo(State.UNSUPPORTED_STATUS));
    }

    @Test
    void convertWithSupportedStatus() {
        State state = stateConverter.convert("all");
        assertThat(state, Matchers.equalTo(State.ALL));
    }
}