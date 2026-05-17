package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import java.util.List;

public interface ItemService {
    ItemResponseDto create(Long ownerId, ItemCreateDto createDto);

    ItemResponseDto update(Long ownerId, Long itemId, ItemUpdateDto updateDto);

    ItemResponseDto findById(Long id);

    List<ItemResponseDto> findAllByOwnerId(Long ownerId);

    List<ItemResponseDto> search(String text);
}