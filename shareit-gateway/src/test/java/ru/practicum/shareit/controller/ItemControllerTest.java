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
import ru.practicum.shareit.booking.BookingClient;
import ru.practicum.shareit.item.ItemClient;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import ru.practicum.shareit.request.RequestClient;
import ru.practicum.shareit.user.UserClient;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemClient itemClient;

    @MockBean
    private BookingClient bookingClient;

    @MockBean
    private UserClient userClient;

    @MockBean
    private RequestClient requestClient;

    private final Long userId = 1L;
    private final Long itemId = 2L;

    @Test
    void create_whenValid_thenStatusIsOk() throws Exception {
        ItemCreateDto request = new ItemCreateDto();
        request.setName("Drill");
        request.setDescription("Power drill");
        request.setAvailable(true);

        ResponseEntity<Object> mockResponse = new ResponseEntity<>(HttpStatus.OK);
        when(itemClient.create(anyLong(), any(ItemCreateDto.class))).thenReturn(mockResponse);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(itemClient, times(1)).create(userId, request);
    }

    @Test
    void create_whenMissingUserHeader_thenStatusIsBadRequest() throws Exception {
        ItemCreateDto request = new ItemCreateDto();
        request.setName("Drill");

        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(itemClient);
    }

    @Test
    void create_whenInvalidUserId_thenStatusIsBadRequest() throws Exception {
        ItemCreateDto request = new ItemCreateDto();
        request.setName("Drill");

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", -1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(itemClient);
    }

    @Test
    void update_whenValid_thenStatusIsOk() throws Exception {
        ItemUpdateDto request = new ItemUpdateDto();
        request.setName("Updated Drill");

        ResponseEntity<Object> mockResponse = new ResponseEntity<>(HttpStatus.OK);
        when(itemClient.update(anyLong(), anyLong(), any(ItemUpdateDto.class))).thenReturn(mockResponse);

        mockMvc.perform(patch("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(itemClient, times(1)).update(userId, itemId, request);
    }

    @Test
    void update_whenMissingUserHeader_thenStatusIsBadRequest() throws Exception {
        ItemUpdateDto request = new ItemUpdateDto();
        request.setName("Updated Drill");

        mockMvc.perform(patch("/items/{itemId}", itemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(itemClient);
    }

    @Test
    void update_whenInvalidItemId_thenStatusIsBadRequest() throws Exception {
        ItemUpdateDto request = new ItemUpdateDto();
        request.setName("Updated Drill");

        mockMvc.perform(patch("/items/{itemId}", -1)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(itemClient);
    }

    @Test
    void findById_whenValid_thenStatusIsOk() throws Exception {
        ResponseEntity<Object> mockResponse = new ResponseEntity<>(HttpStatus.OK);
        when(itemClient.findById(anyLong(), anyLong())).thenReturn(mockResponse);

        mockMvc.perform(get("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());

        verify(itemClient, times(1)).findById(itemId, userId);
    }

    @Test
    void findById_whenMissingUserHeader_thenStatusIsBadRequest() throws Exception {
        mockMvc.perform(get("/items/{itemId}", itemId))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(itemClient);
    }

    @Test
    void findById_whenInvalidItemId_thenStatusIsBadRequest() throws Exception {
        mockMvc.perform(get("/items/{itemId}", -1)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(itemClient);
    }

    @Test
    void findAllByOwner_whenValid_thenStatusIsOk() throws Exception {
        ResponseEntity<Object> mockResponse = new ResponseEntity<>(HttpStatus.OK);
        when(itemClient.findAllByOwner(anyLong())).thenReturn(mockResponse);

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());

        verify(itemClient, times(1)).findAllByOwner(userId);
    }

    @Test
    void findAllByOwner_whenMissingUserHeader_thenStatusIsBadRequest() throws Exception {
        mockMvc.perform(get("/items"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(itemClient);
    }

    @Test
    void search_whenValid_thenStatusIsOk() throws Exception {
        ResponseEntity<Object> mockResponse = new ResponseEntity<>(HttpStatus.OK);
        when(itemClient.search(anyString())).thenReturn(mockResponse);

        mockMvc.perform(get("/items/search")
                        .param("text", "drill"))
                .andExpect(status().isOk());

        verify(itemClient, times(1)).search("drill");
    }

    @Test
    void addComment_whenValid_thenStatusIsOk() throws Exception {
        CommentCreateDto request = new CommentCreateDto();
        request.setText("Great item!");

        ResponseEntity<Object> mockResponse = new ResponseEntity<>(HttpStatus.OK);
        when(itemClient.addComment(anyLong(), anyLong(), any(CommentCreateDto.class))).thenReturn(mockResponse);

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(itemClient, times(1)).addComment(userId, itemId, request);
    }

    @Test
    void addComment_whenMissingUserHeader_thenStatusIsBadRequest() throws Exception {
        CommentCreateDto request = new CommentCreateDto();
        request.setText("Great item!");

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(itemClient);
    }

    @Test
    void addComment_whenInvalidItemId_thenStatusIsBadRequest() throws Exception {
        CommentCreateDto request = new CommentCreateDto();
        request.setText("Great item!");

        mockMvc.perform(post("/items/{itemId}/comment", -1)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(itemClient);
    }
}