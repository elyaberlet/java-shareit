package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

public interface UserService {
    UserResponseDto create(UserCreateDto createDto);

    UserResponseDto update(Long userId, UserUpdateDto updateDto);

    UserResponseDto findById(Long id);

    void deleteById(Long id);
}