package ru.practicum.shareit.booking;

import jakarta.transaction.Transactional;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.User;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final BookingMapper bookingMapper;
    private final UserRepository userRepository;

    @Override
    public BookingResponseDto createBooking(Long userId, BookingCreateDto bookingCreateDto) {
        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));


        Item item = itemRepository.findById(bookingCreateDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Вещь с id " + bookingCreateDto.getItemId() + " не найдена"));

        if (item.getOwner().getId().equals(userId)) {
            throw new AccessDeniedException("Владелец не может забронировать свою вещь");
        }

        if (!item.getAvailable()) {
            throw new ValidationException("Вещь с id " + item.getId() + " недоступна для бронирования");
        }

        LocalDateTime start = bookingCreateDto.getStart();
        LocalDateTime end = bookingCreateDto.getEnd();
        LocalDateTime now = LocalDateTime.now();

        if (end.isBefore(start) || end.equals(start)) {
            throw new ValidationException("Дата окончания должна быть позже даты начала");
        }

        if (bookingRepository.isBookingDateFree(item, start, end)) {
            throw new ConflictException("Вещь уже забронирована");
        }

        Booking booking = new Booking();
        booking.setStartDate(start);
        booking.setEndDate(end);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);
        booking.setBooker(booker);

        Booking savedBooking = bookingRepository.save(booking);

        return bookingMapper.toResponseDto(savedBooking);
    }

    @Override
    public BookingResponseDto approveBooking(Long userId, Long bookingId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с id " + bookingId + " не найдено"));

        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new AccessDeniedException("Только владелец вещи может подтверждать или отклонять бронирование");
        }

        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new ValidationException("Бронирование уже обработано. Текущий статус: " + booking.getStatus());
        }

        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }

        Booking updated = bookingRepository.save(booking);
        return bookingMapper.toResponseDto(updated);
    }

    @Override
    public BookingResponseDto getBookingById(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с id " + bookingId + " не найдено"));

        boolean isBooker = booking.getBooker().getId().equals(userId);
        boolean isOwner = booking.getItem().getOwner().getId().equals(userId);

        if (!isOwner && !isBooker) {
            throw new AccessDeniedException("У вас нет доступа к этому бронированию");
        }

        return bookingMapper.toResponseDto(booking);
    }

    @Override
    public List<BookingResponseDto> getUserBookings(Long userId, String state, Integer from, Integer size) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));


        int page = from / size;
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "startDate"));

        List<Booking> bookings;

        LocalDateTime now = LocalDateTime.now();

        bookings = switch (state.toUpperCase()) {
            case "ALL" -> bookingRepository.findAllByBooker(user, pageable);
            case "CURRENT" -> bookingRepository.findCurrentByBooker(user, now, pageable);
            case "FUTURE" -> bookingRepository.findFutureByBooker(user, now, pageable);
            case "PAST" -> bookingRepository.findPastByBooking(user, now, pageable);
            case "WAITING" -> bookingRepository.findByItemOwnerAndStatus(user, BookingStatus.WAITING, pageable);
            case "REJECTED" -> bookingRepository.findByItemOwnerAndStatus(user, BookingStatus.REJECTED, pageable);
            default -> throw new ValidationException("Unknown state: " + state);
        };

        return bookings.stream()
                .map(bookingMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingResponseDto> getOwnerBookings(Long userId, String state, Integer from, Integer size) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        int page = from / size;
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "startDate"));

        List<Booking> bookings;

        LocalDateTime now = LocalDateTime.now();

        bookings = switch (state.toUpperCase()) {
            case "ALL" -> bookingRepository.findByItemOwner(owner, pageable);
            case "CURRENT" -> bookingRepository.findCurrentByItemOwner(owner, now, pageable);
            case "FUTURE" -> bookingRepository.findFutureByItemOwner(owner, now, pageable);
            case "PAST" -> bookingRepository.findPastByItemOwner(owner, now, pageable);
            case "WAITING" -> bookingRepository.findByItemOwnerAndStatus(owner, BookingStatus.WAITING, pageable);
            case "REJECTED" -> bookingRepository.findByItemOwnerAndStatus(owner, BookingStatus.REJECTED, pageable);
            default ->
                    throw new ValidationException("Unknown state: " + state + ". Доступные: ALL, CURRENT, FUTURE, PAST, WAITING, REJECTED");
        };

        return bookings.stream()
                .map(bookingMapper::toResponseDto)
                .collect(Collectors.toList());
    }
}
