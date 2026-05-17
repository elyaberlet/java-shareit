package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemResponseDto create(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                  @Valid @RequestBody ItemCreateDto createDto) {
        return itemService.create(ownerId, createDto);
    }

    @PatchMapping("/{itemId}")
    public ItemResponseDto update(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                  @PathVariable Long itemId,
                                  @Valid @RequestBody ItemUpdateDto updateDto) {
        return itemService.update(ownerId, itemId, updateDto);
    }

    @GetMapping("/{itemId}")
    public ItemResponseDto getById(@PathVariable Long itemId) {
        return itemService.findById(itemId);
    }

    @GetMapping
    public List<ItemResponseDto> getAllByOwner(@RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return itemService.findAllByOwnerId(ownerId);
    }

    @GetMapping("/search")
    public List<ItemResponseDto> search(@RequestParam(required = false) String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        return itemService.search(text);
    }
}