package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;
    private final ItemMapper itemMapper;
    private final UserService userService;

    public ItemController(ItemService itemService, ItemMapper itemMapper, UserService userService) {
        this.itemService = itemService;
        this.itemMapper = itemMapper;
        this.userService = userService;
    }

    @PostMapping
    public ItemResponseDto create(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                  @Valid @RequestBody ItemCreateDto createDto) {
        userService.findById(ownerId);
        Item item = itemMapper.toEntity(createDto, ownerId);
        Item savedItem = itemService.save(item);
        return itemMapper.toResponseDto(savedItem);
    }

    @PatchMapping("/{itemId}")
    public ItemResponseDto update(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                  @PathVariable Long itemId,
                                  @Valid @RequestBody ItemUpdateDto updateDto) {
        Item existing = itemService.findById(itemId);
        if (!existing.getOwnerId().equals(ownerId)) {
            throw new NotFoundException("Только владелец может редактировать вещь");
        }

        itemMapper.updateEntity(existing, updateDto);
        Item updatedItem = itemService.update(existing);
        return itemMapper.toResponseDto(updatedItem);
    }

    @GetMapping("/{itemId}")
    public ItemResponseDto getById(@PathVariable Long itemId) {
        Item item = itemService.findById(itemId);
        return itemMapper.toResponseDto(item);
    }

    @GetMapping
    public List<ItemResponseDto> getAllByOwner(@RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return itemService.findAllByOwnerId(ownerId).stream()
                .map(itemMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/search")
    public List<ItemResponseDto> search(@RequestParam(required = false) String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        return itemService.search(text).stream()
                .map(itemMapper::toResponseDto)
                .collect(Collectors.toList());
    }
}