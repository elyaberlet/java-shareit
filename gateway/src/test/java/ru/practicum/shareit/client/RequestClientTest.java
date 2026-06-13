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
import ru.practicum.shareit.request.RequestClient;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;

import java.lang.reflect.Field;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@RestClientTest(RequestClient.class)
class RequestClientTest {

    @Autowired
    private RequestClient requestClient;

    private MockRestServiceServer mockServer;

    private final Long userId = 1L;
    private final Long requestId = 2L;

    @BeforeEach
    void setUp() throws Exception {
        Field restField = BaseClient.class.getDeclaredField("rest");
        restField.setAccessible(true);
        RestTemplate restTemplate = (RestTemplate) restField.get(requestClient);

        // Создаём MockServer и привязываем его к конкретному RestTemplate
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    void create_shouldSendCorrectPostRequest() {
        ItemRequestCreateDto dto = new ItemRequestCreateDto();
        dto.setDescription("Need a drill");

        mockServer.expect(requestTo(containsString("/requests")))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("X-Sharer-User-Id", String.valueOf(userId)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{\"id\": 1, \"description\": \"Need a drill\"}"));

        ResponseEntity<Object> response = requestClient.create(userId, dto);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        mockServer.verify();
    }

    @Test
    void findByRequester_shouldSendCorrectGetRequest() {
        mockServer.expect(requestTo(containsString("/requests")))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("X-Sharer-User-Id", String.valueOf(userId)))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("[]"));

        ResponseEntity<Object> response = requestClient.findByRequester(userId);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        mockServer.verify();
    }

    @Test
    void findAllOther_shouldSendCorrectGetRequest() {
        mockServer.expect(requestTo(containsString("/requests/all")))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("X-Sharer-User-Id", String.valueOf(userId)))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("[]"));

        ResponseEntity<Object> response = requestClient.findAllOther(userId);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        mockServer.verify();
    }

    @Test
    void findById_shouldSendCorrectGetRequest() {
        mockServer.expect(requestTo(containsString("/requests/2")))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("X-Sharer-User-Id", String.valueOf(userId)))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{\"id\": 2, \"description\": \"Need a drill\"}"));

        ResponseEntity<Object> response = requestClient.findById(userId, requestId);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        mockServer.verify();
    }
}