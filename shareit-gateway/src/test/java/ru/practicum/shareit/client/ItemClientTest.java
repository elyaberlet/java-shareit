package ru.practicum.shareit.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.item.ItemClient;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import java.lang.reflect.Field;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@RestClientTest(ItemClient.class)
class ItemClientTest {

    @Autowired
    private ItemClient itemClient;

    private MockRestServiceServer mockServer;

    private final Long userId = 1L;
    private final Long itemId = 2L;

    @BeforeEach
    void setUp() throws Exception {
        // Достаём поле 'rest' из базового класса BaseClient через рефлексию
        Field restField = BaseClient.class.getDeclaredField("rest");
        restField.setAccessible(true);
        RestTemplate restTemplate = (RestTemplate) restField.get(itemClient);

        // Создаём MockServer и привязываем его к конкретному RestTemplate
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    void create_shouldSendCorrectPostRequest() {
        ItemCreateDto dto = new ItemCreateDto();
        dto.setName("Drill");
        dto.setDescription("Power drill");
        dto.setAvailable(true);

        mockServer.expect(requestTo(containsString("/items")))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("X-Sharer-User-Id", String.valueOf(userId)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{\"id\": 1, \"name\": \"Drill\"}"));

        ResponseEntity<Object> response = itemClient.create(userId, dto);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        mockServer.verify();
    }

    @Test
    void update_shouldSendCorrectPatchRequest() {
        ItemUpdateDto dto = new ItemUpdateDto();
        dto.setName("Updated Drill");

        mockServer.expect(requestTo(containsString("/items/2")))
                .andExpect(method(HttpMethod.PATCH))
                .andExpect(header("X-Sharer-User-Id", String.valueOf(userId)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{\"id\": 2, \"name\": \"Updated Drill\"}"));

        ResponseEntity<Object> response = itemClient.update(userId, itemId, dto);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        mockServer.verify();
    }

    @Test
    void findById_shouldSendCorrectGetRequest() {
        mockServer.expect(requestTo(containsString("/items/2")))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("X-Sharer-User-Id", String.valueOf(userId)))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{\"id\": 2, \"name\": \"Drill\"}"));

        ResponseEntity<Object> response = itemClient.findById(itemId, userId);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        mockServer.verify();
    }

    @Test
    void findAllByOwner_shouldSendCorrectGetRequest() {
        mockServer.expect(requestTo(containsString("/items")))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("X-Sharer-User-Id", String.valueOf(userId)))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("[]"));

        ResponseEntity<Object> response = itemClient.findAllByOwner(userId);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        mockServer.verify();
    }

    @Test
    void search_shouldSendCorrectGetRequest() {
        String text = "drill";

        mockServer.expect(requestTo(containsString("/items/search?text=" + text)))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("[]"));

        ResponseEntity<Object> response = itemClient.search(text);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        mockServer.verify();
    }

    @Test
    void addComment_shouldSendCorrectPostRequest() {
        CommentCreateDto dto = new CommentCreateDto();
        dto.setText("Great item!");

        mockServer.expect(requestTo(containsString("/items/2/comment")))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("X-Sharer-User-Id", String.valueOf(userId)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{\"id\": 1, \"text\": \"Great item!\"}"));

        ResponseEntity<Object> response = itemClient.addComment(userId, itemId, dto);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        mockServer.verify();
    }
}
