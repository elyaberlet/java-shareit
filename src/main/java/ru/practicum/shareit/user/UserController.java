package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.service.UserService;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @PostMapping
    public UserResponseDto create(@Valid @RequestBody UserCreateDto createDto) {
       User user = userMapper.toEntity(createDto);
       User savedUser = userService.create(user);
       return userMapper.toResponseDto(savedUser);
    }

    @PatchMapping("{userId}")
    public UserResponseDto update(@PathVariable Long userId,
                                  @Valid @RequestBody UserUpdateDto updateDto) {
        User userForUpdate = new User();
        userForUpdate.setId(userId);
        userForUpdate.setName(updateDto.getName());
        userForUpdate.setEmail(updateDto.getEmail());
        User updatedUser = userService.update(userForUpdate);
        return userMapper.toResponseDto(updatedUser);
    }

    @GetMapping("{userId}")
    public UserResponseDto findById(@PathVariable Long userId) {
        User user = userService.findById(userId);
        return userMapper.toResponseDto(user);
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable Long userId) {
        userService.deleteById(userId);
    }
}
