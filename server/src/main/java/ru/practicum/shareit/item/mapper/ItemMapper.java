package ru.practicum.shareit.item.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;

@Component
public class ItemMapper {
    public void updateEntity(Item existing, ItemUpdateDto updateDto) {
        if (updateDto.getName() != null) {
            existing.setName(updateDto.getName());
        }
        if (updateDto.getDescription() != null) {
            existing.setDescription(updateDto.getDescription());
        }
        if (updateDto.getAvailable() != null) {
            existing.setAvailable(updateDto.getAvailable());
        }
    }

    public ItemResponseDto toResponseDto(Item item) {
        if (item == null) return null;

        ItemResponseDto dto = new ItemResponseDto();
        dto.setAvailable(item.getAvailable());
        dto.setDescription(item.getDescription());
        dto.setName(item.getName());
        dto.setId(item.getId());
        if (item.getRequest() != null) {
            dto.setRequestId(item.getRequest().getId());
        }
        return dto;
    }

    public Item toEntity(ItemCreateDto createDto, Long ownerId, ItemRequest request) {
        if (createDto == null) return null;

        Item item = new Item();
        item.setAvailable(createDto.getAvailable());
        item.setDescription(createDto.getDescription());
        item.setName(createDto.getName());
        item.setRequest(request);
        return item;
    }
}
