package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.client.BaseClient;

@Service
public class BookingClient extends BaseClient {

    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder.build(), serverUrl + "/bookings");
    }

    public ResponseEntity<Object> create(Long userId, BookingCreateDto dto) {
        return post("", userId, dto);
    }

    public ResponseEntity<Object> updateStatus(Long userId, Long bookingId, Boolean approved) {
        // Исправлено: передаём null как body
        return patch("/" + bookingId + "?approved=" + approved, userId, null);
    }

    public ResponseEntity<Object> findById(Long userId, Long bookingId) {
        return get("/" + bookingId, userId);
    }

    public ResponseEntity<Object> findAllByUser(Long userId, String state, Integer from, Integer size) {
        String path = "?state=" + state + "&from=" + from + "&size=" + size;
        return get(path, userId);
    }

    public ResponseEntity<Object> findAllByOwner(Long userId, String state, Integer from, Integer size) {
        String path = "/owner?state=" + state + "&from=" + from + "&size=" + size;
        return get(path, userId);
    }
}