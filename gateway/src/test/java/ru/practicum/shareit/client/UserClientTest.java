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
import ru.practicum.shareit.user.UserClient;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import java.lang.reflect.Field;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@RestClientTest(UserClient.class)
class UserClientTest {

    @Autowired
    private UserClient userClient;

    private MockRestServiceServer mockServer;

    private final Long userId = 1L;

    @BeforeEach
    void setUp() throws Exception {
        Field restField = BaseClient.class.getDeclaredField("rest");
        restField.setAccessible(true);
        RestTemplate restTemplate = (RestTemplate) restField.get(userClient);

        // Создаём MockServer и привязываем его к конкретному RestTemplate
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    void create_shouldSendCorrectPostRequest() {
        UserCreateDto dto = new UserCreateDto();
        dto.setName("New User");
        dto.setEmail("new@example.com");

        mockServer.expect(requestTo(containsString("/users")))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{\"id\": 1, \"name\": \"New User\", \"email\": \"new@example.com\"}"));

        ResponseEntity<Object> response = userClient.create(dto);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        mockServer.verify();
    }

    @Test
    void update_shouldSendCorrectPatchRequest() {
        UserUpdateDto dto = new UserUpdateDto();
        dto.setName("Updated User");

        mockServer.expect(requestTo(containsString("/users/1")))
                .andExpect(method(HttpMethod.PATCH))
                .andExpect(header("X-Sharer-User-Id", String.valueOf(userId)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{\"id\": 1, \"name\": \"Updated User\"}"));

        ResponseEntity<Object> response = userClient.update(userId, dto);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        mockServer.verify();
    }

    @Test
    void findById_shouldSendCorrectGetRequest() {
        mockServer.expect(requestTo(containsString("/users/1")))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("X-Sharer-User-Id", String.valueOf(userId)))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{\"id\": 1, \"name\": \"User\", \"email\": \"user@example.com\"}"));

        ResponseEntity<Object> response = userClient.findById(userId);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        mockServer.verify();
    }

    @Test
    void findAll_shouldSendCorrectGetRequest() {
        mockServer.expect(requestTo(containsString("/users")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("[]"));

        ResponseEntity<Object> response = userClient.findAll();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        mockServer.verify();
    }

    @Test
    void delete_shouldSendCorrectDeleteRequest() {
        mockServer.expect(requestTo(containsString("/users/1")))
                .andExpect(method(HttpMethod.DELETE))
                .andExpect(header("X-Sharer-User-Id", String.valueOf(userId)))
                .andRespond(withStatus(HttpStatus.OK));

        ResponseEntity<Object> response = userClient.delete(userId);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        mockServer.verify();
    }
}