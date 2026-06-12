package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

@Service
public class UserClient extends BaseClient {

    public UserClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder.build(), serverUrl + "/users");
    }

    public ResponseEntity<Object> create(UserCreateDto dto) {
        return post("", dto);
    }

    public ResponseEntity<Object> update(Long userId, UserUpdateDto dto) {
        return patch("/" + userId, userId, dto);
    }

    public ResponseEntity<Object> findById(Long userId) {
        return get("/" + userId, userId);
    }

    public ResponseEntity<Object> findAll() {
        return get("");
    }

    public ResponseEntity<Object> delete(Long userId) {
        return deleteRequest("/" + userId, userId);
    }
}