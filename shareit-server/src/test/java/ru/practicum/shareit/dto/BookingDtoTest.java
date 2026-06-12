package ru.practicum.shareit.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

class BookingDtoTest {

    private ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ObjectMapper();

        JavaTimeModule javaTimeModule = new JavaTimeModule();

        javaTimeModule.addSerializer(LocalDateTime.class,
                new com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer(
                        DateTimeFormatter.ISO_LOCAL_DATE_TIME
                )
        );

        mapper.registerModule(javaTimeModule);

        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    void testBookingCreateDtoSerialization() throws Exception {
        BookingCreateDto dto = new BookingCreateDto();
        dto.setItemId(5L);
        dto.setStart(LocalDateTime.of(2026, 7, 1, 10, 0, 0));
        dto.setEnd(LocalDateTime.of(2026, 7, 10, 18, 0, 0));

        String json = mapper.writeValueAsString(dto);

        assertThat(json).contains("\"itemId\":5");
        assertThat(json).contains("\"start\":\"2026-07-01T10:00:00\"");
        assertThat(json).contains("\"end\":\"2026-07-10T18:00:00\"");
    }

    @Test
    void testBookingCreateDtoDeserialization() throws Exception {
        String json = """
            {
                "itemId": 10,
                "start": "2026-08-01T10:00:00",
                "end": "2026-08-10T18:00:00"
            }
            """;

        BookingCreateDto dto = mapper.readValue(json, BookingCreateDto.class);

        assertThat(dto.getItemId()).isEqualTo(10L);
        assertThat(dto.getStart()).isEqualTo(LocalDateTime.of(2026, 8, 1, 10, 0, 0));
        assertThat(dto.getEnd()).isEqualTo(LocalDateTime.of(2026, 8, 10, 18, 0, 0));
    }

    @Test
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

        String json = mapper.writeValueAsString(dto);

        assertThat(json).contains("\"id\":100");
        assertThat(json).contains("\"status\":\"APPROVED\"");
        assertThat(json).contains("\"item\":{\"id\":5,\"name\":\"Дрель\"}");
        assertThat(json).contains("\"booker\":{\"id\":2}");
    }

    @Test
    void testBookingResponseDtoDeserialization() throws Exception {
        String json = """
            {
                "id": 200,
                "start": "2026-09-01T10:00:00",
                "end": "2026-09-05T18:00:00",
                "status": "WAITING",
                "booker": {"id": 3},
                "item": {"id": 15, "name": "Молоток"}
            }
            """;

        BookingResponseDto dto = mapper.readValue(json, BookingResponseDto.class);

        assertThat(dto.getId()).isEqualTo(200L);
        assertThat(dto.getStatus()).isEqualTo(BookingStatus.WAITING);
        assertThat(dto.getItem().getId()).isEqualTo(15L);
        assertThat(dto.getItem().getName()).isEqualTo("Молоток");
        assertThat(dto.getBooker().getId()).isEqualTo(3L);
    }
}