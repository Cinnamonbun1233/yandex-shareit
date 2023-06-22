package ru.practicum.shareit.booking.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.booking.status.State;

@Getter
@Setter
@NoArgsConstructor
public class GetBookingRequest {
    private Long userId;
    private State state;
    private boolean isOwner;
    private int from;
    private int size;

    public static GetBookingRequest of(State state, Long userId, boolean isOwner) {
        GetBookingRequest request = new GetBookingRequest();
        request.setState(state);
        request.setUserId(userId);
        request.setOwner(isOwner);
//        request.setSize(size);
//        request.setFrom(from > 0 ? from / size : 0);
        return request;
    }
}