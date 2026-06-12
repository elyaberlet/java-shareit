package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {

    private final RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> create(
            @RequestHeader("X-Sharer-User-Id") @Positive Long userId,
            @Valid @RequestBody ItemRequestCreateDto dto
    ) {
        return requestClient.create(userId, dto);
    }

    @GetMapping
    public ResponseEntity<Object> findByRequester(
            @RequestHeader("X-Sharer-User-Id") @Positive Long userId
    ) {
        return requestClient.findByRequester(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> findAllOther(
            @RequestHeader("X-Sharer-User-Id") @Positive Long userId
    ) {
        return requestClient.findAllOther(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> findById(
            @RequestHeader("X-Sharer-User-Id") @Positive Long userId,
            @PathVariable @Positive Long requestId
    ) {
        return requestClient.findById(userId, requestId);
    }
}