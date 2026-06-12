package ru.practicum.shareit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
public class RequestControllerTest {

    @MockBean
    private ItemRequestService requestService;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @Test
    void create_shouldReturn200AndRequest() throws Exception {
        Long userId = 1L;
        ItemRequestCreateDto dto = new ItemRequestCreateDto();
        dto.setDescription("Need a drill");

        ItemRequestResponseDto response = new ItemRequestResponseDto();
        response.setId(1L);
        response.setDescription("Need a drill");

        when(requestService.create(eq(userId), any(ItemRequestCreateDto.class)))
                .thenReturn(response);

        mvc.perform(post("/requests")
                        .header(USER_ID_HEADER, userId)
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value("Need a drill"));
    }

    @Test
    void findByRequester_shouldReturn200AndList() throws Exception {
        Long userId = 1L;
        ItemRequestResponseDto response = new ItemRequestResponseDto();
        response.setId(1L);
        response.setDescription("Request 1");

        when(requestService.findByRequester(eq(userId)))
                .thenReturn(List.of(response));

        mvc.perform(get("/requests")
                        .header(USER_ID_HEADER, userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].description").value("Request 1"));
    }

    @Test
    void findAllOther_shouldReturn200AndList() throws Exception {
        Long userId = 1L;
        ItemRequestResponseDto response = new ItemRequestResponseDto();
        response.setId(2L);
        response.setDescription("Other Request");

        when(requestService.findAllByOtherUsers(eq(userId)))
                .thenReturn(List.of(response));

        mvc.perform(get("/requests/all")
                        .header(USER_ID_HEADER, userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].id").value(2L))
                .andExpect(jsonPath("$[0].description").value("Other Request"));
    }

    @Test
    void findById_shouldReturn200AndRequest() throws Exception {
        Long userId = 1L;
        Long requestId = 1L;

        ItemRequestResponseDto response = new ItemRequestResponseDto();
        response.setId(requestId);
        response.setDescription("Specific Request");

        when(requestService.findById(eq(requestId), eq(userId)))
                .thenReturn(response);

        mvc.perform(get("/requests/{requestId}", requestId)
                        .header(USER_ID_HEADER, userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(requestId))
                .andExpect(jsonPath("$.description").value("Specific Request"));
    }
}