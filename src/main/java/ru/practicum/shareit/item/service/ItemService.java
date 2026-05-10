package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item save(Item item);

    Item update(Item item);

    Item findById(Long id);

    List<Item> findAllByOwnerId(Long ownerId);

    List<Item> search(String text);
}