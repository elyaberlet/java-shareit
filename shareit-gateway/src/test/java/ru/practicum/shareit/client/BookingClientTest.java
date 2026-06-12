package ru.practicum.shareit.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.booking.BookingClient;
import ru.practicum.shareit.booking.dto.BookingCreateDto;

import java.lang.reflect.Field;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@RestClientTest(BookingClient.class)
class BookingClientTest {

    @Autowired
    private BookingClient bookingClient;

    private MockRestServiceServer mockServer;

    private final Long userId = 1L;
    private final Long bookingId = 2L;

    @TestConfiguration
    static class TestConfig {
        @Bean
        RestTemplateBuilder restTemplateBuilder() {
            return new RestTemplateBuilder();
        }
    }

    @BeforeEach
    void setUp() throws Exception {
        Field restField = BaseClient.class.getDeclaredField("rest");
        restField.setAccessible(true);
        RestTemplate restTemplate = (RestTemplate) restField.get(bookingClient);

        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    void findAllByUser_shouldSendCorrectRequest() {
        mockServer.expect(requestTo(containsString("/bookings?state=ALL&from=0&size=20")))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("X-Sharer-User-Id", String.valueOf(userId)))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("[]"));

        ResponseEntity<Object> response = bookingClient.findAllByUser(userId, "ALL", 0, 20);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        mockServer.verify();
    }

    @Test
    void findAllByOwner_shouldSendCorrectRequest() {
        mockServer.expect(requestTo(containsString("/bookings/owner?state=WAITING&from=0&size=10")))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("X-Sharer-User-Id", String.valueOf(userId)))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("[]"));

        ResponseEntity<Object> response = bookingClient.findAllByOwner(userId, "WAITING", 0, 10);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        mockServer.verify();
    }

    @Test
    void create_shouldSendCorrectRequestWithBody() {
        BookingCreateDto requestDto = new BookingCreateDto();
        requestDto.setItemId(10L);
        requestDto.setStart(LocalDateTime.now().plusDays(1));
        requestDto.setEnd(LocalDateTime.now().plusDays(2));

        mockServer.expect(requestTo(containsString("/bookings")))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("X-Sharer-User-Id", String.valueOf(userId)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andRespond(withStatus(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{\"id\": 1}"));

        ResponseEntity<Object> response = bookingClient.create(userId, requestDto);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        mockServer.verify();
    }

    @Test
    void findById_shouldSendCorrectRequest() {
        mockServer.expect(requestTo(containsString("/bookings/2")))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("X-Sharer-User-Id", String.valueOf(userId)))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{}"));

        ResponseEntity<Object> response = bookingClient.findById(userId, bookingId);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        mockServer.verify();
    }

    @Test
    void updateStatus_shouldSendCorrectPatchRequest() {
        mockServer.expect(requestTo(containsString("/bookings/2?approved=true")))
                .andExpect(method(HttpMethod.PATCH))
                .andExpect(header("X-Sharer-User-Id", String.valueOf(userId)))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{}"));

        ResponseEntity<Object> response = bookingClient.updateStatus(userId, bookingId, true);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        mockServer.verify();
    }
}