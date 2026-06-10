package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

@Component
public class UserMapper {
    public UserResponseDto toResponseDto(User user) {
        if (user == null) return null;

        UserResponseDto dto = new UserResponseDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        return dto;
    }

    public User toEntity(UserCreateDto createDto) {
        if (createDto == null) return null;

        User user = new User();
        user.setEmail(createDto.getEmail());
        user.setName(createDto.getName());
        return user;
    }

    public void updateEntity(User user, UserUpdateDto updateDto) {
        if (updateDto.getName() != null) {
            user.setName(updateDto.getName());
        }

        if (updateDto.getEmail() != null) {
            user.setEmail(updateDto.getEmail());
        }
    }

    public User toEntity(UserResponseDto dto) {
        User user = new User();
        user.setId(dto.getId());
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());

        return user;
    }
}
