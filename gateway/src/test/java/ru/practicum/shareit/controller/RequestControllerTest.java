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
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.RequestClient;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class RequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RequestClient requestClient;

    private final Long userId = 1L;
    private final Long requestId = 2L;

    @Test
    void create_shouldCallClientWithCorrectParams() throws Exception {
        ItemRequestCreateDto dto = new ItemRequestCreateDto();
        dto.setDescription("Need a drill");

        when(requestClient.create(eq(userId), any(ItemRequestCreateDto.class)))
                .thenReturn(ResponseEntity.status(HttpStatus.CREATED).body("{\"id\": 1}"));

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());

        verify(requestClient).create(eq(userId), any(ItemRequestCreateDto.class));
    }

    @Test
    void findByRequester_shouldCallClientWithCorrectParams() throws Exception {
        when(requestClient.findByRequester(userId))
                .thenReturn(ResponseEntity.ok("[]"));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());

        verify(requestClient).findByRequester(userId);
    }

    @Test
    void findAllOther_shouldCallClientWithCorrectParams() throws Exception {
        when(requestClient.findAllOther(userId))
                .thenReturn(ResponseEntity.ok("[]"));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());

        verify(requestClient).findAllOther(userId);
    }

    @Test
    void findById_shouldCallClientWithCorrectParams() throws Exception {
        when(requestClient.findById(userId, requestId))
                .thenReturn(ResponseEntity.ok("{\"id\": 2}"));

        mockMvc.perform(get("/requests/{requestId}", requestId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());

        verify(requestClient).findById(userId, requestId);
    }
}