package ru.practicum.shareit.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.EmailAlreadyExistsException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.storage.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
public class UserIntegrTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    private User existingUser;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        existingUser = createUser("Existing User", "existing@example.com");
    }

    @Test
    void create_shouldCreateUserSuccessfully() {
        UserCreateDto dto = new UserCreateDto();
        dto.setName("New User");
        dto.setEmail("new@example.com");

        UserResponseDto result = userService.create(dto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isNotNull();
        assertThat(result.getName()).isEqualTo("New User");
        assertThat(result.getEmail()).isEqualTo("new@example.com");
        assertThat(userRepository.findById(result.getId())).isPresent();
    }

    @Test
    void create_whenEmailAlreadyExists_shouldThrowEmailAlreadyExistsException() {
        UserCreateDto dto = new UserCreateDto();
        dto.setName("Another User");
        dto.setEmail("existing@example.com");

        assertThatThrownBy(() -> userService.create(dto))
                .isInstanceOf(EmailAlreadyExistsException.class)
                .hasMessageContaining("уже существует");
    }

    @Test
    void update_shouldUpdateNameSuccessfully() {
        UserUpdateDto dto = new UserUpdateDto();
        dto.setName("Updated Name");

        UserResponseDto result = userService.update(existingUser.getId(), dto);

        assertThat(result.getName()).isEqualTo("Updated Name");
        assertThat(result.getEmail()).isEqualTo("existing@example.com");
    }

    @Test
    void update_shouldUpdateEmailSuccessfully() {
        UserUpdateDto dto = new UserUpdateDto();
        dto.setEmail("updated@example.com");

        UserResponseDto result = userService.update(existingUser.getId(), dto);

        assertThat(result.getEmail()).isEqualTo("updated@example.com");
        assertThat(result.getName()).isEqualTo("Existing User");
    }

    @Test
    void update_whenNewEmailAlreadyExists_shouldThrowEmailAlreadyExistsException() {
        User anotherUser = createUser("Another", "another@example.com");

        UserUpdateDto dto = new UserUpdateDto();
        dto.setEmail("another@example.com");

        assertThatThrownBy(() -> userService.update(existingUser.getId(), dto))
                .isInstanceOf(EmailAlreadyExistsException.class)
                .hasMessageContaining("уже существует");
    }

    @Test
    void update_whenUserNotFound_shouldThrowNotFoundException() {
        UserUpdateDto dto = new UserUpdateDto();
        dto.setName("Updated");

        assertThatThrownBy(() -> userService.update(999L, dto))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("не найден");
    }

    @Test
    void findById_shouldReturnUserSuccessfully() {
        UserResponseDto result = userService.findById(existingUser.getId());

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(existingUser.getId());
        assertThat(result.getName()).isEqualTo("Existing User");
        assertThat(result.getEmail()).isEqualTo("existing@example.com");
    }

    @Test
    void findById_whenUserNotFound_shouldThrowNotFoundException() {
        assertThatThrownBy(() -> userService.findById(999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("не найден");
    }

    @Test
    void deleteById_shouldDeleteUserSuccessfully() {
        userService.deleteById(existingUser.getId());

        assertThat(userRepository.findById(existingUser.getId())).isEmpty();
    }

    private User createUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        return userRepository.save(user);
    }
}