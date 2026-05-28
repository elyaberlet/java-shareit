package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImp implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final ItemMapper itemMapper;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final UserRepository userRepository;

    @Override
    public ItemResponseDto create(Long ownerId, ItemCreateDto createDto) {

        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Item item = itemMapper.toEntity(createDto, ownerId);
        item.setOwner(owner);

        Item savedItem = itemRepository.save(item);

        return itemMapper.toResponseDto(savedItem);
    }

    @Override
    public ItemResponseDto update(Long ownerId, Long itemId, ItemUpdateDto updateDto) {
        Item existing = findByIdEntity(itemId);

        if (!existing.getOwner().getId().equals(ownerId)) {
            throw new NotFoundException("Только владелец может редактировать вещь");
        }

        if (updateDto.getName() != null) {
            existing.setName(updateDto.getName());
        }

        if (updateDto.getDescription() != null) {
            existing.setDescription(updateDto.getDescription());
        }

        if (updateDto.getAvailable() != null) {
            existing.setAvailable(updateDto.getAvailable());
        }

        Item updatedItem = itemRepository.save(existing);
        return itemMapper.toResponseDto(updatedItem);
    }

    @Override
    public ItemResponseDto findById(Long id, Long userId) {
        Item item = findByIdEntity(id);
        ItemResponseDto dto = itemMapper.toResponseDto(item);

        if (item.getOwner().getId().equals(userId)) {
            addBookingInfo(dto, item);
        }

        addCommentsInfo(dto, item);
        return dto;
    }


    @Override
    public List<ItemResponseDto> findAllByOwnerId(Long ownerId) {
        userService.findById(ownerId);

        return itemRepository.findByOwnerId(ownerId).stream()
                .map(item -> {
                    ItemResponseDto dto = itemMapper.toResponseDto(item);
                    addBookingInfo(dto, item);
                    addCommentsInfo(dto, item);  // ← ДОБАВИТЬ комментарии
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemResponseDto> search(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }

        String lowerText = text.toLowerCase();

        return itemRepository.findAll().stream()
                .filter(Item::getAvailable)
                .filter(item -> (item.getName() != null && item.getName().toLowerCase().contains(lowerText)) ||
                        (item.getDescription() != null && item.getDescription().toLowerCase().contains(lowerText))
                )
                .map(itemMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentResponseDto addComment(Long userId, Long itemId, CommentCreateDto createDto) {
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Item item = findByIdEntity(itemId);

        LocalDateTime now = LocalDateTime.now();
        List<Booking> finishedBookings = bookingRepository.findFinishedBookingsByUserAndItem(author, item, now);

        if (finishedBookings.isEmpty()) {
            throw new ValidationException("Пользователь не брал эту вещь в аренду или бронирование ещё не завершено");
        }

        Comment comment = new Comment();
        comment.setText(createDto.getText());
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(now);

        Comment saved = commentRepository.save(comment);

        return commentMapper.toResponseDto(saved);
    }

    private Item findByIdEntity(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Вещь с id = " + id + " не найдена"));
    }

    private void addBookingInfo(ItemResponseDto dto, Item item) {
        LocalDateTime now = LocalDateTime.now();

        bookingRepository.findLastBookingByItem(item.getId(), now)
                .ifPresent(last -> dto.setLastBooking(new ItemResponseDto.BookingInfo(
                        last.getId(),
                        last.getBooker().getId(),
                        last.getStartDate(),
                        last.getEndDate()
                )));

        bookingRepository.findNextBookingByItem(item.getId(), now)
                .ifPresent(next -> dto.setNextBooking(new ItemResponseDto.BookingInfo(
                        next.getId(),
                        next.getBooker().getId(),
                        next.getStartDate(),
                        next.getEndDate()
                )));
    }

    private void addCommentsInfo(ItemResponseDto dto, Item item) {
        List<CommentResponseDto> comments = commentRepository.findByItemOrderByCreatedDesc(item).stream()
                .map(commentMapper::toResponseDto)
                .collect(Collectors.toList());
        dto.setComments(comments);
    }
}