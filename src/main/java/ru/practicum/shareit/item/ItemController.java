package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
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
    public ItemResponseDto getById(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long itemId) {
        return itemService.findById(itemId, userId);
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

    @PostMapping("/{itemId}/comment")
    public CommentResponseDto addComment(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long itemId,
            @Valid @RequestBody CommentCreateDto commentDto) {
        return itemService.addComment(userId, itemId, commentDto);
    }
}