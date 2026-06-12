package ru.practicum.shareit.user.service;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EmailAlreadyExistsException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.storage.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userStorage;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserResponseDto create(UserCreateDto createDto) {
        User user = userMapper.toEntity(createDto);

        if (userStorage.findByEmail(user.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("Пользователь с email " + user.getEmail() + " уже существует");
        }
        User savedUser = userStorage.save(user);
        return userMapper.toResponseDto(savedUser);
    }

    @Override
    @Transactional
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

        User updatedUser = userStorage.save(existing);
        return userMapper.toResponseDto(updatedUser);
    }

    @Override
    public UserResponseDto findById(Long id) {
        User user = findByIdEntity(id);
        return userMapper.toResponseDto(user);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        userStorage.deleteById(id);
    }

    private User findByIdEntity(Long id) {
        return userStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id =" + id + " не найден"));
    }
}