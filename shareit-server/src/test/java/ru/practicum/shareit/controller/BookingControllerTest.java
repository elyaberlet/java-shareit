package ru.practicum.shareit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.NotFoundException;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {

    @MockBean
    private BookingService bookingService;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @Test
    void createBooking_whenRequestIsValid_shouldReturn200AndBooking() throws Exception {
        Long userId = 1L;
        BookingCreateDto request = new BookingCreateDto();
        request.setItemId(1L);
        request.setStart(LocalDateTime.now().plusDays(1));
        request.setEnd(LocalDateTime.now().plusDays(2));

        BookingResponseDto response = new BookingResponseDto();
        response.setId(1L);
        response.setStatus(BookingStatus.WAITING);

        when(bookingService.createBooking(eq(userId), any(BookingCreateDto.class)))
                .thenReturn(response);

        mvc.perform(post("/bookings")
                        .header(USER_ID_HEADER, userId)
                        .content(mapper.writeValueAsString(request))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value(BookingStatus.WAITING.toString()));
    }

    @Test
    void createBooking_whenNotFound_shouldReturn404() throws Exception {
        Long userId = 1L;
        BookingCreateDto request = new BookingCreateDto();
        request.setItemId(999L);
        request.setStart(LocalDateTime.now().plusDays(1));
        request.setEnd(LocalDateTime.now().plusDays(2));

        String errorMessage = "Вещь с id=999 не найдена";

        when(bookingService.createBooking(eq(userId), any(BookingCreateDto.class)))
                .thenThrow(new NotFoundException(errorMessage));

        mvc.perform(post("/bookings")
                        .header(USER_ID_HEADER, userId)
                        .content(mapper.writeValueAsString(request))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(errorMessage));
    }

    @Test
    void approveBooking_whenRequestIsValid_shouldReturn200AndUpdatedBooking() throws Exception {
        Long ownerId = 1L;
        Long bookingId = 10L;
        boolean approved = true;

        BookingResponseDto response = new BookingResponseDto();
        response.setId(bookingId);
        response.setStatus(BookingStatus.APPROVED);

        when(bookingService.approveBooking(eq(ownerId), eq(bookingId), eq(approved)))
                .thenReturn(response);

        mvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header(USER_ID_HEADER, ownerId)
                        .param("approved", String.valueOf(approved))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingId))
                .andExpect(jsonPath("$.status").value(BookingStatus.APPROVED.toString()));
    }

    @Test
    void getBookingById_whenBookingExists_shouldReturn200AndBooking() throws Exception {
        Long userId = 1L;
        Long bookingId = 1L;

        BookingResponseDto response = new BookingResponseDto();
        response.setId(bookingId);
        response.setStatus(BookingStatus.WAITING);

        when(bookingService.getBookingById(eq(userId), eq(bookingId))).thenReturn(response);

        mvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header(USER_ID_HEADER, userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingId))
                .andExpect(jsonPath("$.status").value(BookingStatus.WAITING.toString()));
    }

    @Test
    void getUserBookings_whenStateIsNotProvided_shouldUseDefaultAllAndReturnCollection() throws Exception {
        Long userId = 1L;

        BookingResponseDto bookingResponse = new BookingResponseDto();
        bookingResponse.setId(10L);
        bookingResponse.setStatus(BookingStatus.WAITING);

        when(bookingService.getUserBookings(eq(userId), eq("ALL"), eq(0), eq(20)))
                .thenReturn(List.of(bookingResponse));

        mvc.perform(get("/bookings")
                        .header(USER_ID_HEADER, userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].id").value(10L))
                .andExpect(jsonPath("$[0].status").value(BookingStatus.WAITING.toString()));
    }

    @Test
    void getUserBookings_whenStateIsProvided_shouldUseItAndReturnCollection() throws Exception {
        Long userId = 1L;

        BookingResponseDto bookingResponse = new BookingResponseDto();
        bookingResponse.setId(10L);
        bookingResponse.setStatus(BookingStatus.WAITING);

        when(bookingService.getUserBookings(eq(userId), eq("WAITING"), eq(0), eq(20)))
                .thenReturn(List.of(bookingResponse));

        mvc.perform(get("/bookings")
                        .header(USER_ID_HEADER, userId)
                        .param("state", "WAITING")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].id").value(10L));
    }

    @Test
    void getOwnerBookings_whenStateIsNotProvided_shouldUseDefaultAllAndReturnCollection() throws Exception {
        Long userId = 1L;

        BookingResponseDto bookingResponse = new BookingResponseDto();
        bookingResponse.setId(15L);
        bookingResponse.setStatus(BookingStatus.WAITING);

        when(bookingService.getOwnerBookings(eq(userId), eq("ALL"), eq(0), eq(20)))
                .thenReturn(List.of(bookingResponse));

        mvc.perform(get("/bookings/owner")
                        .header(USER_ID_HEADER, userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].id").value(15L))
                .andExpect(jsonPath("$[0].status").value(BookingStatus.WAITING.toString()));
    }

    @Test
    void getOwnerBookings_whenStateIsProvided_shouldUseItAndReturnCollection() throws Exception {
        Long userId = 1L;

        BookingResponseDto bookingResponse = new BookingResponseDto();
        bookingResponse.setId(15L);
        bookingResponse.setStatus(BookingStatus.REJECTED);

        when(bookingService.getOwnerBookings(eq(userId), eq("REJECTED"), eq(0), eq(20)))
                .thenReturn(List.of(bookingResponse));

        mvc.perform(get("/bookings/owner")
                        .header(USER_ID_HEADER, userId)
                        .param("state", "REJECTED")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].id").value(15L))
                .andExpect(jsonPath("$[0].status").value(BookingStatus.REJECTED.toString()));
    }
}
