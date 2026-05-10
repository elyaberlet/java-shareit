package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImp implements ItemService {
    private final ItemStorage itemStorage;

    @Override
    public Item save(Item item) {
        return itemStorage.save(item);
    }

    @Override
    public Item update(Item item) {
        findById(item.getId());
        return itemStorage.update(item);
    }

    @Override
    public Item findById(Long id) {
        return itemStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Вещь с id =" + id + " не найдена"));
    }

    @Override
    public List<Item> findAllByOwnerId(Long ownerId) {
        return itemStorage.findAllByOwnerId(ownerId);
    }

    @Override
    public List<Item> search(String text) {
        String lowerText = text.toLowerCase();

        return itemStorage.findAll().stream()
                .filter(Item::getAvailable)
                .filter(item -> (item.getName() != null && item.getName().toLowerCase().contains(lowerText)) ||
                        (item.getDescription() != null && item.getDescription().toLowerCase().contains(lowerText))
                )
                .collect(Collectors.toList());
    }
}
