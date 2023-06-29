package ru.practicum.shareit.request.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.request.dto.RequestItemDto;

import java.util.Map;

@Service
public class RequestItemClient extends BaseClient {
    private static final String API_PREFIX = "/requests";

    @Autowired
    public RequestItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder restTemplateBuilder) {
        super(
                restTemplateBuilder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> createNewRequest(RequestItemDto requestItemDto, Long ownerId) {
        return post("", ownerId, requestItemDto);
    }

    public ResponseEntity<Object> getRequestsByUserId(Long userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> getAllRequestsByUserId(Long userId, int from, int size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get("/all?from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> getRequestByUserId(Long userId, Long requestId) {
        return get("/" + requestId, userId);
    }
}