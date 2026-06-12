package ru.practicum.shareit.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
public class ItemIntegrTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private CommentRepository commentRepository;

    private User owner;
    private User booker;
    private Item item;

    @BeforeEach
    void setUp() {
        commentRepository.deleteAll();
        bookingRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();

        owner = userRepository.save(new User(null, "Owner", "owner@example.com"));
        booker = userRepository.save(new User(null, "Booker", "booker@example.com"));

        item = itemRepository.save(new Item(null, "Item", "Description", true, owner, null));
    }

    @Test
    void create_shouldCreateItemSuccessfully() {
        ItemCreateDto dto = new ItemCreateDto();
        dto.setName("New Item");
        dto.setDescription("New Desc");
        dto.setAvailable(true);

        ItemResponseDto result = itemService.create(owner.getId(), dto);

        assertThat(result.getId()).isNotNull();
        assertThat(result.getName()).isEqualTo("New Item");
        assertThat(result.getDescription()).isEqualTo("New Desc");
        assertThat(result.getAvailable()).isTrue();
    }

    @Test
    void update_whenOwnerUpdates_shouldUpdateItem() {
        ItemUpdateDto dto = new ItemUpdateDto();
        dto.setName("Updated Name");
        dto.setDescription("Updated Desc");

        ItemResponseDto result = itemService.update(owner.getId(), item.getId(), dto);

        assertThat(result.getName()).isEqualTo("Updated Name");
        assertThat(result.getDescription()).isEqualTo("Updated Desc");
    }

    @Test
    void update_whenNonOwnerUpdates_shouldThrowNotFoundException() {
        ItemUpdateDto dto = new ItemUpdateDto();
        dto.setName("Updated Name");

        assertThatThrownBy(() -> itemService.update(booker.getId(), item.getId(), dto))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Только владелец может редактировать вещь");
    }

    @Test
    void findById_whenOwnerRequests_shouldReturnItem() {
        ItemResponseDto result = itemService.findById(item.getId(), owner.getId());

        assertThat(result.getId()).isEqualTo(item.getId());
        assertThat(result.getName()).isEqualTo("Item");
    }

    @Test
    void findById_whenNonOwnerRequests_shouldReturnItemWithoutBookingInfo() {
        ItemResponseDto result = itemService.findById(item.getId(), booker.getId());

        assertThat(result.getId()).isEqualTo(item.getId());
        assertThat(result.getLastBooking()).isNull();
        assertThat(result.getNextBooking()).isNull();
    }

    @Test
    void findAllByOwnerId_shouldReturnAllItems() {
        itemRepository.save(new Item(null, "Item 2", "Desc 2", true, owner, null));

        List<ItemResponseDto> results = itemService.findAllByOwnerId(owner.getId());

        assertThat(results).hasSize(2);
    }

    @Test
    void search_whenTextMatches_shouldReturnItems() {
        List<ItemResponseDto> results = itemService.search("Item");

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getName()).isEqualTo("Item");
    }

    @Test
    void search_whenTextBlank_shouldReturnEmptyList() {
        List<ItemResponseDto> results = itemService.search("   ");

        assertThat(results).isEmpty();
    }

    @Test
    void addComment_whenBookingFinished_shouldAddComment() {
        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.APPROVED);
        booking.setStartDate(LocalDateTime.now().minusDays(2));
        booking.setEndDate(LocalDateTime.now().minusDays(1));
        bookingRepository.save(booking);

        CommentCreateDto dto = new CommentCreateDto();
        dto.setText("Great item!");

        CommentResponseDto result = itemService.addComment(booker.getId(), item.getId(), dto);

        assertThat(result.getId()).isNotNull();
        assertThat(result.getText()).isEqualTo("Great item!");
    }

    @Test
    void addComment_whenNoFinishedBooking_shouldThrowValidationException() {
        CommentCreateDto dto = new CommentCreateDto();
        dto.setText("Great item!");

        assertThatThrownBy(() -> itemService.addComment(booker.getId(), item.getId(), dto))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Пользователь не брал эту вещь в аренду или бронирование ещё не завершено");
    }
}