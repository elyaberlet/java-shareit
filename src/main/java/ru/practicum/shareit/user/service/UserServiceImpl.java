package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EmailAlreadyExistsException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.storage.UserStorage;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;
    private final UserMapper userMapper;

    @Override
    public UserResponseDto create(UserCreateDto createDto) {
        User user = userMapper.toEntity(createDto);

        if (userStorage.findByEmail(user.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("Пользователь с email " + user.getEmail() + " уже существует");
        }
        User savedUser = userStorage.create(user);
        return userMapper.toResponseDto(savedUser);
    }

    @Override
    public UserResponseDto update(Long userId, UserUpdateDto updateDto) {
        User existing = findByIdEntity(userId);

        if (updateDto.getName() != null) {
            existing.setName(updateDto.getName());
        }

        if (updateDto.getEmail() != null && !existing.getEmail().equals(updateDto.getEmail())) {
            if (userStorage.findByEmail(updateDto.getEmail()).isPresent()) {
                throw new EmailAlreadyExistsException("Пользователь с email " + updateDto.getEmail() + " уже существует");
            }
            existing.setEmail(updateDto.getEmail());
        }

        User updatedUser = userStorage.update(existing);
        return userMapper.toResponseDto(updatedUser);
    }

    @Override
    public UserResponseDto findById(Long id) {
        User user = findByIdEntity(id);
        return userMapper.toResponseDto(user);
    }

    @Override
    public void deleteById(Long id) {
        userStorage.deleteById(id);
    }

    private User findByIdEntity(Long id) {
        return userStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id =" + id + " не найден"));
    }
}