package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImp implements ItemService {
    private final ItemStorage itemStorage;
    private final UserService userService;
    private final ItemMapper itemMapper;

    @Override
    public ItemResponseDto create(Long ownerId, ItemCreateDto createDto) {
        userService.findById(ownerId);

        Item item = itemMapper.toEntity(createDto, ownerId);
        Item savedItem = itemStorage.save(item);

        return itemMapper.toResponseDto(savedItem);
    }

    @Override
    public ItemResponseDto update(Long ownerId, Long itemId, ItemUpdateDto updateDto) {
        Item existing = findByIdEntity(itemId);

        if (!existing.getOwnerId().equals(ownerId)) {
            throw new NotFoundException("Только владелец может редактировать вещь");
        }

        if (updateDto.getName() != null) {
            existing.setName(updateDto.getName());
        }

        if (updateDto.getDescription() != null) {
            existing.setDescription(updateDto.getDescription());
        }

        if (updateDto.getAvailable() != null) {
            existing.setAvailable(updateDto.getAvailable());
        }

        Item updatedItem = itemStorage.update(existing);
        return itemMapper.toResponseDto(updatedItem);
    }

    @Override
    public ItemResponseDto findById(Long id) {
        Item item = findByIdEntity(id);
        return itemMapper.toResponseDto(item);
    }

    @Override
    public List<ItemResponseDto> findAllByOwnerId(Long ownerId) {
        userService.findById(ownerId);

        return itemStorage.findAllByOwnerId(ownerId).stream()
                .map(itemMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemResponseDto> search(String text) {
        String lowerText = text.toLowerCase();

        return itemStorage.findAll().stream()
                .filter(Item::getAvailable)
                .filter(item -> (item.getName() != null && item.getName().toLowerCase().contains(lowerText)) ||
                        (item.getDescription() != null && item.getDescription().toLowerCase().contains(lowerText))
                )
                .map(itemMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    private Item findByIdEntity(Long id) {
        return itemStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Вещь с id =" + id + " не найдена"));
    }
}