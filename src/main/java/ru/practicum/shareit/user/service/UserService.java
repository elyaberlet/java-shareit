package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.User;

public interface UserService {
    User create(User user);

    User update(User user);

    User findById(Long id);

    void deleteById(Long id);
}
