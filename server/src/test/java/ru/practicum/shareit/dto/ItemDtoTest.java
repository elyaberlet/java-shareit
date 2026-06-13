package ru.practicum.shareit.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.*;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ItemDtoTest {

    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @Test
    void testCommentCreateDto() throws Exception {
        CommentCreateDto dto = new CommentCreateDto();
        dto.setText("Отличная вещь!");

        String json = mapper.writeValueAsString(dto);
        assertThat(json).contains("\"text\":\"Отличная вещь!\"");

        CommentCreateDto result = mapper.readValue(json, CommentCreateDto.class);
        assertThat(result.getText()).isEqualTo("Отличная вещь!");
    }

    @Test
    void testCommentResponseDto() throws Exception {
        CommentResponseDto dto = new CommentResponseDto(
                1L,
                "Отличная вещь!",
                "Иван",
                LocalDateTime.of(2024, 1, 15, 10, 30, 0)
        );

        String json = mapper.writeValueAsString(dto);

        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"text\":\"Отличная вещь!\"");
        assertThat(json).contains("\"authorName\":\"Иван\"");

        CommentResponseDto result = mapper.readValue(json, CommentResponseDto.class);
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getText()).isEqualTo("Отличная вещь!");
    }


    @Test
    void testItemCreateDto() throws Exception {
        ItemCreateDto dto = new ItemCreateDto();
        dto.setName("Дрель");
        dto.setDescription("Мощная дрель");
        dto.setAvailable(true);
        dto.setRequestId(5L);

        String json = mapper.writeValueAsString(dto);

        assertThat(json).contains("\"name\":\"Дрель\"");
        assertThat(json).contains("\"description\":\"Мощная дрель\"");
        assertThat(json).contains("\"available\":true");
        assertThat(json).contains("\"requestId\":5");

        ItemCreateDto result = mapper.readValue(json, ItemCreateDto.class);
        assertThat(result.getName()).isEqualTo("Дрель");
        assertThat(result.getAvailable()).isTrue();
    }

    @Test
    void testItemResponseDto() throws Exception {
        ItemResponseDto dto = new ItemResponseDto();
        dto.setId(1L);
        dto.setName("Дрель");
        dto.setDescription("Мощная дрель");
        dto.setAvailable(true);
        dto.setOwnerId(10L);
        dto.setRequestId(5L);

        CommentResponseDto comment = new CommentResponseDto(
                100L, "Хорошая вещь!", "Анна", LocalDateTime.now()
        );
        dto.setComments(List.of(comment));

        String json = mapper.writeValueAsString(dto);

        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"name\":\"Дрель\"");
        assertThat(json).contains("\"available\":true");
        assertThat(json).contains("\"comments\"");

        ItemResponseDto result = mapper.readValue(json, ItemResponseDto.class);
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Дрель");
        assertThat(result.getComments()).hasSize(1);
    }

    @Test
    void testItemUpdateDto() throws Exception {
        ItemUpdateDto dto = new ItemUpdateDto();
        dto.setName("Новая дрель");
        dto.setDescription("Обновлённое описание");
        dto.setAvailable(false);

        String json = mapper.writeValueAsString(dto);

        assertThat(json).contains("\"name\":\"Новая дрель\"");
        assertThat(json).contains("\"available\":false");

        ItemUpdateDto result = mapper.readValue(json, ItemUpdateDto.class);
        assertThat(result.getName()).isEqualTo("Новая дрель");
        assertThat(result.getAvailable()).isFalse();
    }
}