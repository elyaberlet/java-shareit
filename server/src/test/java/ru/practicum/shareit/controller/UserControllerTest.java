package ru.practicum.shareit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {

    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @Test
    void create_whenRequestIsValid_shouldReturn200AndUser() throws Exception {
        UserCreateDto request = new UserCreateDto();
        request.setName("New User");
        request.setEmail("new@example.com");

        UserResponseDto response = new UserResponseDto();
        response.setId(1L);
        response.setName("New User");
        response.setEmail("new@example.com");

        when(userService.create(any(UserCreateDto.class)))
                .thenReturn(response);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(request))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("New User"))
                .andExpect(jsonPath("$.email").value("new@example.com"));
    }

    @Test
    void update_whenRequestIsValid_shouldReturn200AndUpdatedUser() throws Exception {
        Long userId = 1L;
        UserUpdateDto request = new UserUpdateDto();
        request.setName("Updated Name");
        request.setEmail("updated@example.com");

        UserResponseDto response = new UserResponseDto();
        response.setId(userId);
        response.setName("Updated Name");
        response.setEmail("updated@example.com");

        when(userService.update(eq(userId), any(UserUpdateDto.class)))
                .thenReturn(response);

        mvc.perform(patch("/users/{userId}", userId)
                        .content(mapper.writeValueAsString(request))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.name").value("Updated Name"))
                .andExpect(jsonPath("$.email").value("updated@example.com"));
    }

    @Test
    void findById_whenUserExists_shouldReturn200AndUser() throws Exception {
        Long userId = 1L;

        UserResponseDto response = new UserResponseDto();
        response.setId(userId);
        response.setName("User");
        response.setEmail("user@example.com");

        when(userService.findById(eq(userId)))
                .thenReturn(response);

        mvc.perform(get("/users/{userId}", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.name").value("User"))
                .andExpect(jsonPath("$.email").value("user@example.com"));
    }

    @Test
    void delete_whenUserExists_shouldReturn200() throws Exception {
        Long userId = 1L;

        mvc.perform(delete("/users/{userId}", userId)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk());

        verify(userService).deleteById(userId);
    }
}