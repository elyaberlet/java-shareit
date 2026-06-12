package ru.practicum.shareit.dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class BookingDtoTest {

    private ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    @DisplayName("BookingCreateDto должен корректно сериализоваться в JSON")
    void testBookingCreateDtoSerialization() throws Exception {
        BookingCreateDto dto = new BookingCreateDto();
        dto.setItemId(5L);
        dto.setStart(LocalDateTime.of(2026, 7, 1, 10, 0, 0));
        dto.setEnd(LocalDateTime.of(2026, 7, 10, 18, 0, 0));

        JsonNode jsonNode = mapper.valueToTree(dto);

        assertThat(jsonNode.get("itemId").asLong()).isEqualTo(5L);
        assertThat(jsonNode.get("start").asText()).isEqualTo("2026-07-01T10:00:00");
        assertThat(jsonNode.get("end").asText()).isEqualTo("2026-07-10T18:00:00");
    }

    @Test
    @DisplayName("BookingCreateDto должен корректно десериализоваться из JSON")
    void testBookingCreateDtoDeserialization() throws Exception {
        String json = "{\"itemId\": 10, \"start\": \"2026-08-01T10:00:00\", \"end\": \"2026-08-10T18:00:00\"}";

        BookingCreateDto dto = mapper.readValue(json, BookingCreateDto.class);

        assertThat(dto.getItemId()).isEqualTo(10L);
        assertThat(dto.getStart()).isEqualTo(LocalDateTime.of(2026, 8, 1, 10, 0, 0));
        assertThat(dto.getEnd()).isEqualTo(LocalDateTime.of(2026, 8, 10, 18, 0, 0));
    }

    @Test
    @DisplayName("BookingResponseDto должен корректно сериализоваться в JSON")
    void testBookingResponseDtoSerialization() throws Exception {
        BookingResponseDto dto = new BookingResponseDto();
        dto.setId(100L);
        dto.setStart(LocalDateTime.of(2026, 7, 1, 10, 0, 0));
        dto.setEnd(LocalDateTime.of(2026, 7, 10, 18, 0, 0));
        dto.setStatus(BookingStatus.APPROVED);

        BookingResponseDto.ItemDto item = new BookingResponseDto.ItemDto();
        item.setId(5L);
        item.setName("Дрель");
        dto.setItem(item);

        BookingResponseDto.BookerDto booker = new BookingResponseDto.BookerDto();
        booker.setId(2L);
        dto.setBooker(booker);

        JsonNode jsonNode = mapper.valueToTree(dto);

        assertThat(jsonNode.get("id").asLong()).isEqualTo(100L);
        assertThat(jsonNode.get("status").asText()).isEqualTo("APPROVED");
        assertThat(jsonNode.get("item").get("id").asLong()).isEqualTo(5L);
        assertThat(jsonNode.get("item").get("name").asText()).isEqualTo("Дрель");
        assertThat(jsonNode.get("booker").get("id").asLong()).isEqualTo(2L);
    }

    @Test
    @DisplayName("BookingResponseDto должен корректно десериализоваться из JSON")
    void testBookingResponseDtoDeserialization() throws Exception {
        String json = "{\"id\": 200, \"start\": \"2026-09-01T10:00:00\", \"end\": \"2026-09-05T18:00:00\", \"status\": \"WAITING\", \"booker\": {\"id\": 3}, \"item\": {\"id\": 15, \"name\": \"Молоток\"}}";

        BookingResponseDto dto = mapper.readValue(json, BookingResponseDto.class);

        assertThat(dto.getId()).isEqualTo(200L);
        assertThat(dto.getStatus()).isEqualTo(BookingStatus.WAITING);
        assertThat(dto.getItem().getId()).isEqualTo(15L);
        assertThat(dto.getItem().getName()).isEqualTo("Молоток");
        assertThat(dto.getBooker().getId()).isEqualTo(3L);
    }
}