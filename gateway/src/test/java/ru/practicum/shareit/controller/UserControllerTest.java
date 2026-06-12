package ru.practicum.shareit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.UserClient;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserClient userClient;

    @Test
    void create_shouldReturnCreatedResponse() throws Exception {
        UserCreateDto dto = new UserCreateDto();
        dto.setName("New User");
        dto.setEmail("new@example.com");

        Map<String, Object> responseBody = Map.of(
                "id", 1L,
                "name", "New User",
                "email", "new@example.com"
        );
        ResponseEntity<Object> clientResponse = ResponseEntity.status(HttpStatus.CREATED).body(responseBody);

        when(userClient.create(any(UserCreateDto.class))).thenReturn(clientResponse);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("New User"))
                .andExpect(jsonPath("$.email").value("new@example.com"));

        verify(userClient).create(any(UserCreateDto.class));
    }

    @Test
    void update_shouldReturnOkResponse() throws Exception {
        Long userId = 1L;
        UserUpdateDto dto = new UserUpdateDto();
        dto.setName("Updated Name");

        Map<String, Object> responseBody = Map.of(
                "id", userId,
                "name", "Updated Name",
                "email", "user@example.com"
        );
        ResponseEntity<Object> clientResponse = ResponseEntity.ok(responseBody);

        when(userClient.update(eq(userId), any(UserUpdateDto.class))).thenReturn(clientResponse);

        mockMvc.perform(patch("/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.name").value("Updated Name"));

        verify(userClient).update(eq(userId), any(UserUpdateDto.class));
    }

    @Test
    void findById_shouldReturnOkResponse() throws Exception {
        Long userId = 1L;

        Map<String, Object> responseBody = Map.of(
                "id", userId,
                "name", "User",
                "email", "user@example.com"
        );
        ResponseEntity<Object> clientResponse = ResponseEntity.ok(responseBody);

        when(userClient.findById(userId)).thenReturn(clientResponse);

        mockMvc.perform(get("/users/{userId}", userId)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.name").value("User"))
                .andExpect(jsonPath("$.email").value("user@example.com"));

        verify(userClient).findById(userId);
    }

    @Test
    void findAll_shouldReturnOkResponse() throws Exception {
        ResponseEntity<Object> clientResponse = ResponseEntity.ok(
                java.util.List.of(
                        Map.of("id", 1L, "name", "User 1", "email", "user1@example.com"),
                        Map.of("id", 2L, "name", "User 2", "email", "user2@example.com")
                )
        );

        when(userClient.findAll()).thenReturn(clientResponse);

        mockMvc.perform(get("/users")
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("User 1"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("User 2"));

        verify(userClient).findAll();
    }

    @Test
    void delete_shouldReturnOkResponse() throws Exception {
        Long userId = 1L;
        ResponseEntity<Object> clientResponse = ResponseEntity.ok().build();

        when(userClient.delete(userId)).thenReturn(clientResponse);

        mockMvc.perform(delete("/users/{userId}", userId)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk());

        verify(userClient).delete(userId);
    }

    @Test
    void create_shouldPropagateClientBadRequest() throws Exception {
        UserCreateDto dto = new UserCreateDto();
        dto.setName("User");
        dto.setEmail("duplicate@example.com");

        ResponseEntity<Object> clientResponse = ResponseEntity
                .badRequest()
                .body(Map.of("error", "Email already exists"));

        when(userClient.create(any(UserCreateDto.class))).thenReturn(clientResponse);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Email already exists"));
    }

    @Test
    void findById_shouldPropagateClientNotFound() throws Exception {
        Long userId = 999L;
        ResponseEntity<Object> clientResponse = ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "User not found"));

        when(userClient.findById(userId)).thenReturn(clientResponse);

        mockMvc.perform(get("/users/{userId}", userId)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("User not found"));
    }
}
