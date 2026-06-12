package ru.practicum.shareit.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
public class BookingIntegrTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    private User owner;
    private User booker;
    private User otherUser;
    private Item availableItem;
    private Item unavailableItem;

    @BeforeEach
    void setUp() {
        bookingRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();

        owner = createUser("Owner", "owner@example.com");
        booker = createUser("Booker", "booker@example.com");
        otherUser = createUser("Other", "other@example.com");

        availableItem = createItem("Available Item", "Description", true, owner);
        unavailableItem = createItem("Unavailable Item", "Description", false, owner);
    }

    @Test
    void createBooking_shouldCreateBookingSuccessfully() {
        BookingCreateDto dto = new BookingCreateDto();
        dto.setItemId(availableItem.getId());
        dto.setStart(LocalDateTime.now().plusDays(1));
        dto.setEnd(LocalDateTime.now().plusDays(2));

        BookingResponseDto result = bookingService.createBooking(booker.getId(), dto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isNotNull();
        assertThat(result.getStatus()).isEqualTo(BookingStatus.WAITING);
        assertThat(result.getBooker().getId()).isEqualTo(booker.getId());
        assertThat(result.getItem().getId()).isEqualTo(availableItem.getId());

        assertThat(bookingRepository.findById(result.getId())).isPresent();
    }

    @Test
    void createBooking_whenOwnerBooksOwnItem_shouldThrowAccessDeniedException() {
        BookingCreateDto dto = new BookingCreateDto();
        dto.setItemId(availableItem.getId());
        dto.setStart(LocalDateTime.now().plusDays(1));
        dto.setEnd(LocalDateTime.now().plusDays(2));

        assertThatThrownBy(() -> bookingService.createBooking(owner.getId(), dto))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("Owner cannot book own item");
    }

    @Test
    void approveBooking_whenOwnerApproves_shouldChangeStatusToApproved() {
        Booking booking = createAndSaveBooking(BookingStatus.WAITING, booker, availableItem);

        BookingResponseDto result = bookingService.approveBooking(owner.getId(), booking.getId(), true);

        assertThat(result.getStatus()).isEqualTo(BookingStatus.APPROVED);
        assertThat(bookingRepository.findById(booking.getId()).get().getStatus()).isEqualTo(BookingStatus.APPROVED);
    }

    @Test
    void approveBooking_whenNonOwnerTriesToApprove_shouldThrowAccessDeniedException() {

        Booking booking = createAndSaveBooking(BookingStatus.WAITING, booker, availableItem);

        assertThatThrownBy(() -> bookingService.approveBooking(booker.getId(), booking.getId(), true))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("Только владелец вещи может подтверждать или отклонять бронирование");
    }

    @Test
    void getBookingById_whenBookerRequests_shouldReturnBooking() {
        Booking booking = createAndSaveBooking(BookingStatus.APPROVED, booker, availableItem);

        BookingResponseDto result = bookingService.getBookingById(booker.getId(), booking.getId());

        assertThat(result.getId()).isEqualTo(booking.getId());
        assertThat(result.getBooker().getId()).isEqualTo(booker.getId());
    }

    @Test
    void getBookingById_whenUnauthorizedUserRequests_shouldThrowAccessDeniedException() {
        Booking booking = createAndSaveBooking(BookingStatus.APPROVED, booker, availableItem);

        assertThatThrownBy(() -> bookingService.getBookingById(otherUser.getId(), booking.getId()))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("У вас нет доступа к этому бронированию");
    }

    @Test
    void getUserBookings_whenStateAll_shouldReturnAllBookersBookingsSortedByDateDesc() {
        createAndSaveBooking(BookingStatus.APPROVED, booker, availableItem, LocalDateTime.now().plusDays(5), LocalDateTime.now().plusDays(6));
        createAndSaveBooking(BookingStatus.WAITING, booker, availableItem, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));
        createAndSaveBooking(BookingStatus.REJECTED, otherUser, availableItem, LocalDateTime.now().plusDays(3), LocalDateTime.now().plusDays(4));

        List<BookingResponseDto> results = bookingService.getUserBookings(booker.getId(), "ALL", 0, 10);

        assertThat(results).hasSize(2); // Только бронирования booker'а
        assertThat(results.get(0).getStart()).isAfter(results.get(1).getStart()); // Проверка сортировки DESC
    }

    @Test
    void getOwnerBookings_whenStateWaiting_shouldReturnOnlyWaitingBookingsForOwnerItems() {
        createAndSaveBooking(BookingStatus.WAITING, booker, availableItem);
        createAndSaveBooking(BookingStatus.APPROVED, booker, availableItem);
        createAndSaveBooking(BookingStatus.WAITING, booker, unavailableItem);

        List<BookingResponseDto> results = bookingService.getOwnerBookings(owner.getId(), "WAITING", 0, 10);

        assertThat(results).hasSize(2);
        assertThat(results).allMatch(dto -> dto.getStatus() == BookingStatus.WAITING);
    }

    private User createUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        return userRepository.save(user);
    }

    private Item createItem(String name, String description, Boolean available, User owner) {
        Item item = new Item();
        item.setName(name);
        item.setDescription(description);
        item.setAvailable(available);
        item.setOwner(owner);
        return itemRepository.save(item);
    }

    private Booking createAndSaveBooking(BookingStatus status, User bookerUser, Item item) {
        return createAndSaveBooking(status, bookerUser, item, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));
    }

    private Booking createAndSaveBooking(BookingStatus status, User bookerUser, Item item, LocalDateTime start, LocalDateTime end) {
        Booking booking = new Booking();
        booking.setStartDate(start);
        booking.setEndDate(end);
        booking.setItem(item);
        booking.setStatus(status);
        booking.setBooker(bookerUser);
        return bookingRepository.save(booking);
    }
}