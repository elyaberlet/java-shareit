package ru.practicum.shareit.client;

import org.springframework.http.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

public class BaseClient {
    protected final RestTemplate rest;
    protected final String baseUrl;

    public BaseClient(RestTemplate rest, String baseUrl) {
        this.rest = rest;
        this.baseUrl = baseUrl;
    }

    protected ResponseEntity<Object> get(String path) {
        return get(path, null);
    }

    protected ResponseEntity<Object> get(String path, Long userId) {
        return sendRequest(HttpMethod.GET, path, userId, null);
    }

    protected <T> ResponseEntity<Object> post(String path, T body) {
        return sendRequest(HttpMethod.POST, path, null, body);
    }

    protected <T> ResponseEntity<Object> post(String path, Long userId, T body) {
        return sendRequest(HttpMethod.POST, path, userId, body);
    }

    protected <T> ResponseEntity<Object> patch(String path, Long userId, T body) {
        return sendRequest(HttpMethod.PATCH, path, userId, body);
    }

    protected ResponseEntity<Object> patch(String path, Long userId) {
        return sendRequest(HttpMethod.PATCH, path, userId, null);
    }

    protected ResponseEntity<Object> deleteRequest(String path, Long userId) {
        return sendRequest(HttpMethod.DELETE, path, userId, null);
    }

    private <T> ResponseEntity<Object> sendRequest(HttpMethod method, String path, Long userId, T body) {
        String url = baseUrl + path;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (userId != null) {
            headers.set("X-Sharer-User-Id", String.valueOf(userId));
        }

        HttpEntity<T> entity = new HttpEntity<>(body, headers);

        try {
            return rest.exchange(url, method, entity, Object.class);
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
        }
    }
}