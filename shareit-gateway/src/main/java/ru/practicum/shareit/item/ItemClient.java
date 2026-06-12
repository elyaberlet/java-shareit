package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.dto.CommentCreateDto;

@Service
public class ItemClient extends BaseClient {

    private static final String API_PREFIX = "/items";

    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder.build(), serverUrl + "/items");
    }

    public ResponseEntity<Object> create(Long userId, ItemCreateDto dto) {
        return post("", userId, dto);
    }

    public ResponseEntity<Object> update(Long userId, Long itemId, ItemUpdateDto dto) {
        return patch("/" + itemId, userId, dto);
    }

    public ResponseEntity<Object> findById(Long itemId, Long userId) {
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> findAllByOwner(Long userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> search(String text) {
        return get("/search?text=" + text);
    }

    public ResponseEntity<Object> addComment(Long userId, Long itemId, CommentCreateDto dto) {
        return post("/" + itemId + "/comment", userId, dto);
    }
}