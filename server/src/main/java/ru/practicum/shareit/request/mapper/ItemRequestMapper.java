package ru.practicum.shareit.request.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ItemRequestMapper {
    public ItemRequest toEntity(ItemRequestCreateDto createDto, User requester) {
        ItemRequest request = new ItemRequest();
        request.setDescription(createDto.getDescription());
        request.setRequester(requester);
        request.setCreated(LocalDateTime.now());
        return request;
    }

    public ItemRequestResponseDto toResponseDto(ItemRequest request, List<Item> items) {
        List<ItemRequestResponseDto.ItemResponse> itemResponses = items.stream()
                .map(item -> new ItemRequestResponseDto.ItemResponse(
                        item.getId(),
                        item.getName(),
                        item.getOwner().getId()
                ))
                .collect(Collectors.toList());

        return new ItemRequestResponseDto(
                request.getId(),
                request.getDescription(),
                request.getCreated(),
                itemResponses
        );
    }
}
