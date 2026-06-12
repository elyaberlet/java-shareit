package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestResponseDto create(Long userId, ItemRequestCreateDto createDto);

    List<ItemRequestResponseDto> findByRequester(Long userId);

    List<ItemRequestResponseDto> findAllByOtherUsers(Long userId);

    ItemRequestResponseDto findById(Long requestId, Long userId);
}
