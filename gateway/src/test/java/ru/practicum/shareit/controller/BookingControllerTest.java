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
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.dto.BookingCreateDto;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingClient bookingClient;

    private final Long userId = 1L;
    private final Long bookingId = 2L;

    @Test
    void create_whenValid_thenStatusIsOk() throws Exception {
        BookingCreateDto request = new BookingCreateDto();
        request.setItemId(1L);
        request.setStart(LocalDateTime.now().plusDays(1));
        request.setEnd(LocalDateTime.now().plusDays(2));

        ResponseEntity<Object> mockResponse = new ResponseEntity<>(HttpStatus.OK);
        when(bookingClient.create(anyLong(), any(BookingCreateDto.class))).thenReturn(mockResponse);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(bookingClient, times(1)).create(userId, request);
    }

    @Test
    void create_whenMissingUserHeader_thenStatusIsBadRequest() throws Exception {
        BookingCreateDto request = new BookingCreateDto();
        request.setItemId(1L);
        request.setStart(LocalDateTime.now().plusDays(1));
        request.setEnd(LocalDateTime.now().plusDays(2));

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(bookingClient);
    }

    @Test
    void create_whenInvalidUserId_thenStatusIsBadRequest() throws Exception {
        BookingCreateDto request = new BookingCreateDto();
        request.setItemId(1L);
        request.setStart(LocalDateTime.now().plusDays(1));
        request.setEnd(LocalDateTime.now().plusDays(2));

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", -1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(bookingClient);
    }

    @Test
    void updateStatus_whenValid_thenStatusIsOk() throws Exception {
        ResponseEntity<Object> mockResponse = new ResponseEntity<>(HttpStatus.OK);
        when(bookingClient.updateStatus(anyLong(), anyLong(), anyBoolean())).thenReturn(mockResponse);

        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId)
                        .param("approved", "true"))
                .andExpect(status().isOk());

        verify(bookingClient, times(1)).updateStatus(userId, bookingId, true);
    }

    @Test
    void updateStatus_whenMissingUserHeader_thenStatusIsBadRequest() throws Exception {
        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .param("approved", "true"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(bookingClient);
    }

    @Test
    void updateStatus_whenInvalidBookingId_thenStatusIsBadRequest() throws Exception {
        mockMvc.perform(patch("/bookings/{bookingId}", -1)
                        .header("X-Sharer-User-Id", userId)
                        .param("approved", "true"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(bookingClient);
    }

    @Test
    void findById_whenValid_thenStatusIsOk() throws Exception {
        ResponseEntity<Object> mockResponse = new ResponseEntity<>(HttpStatus.OK);
        when(bookingClient.findById(anyLong(), anyLong())).thenReturn(mockResponse);

        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());

        verify(bookingClient, times(1)).findById(userId, bookingId);
    }

    @Test
    void findById_whenMissingUserHeader_thenStatusIsBadRequest() throws Exception {
        mockMvc.perform(get("/bookings/{bookingId}", bookingId))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(bookingClient);
    }

    @Test
    void findAllByUser_withDefaultParams_thenStatusIsOk() throws Exception {
        ResponseEntity<Object> mockResponse = new ResponseEntity<>(HttpStatus.OK);
        when(bookingClient.findAllByUser(anyLong(), anyString(), anyInt(), anyInt())).thenReturn(mockResponse);

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());

        verify(bookingClient, times(1)).findAllByUser(userId, "ALL", 0, 20);
    }

    @Test
    void findAllByUser_withCustomParams_thenStatusIsOk() throws Exception {
        ResponseEntity<Object> mockResponse = new ResponseEntity<>(HttpStatus.OK);
        when(bookingClient.findAllByUser(anyLong(), anyString(), anyInt(), anyInt())).thenReturn(mockResponse);

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", "FUTURE")
                        .param("from", "10")
                        .param("size", "5"))
                .andExpect(status().isOk());

        verify(bookingClient, times(1)).findAllByUser(userId, "FUTURE", 10, 5);
    }

    @Test
    void findAllByUser_whenMissingUserHeader_thenStatusIsBadRequest() throws Exception {
        mockMvc.perform(get("/bookings"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(bookingClient);
    }

    @Test
    void findAllByUser_whenInvalidFrom_thenStatusIsBadRequest() throws Exception {
        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", "-1"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(bookingClient);
    }

    @Test
    void findAllByUser_whenInvalidSize_thenStatusIsBadRequest() throws Exception {
        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .param("size", "0"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(bookingClient);
    }

    @Test
    void findAllByOwner_withDefaultParams_thenStatusIsOk() throws Exception {
        ResponseEntity<Object> mockResponse = new ResponseEntity<>(HttpStatus.OK);
        when(bookingClient.findAllByOwner(anyLong(), anyString(), anyInt(), anyInt())).thenReturn(mockResponse);

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());

        verify(bookingClient, times(1)).findAllByOwner(userId, "ALL", 0, 20);
    }

    @Test
    void findAllByOwner_withCustomParams_thenStatusIsOk() throws Exception {
        ResponseEntity<Object> mockResponse = new ResponseEntity<>(HttpStatus.OK);
        when(bookingClient.findAllByOwner(anyLong(), anyString(), anyInt(), anyInt())).thenReturn(mockResponse);

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", "PAST")
                        .param("from", "5")
                        .param("size", "10"))
                .andExpect(status().isOk());

        verify(bookingClient, times(1)).findAllByOwner(userId, "PAST", 5, 10);
    }

    @Test
    void findAllByOwner_whenMissingUserHeader_thenStatusIsBadRequest() throws Exception {
        mockMvc.perform(get("/bookings/owner"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(bookingClient);
    }

    @Test
    void findAllByOwner_whenInvalidSize_thenStatusIsBadRequest() throws Exception {
        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userId)
                        .param("size", "-5"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(bookingClient);
    }
}