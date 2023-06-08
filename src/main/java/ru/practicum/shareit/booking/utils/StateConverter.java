package ru.practicum.shareit.booking.utils;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.State;

@Component
public class StateConverter implements Converter<String, State> {
    @Override
    public State convert(String source) {
        try {
            return State.valueOf(source.toUpperCase());
        } catch (IllegalArgumentException ex) {
            return State.UNSUPPORTED_STATUS;
        }
    }
}
