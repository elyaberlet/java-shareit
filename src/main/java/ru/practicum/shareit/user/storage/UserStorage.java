package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {
    User create(User user);
    Optional<User> findById(Long id);
    User update(User user);
    void deleteById(Long id);
    List<User> findAll();
    Optional<User> findByEmail(String email);
}
