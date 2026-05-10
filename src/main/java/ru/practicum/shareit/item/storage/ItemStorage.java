package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemStorage {
    Item save(Item item);
    Item update(Item item);
    Optional<Item> findById(Long id);
    List<Item> findAll();
    List<Item> findAllByOwnerId(Long ownerId);
    void deleteById(Long id);
}
