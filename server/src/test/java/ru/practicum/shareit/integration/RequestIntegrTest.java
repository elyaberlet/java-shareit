package ru.practicum.shareit.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
public class RequestIntegrTest {

    @Autowired
    private ItemRequestService requestService;

    @Autowired
    private ItemRequestRepository requestRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    private User requester;
    private User otherUser;

    @BeforeEach
    void setUp() {
        itemRepository.deleteAll();
        requestRepository.deleteAll();
        userRepository.deleteAll();

        requester = createUser("Requester", "requester@example.com");
        otherUser = createUser("Other", "other@example.com");
    }

    @Test
    void create_shouldCreateRequestSuccessfully() {
        ItemRequestCreateDto dto = new ItemRequestCreateDto();
        dto.setDescription("Need a drill");

        ItemRequestResponseDto result = requestService.create(requester.getId(), dto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isNotNull();
        assertThat(result.getDescription()).isEqualTo("Need a drill");
        assertThat(result.getItems()).isEmpty();
        assertThat(requestRepository.findById(result.getId())).isPresent();
    }

    @Test
    void create_whenUserNotFound_shouldThrowNotFoundException() {
        ItemRequestCreateDto dto = new ItemRequestCreateDto();
        dto.setDescription("Need a drill");

        assertThatThrownBy(() -> requestService.create(999L, dto))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    void findByRequester_shouldReturnRequestsSortedByCreatedDesc() {
        createRequest("First request", requester);
        createRequest("Second request", requester);
        createRequest("Other request", otherUser);

        List<ItemRequestResponseDto> results = requestService.findByRequester(requester.getId());

        assertThat(results).hasSize(2);
        assertThat(results.get(0).getDescription()).isEqualTo("Second request");
        assertThat(results.get(1).getDescription()).isEqualTo("First request");
    }

    @Test
    void findByRequester_whenUserNotFound_shouldThrowNotFoundException() {
        assertThatThrownBy(() -> requestService.findByRequester(999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    void findAllByOtherUsers_shouldReturnOnlyOtherUsersRequests() {
        createRequest("My request", requester);
        createRequest("Other request 1", otherUser);
        createRequest("Other request 2", otherUser);

        List<ItemRequestResponseDto> results = requestService.findAllByOtherUsers(requester.getId());

        assertThat(results).hasSize(2);
        assertThat(results).allMatch(dto -> !dto.getDescription().equals("My request"));
    }

    @Test
    void findAllByOtherUsers_whenUserNotFound_shouldThrowNotFoundException() {
        assertThatThrownBy(() -> requestService.findAllByOtherUsers(999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    void findById_shouldReturnRequestWithItems() {
        ItemRequest request = createRequest("Need items", requester);

        Item item = createItem("Drill", true, requester, request);

        ItemRequestResponseDto result = requestService.findById(request.getId(), requester.getId());

        assertThat(result.getId()).isEqualTo(request.getId());
        assertThat(result.getDescription()).isEqualTo("Need items");
        assertThat(result.getItems()).hasSize(1);
        assertThat(result.getItems().get(0).getName()).isEqualTo("Drill");
    }

    @Test
    void findById_whenRequestNotFound_shouldThrowNotFoundException() {
        assertThatThrownBy(() -> requestService.findById(999L, requester.getId()))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Request not found");
    }

    @Test
    void findById_whenUserNotFound_shouldThrowNotFoundException() {
        ItemRequest request = createRequest("Need items", requester);

        assertThatThrownBy(() -> requestService.findById(request.getId(), 999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("User not found");
    }

    private User createUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        return userRepository.save(user);
    }

    private ItemRequest createRequest(String description, User requester) {
        ItemRequest request = new ItemRequest();
        request.setDescription(description);
        request.setRequester(requester);
        request.setCreated(LocalDateTime.now()); // ИСПРАВЛЕНИЕ: устанавливаем дату создания
        return requestRepository.save(request);
    }

    private Item createItem(String name, Boolean available, User owner, ItemRequest request) {
        Item item = new Item();
        item.setName(name);
        item.setAvailable(available);
        item.setOwner(owner);
        item.setRequest(request);
        return itemRepository.save(item);
    }
}