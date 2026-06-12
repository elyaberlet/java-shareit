package ru.practicum.shareit.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RequestDtoTest {

    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @Test
    void testItemRequestCreateDto() throws Exception {
        ItemRequestCreateDto dto = new ItemRequestCreateDto();
        dto.setDescription("Нужна дрель");
        String json = mapper.writeValueAsString(dto);
        assertThat(json).contains("\"description\":\"Нужна дрель\"");
    }

    @Test
    void testItemRequestResponseDto() throws Exception {
        ItemRequestResponseDto dto = new ItemRequestResponseDto();
        dto.setId(1L);
        dto.setDescription("Нужна дрель");
        dto.setCreated(LocalDateTime.now());

        ItemRequestResponseDto.ItemResponse item = new ItemRequestResponseDto.ItemResponse(5L, "Дрель", 10L);
        dto.setItems(List.of(item));

        String json = mapper.writeValueAsString(dto);

        assertThat(json).contains("id", "description", "items");

        ItemRequestResponseDto result = mapper.readValue(json, ItemRequestResponseDto.class);
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getItems().get(0).getName()).isEqualTo("Дрель");
    }
}