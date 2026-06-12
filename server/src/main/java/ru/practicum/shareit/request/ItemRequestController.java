package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestService requestService;

    @PostMapping
    public ItemRequestResponseDto create(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestBody ItemRequestCreateDto dto
    ) {
        return requestService.create(userId, dto);
    }

    @GetMapping
    public List<ItemRequestResponseDto> findByRequester(
            @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        return requestService.findByRequester(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestResponseDto> findAllOther(
            @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        return requestService.findAllByOtherUsers(userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestResponseDto findById(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long requestId
    ) {
        return requestService.findById(requestId, userId);
    }
}