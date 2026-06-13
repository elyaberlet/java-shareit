package ru.practicum.shareit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {

    @MockBean
    private ItemService itemService;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @Test
    void create_whenRequestIsValid_shouldReturn200AndItem() throws Exception {
        Long ownerId = 1L;
        ItemCreateDto request = new ItemCreateDto();
        request.setName("New Item");
        request.setDescription("Description");
        request.setAvailable(true);

        ItemResponseDto response = new ItemResponseDto();
        response.setId(1L);
        response.setName("New Item");
        response.setDescription("Description");
        response.setAvailable(true);

        when(itemService.create(eq(ownerId), any(ItemCreateDto.class)))
                .thenReturn(response);

        mvc.perform(post("/items")
                        .header(USER_ID_HEADER, ownerId)
                        .content(mapper.writeValueAsString(request))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("New Item"))
                .andExpect(jsonPath("$.description").value("Description"))
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    void update_whenRequestIsValid_shouldReturn200AndUpdatedItem() throws Exception {
        Long ownerId = 1L;
        Long itemId = 1L;
        ItemUpdateDto request = new ItemUpdateDto();
        request.setName("Updated Name");
        request.setDescription("Updated Description");

        ItemResponseDto response = new ItemResponseDto();
        response.setId(itemId);
        response.setName("Updated Name");
        response.setDescription("Updated Description");
        response.setAvailable(true);

        when(itemService.update(eq(ownerId), eq(itemId), any(ItemUpdateDto.class)))
                .thenReturn(response);

        mvc.perform(patch("/items/{itemId}", itemId)
                        .header(USER_ID_HEADER, ownerId)
                        .content(mapper.writeValueAsString(request))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemId))
                .andExpect(jsonPath("$.name").value("Updated Name"))
                .andExpect(jsonPath("$.description").value("Updated Description"));
    }

    @Test
    void getById_whenItemExists_shouldReturn200AndItem() throws Exception {
        Long userId = 1L;
        Long itemId = 1L;

        ItemResponseDto response = new ItemResponseDto();
        response.setId(itemId);
        response.setName("Item");
        response.setDescription("Description");
        response.setAvailable(true);

        when(itemService.findById(eq(itemId), eq(userId)))
                .thenReturn(response);

        mvc.perform(get("/items/{itemId}", itemId)
                        .header(USER_ID_HEADER, userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemId))
                .andExpect(jsonPath("$.name").value("Item"))
                .andExpect(jsonPath("$.description").value("Description"));
    }

    @Test
    void getAllByOwner_whenOwnerHasItems_shouldReturn200AndCollection() throws Exception {
        Long ownerId = 1L;

        ItemResponseDto item1 = new ItemResponseDto();
        item1.setId(1L);
        item1.setName("Item 1");
        item1.setAvailable(true);

        ItemResponseDto item2 = new ItemResponseDto();
        item2.setId(2L);
        item2.setName("Item 2");
        item2.setAvailable(false);

        when(itemService.findAllByOwnerId(eq(ownerId)))
                .thenReturn(List.of(item1, item2));

        mvc.perform(get("/items")
                        .header(USER_ID_HEADER, ownerId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Item 1"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name").value("Item 2"));
    }

    @Test
    void search_whenTextIsValid_shouldReturn200AndCollection() throws Exception {
        String text = "drill";

        ItemResponseDto item = new ItemResponseDto();
        item.setId(1L);
        item.setName("Drill");
        item.setDescription("Power drill");
        item.setAvailable(true);

        when(itemService.search(eq(text)))
                .thenReturn(List.of(item));

        mvc.perform(get("/items/search")
                        .param("text", text)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Drill"));
    }

    @Test
    void search_whenTextIsBlank_shouldReturn200AndEmptyCollection() throws Exception {
        mvc.perform(get("/items/search")
                        .param("text", "   ")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(0));
    }

    @Test
    void search_whenTextIsNull_shouldReturn200AndEmptyCollection() throws Exception {
        mvc.perform(get("/items/search")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(0));
    }

    @Test
    void addComment_whenRequestIsValid_shouldReturn200AndComment() throws Exception {
        Long userId = 1L;
        Long itemId = 1L;
        CommentCreateDto request = new CommentCreateDto();
        request.setText("Great item!");

        CommentResponseDto response = new CommentResponseDto();
        response.setId(1L);
        response.setText("Great item!");
        response.setAuthorName("User");
        response.setCreated(LocalDateTime.parse("2024-01-01T12:00:00"));

        when(itemService.addComment(eq(userId), eq(itemId), any(CommentCreateDto.class)))
                .thenReturn(response);

        mvc.perform(post("/items/{itemId}/comment", itemId)
                        .header(USER_ID_HEADER, userId)
                        .content(mapper.writeValueAsString(request))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.text").value("Great item!"))
                .andExpect(jsonPath("$.authorName").value("User"));
    }
}
