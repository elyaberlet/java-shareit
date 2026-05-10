package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EmailAlreadyExistsException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.UserStorage;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    @Override
    public User create(User user) {

        if (userStorage.findByEmail(user.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("Пользователь с email " + user.getEmail() + " уже существует");
        }
        return userStorage.create(user);
    }

    @Override
    public User update(User user) {
        User existing = findById(user.getId());

        if (user.getName() != null) {
            existing.setName(user.getName());
        }

        if (user.getEmail() != null && !existing.getEmail().equals(user.getEmail())) {
            if (userStorage.findByEmail(user.getEmail()).isPresent()) {
                throw new EmailAlreadyExistsException("Пользователь с email " + user.getEmail() + " уже существует");
            }
            existing.setEmail(user.getEmail());
        }

        return userStorage.update(existing);
    }

    @Override
    public User findById(Long id) {
        return userStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id =" + id + " не найден"));
    }

    @Override
    public void deleteById(Long id) {
        userStorage.deleteById(id);
    }
}