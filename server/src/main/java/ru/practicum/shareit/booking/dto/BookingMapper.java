package ru.practicum.shareit.booking.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

@Component
public class BookingMapper {
    public BookingResponseDto toResponseDto(Booking booking) {
        if (booking == null) return null;

        BookingResponseDto dto = new BookingResponseDto();
        dto.setId(booking.getId());
        dto.setStart(booking.getStartDate());
        dto.setEnd(booking.getEndDate());
        dto.setStatus(booking.getStatus());

        User booker = booking.getBooker();
        BookingResponseDto.BookerDto bookerDto = new BookingResponseDto.BookerDto();
        bookerDto.setId(booker.getId());
        dto.setBooker(bookerDto);

        Item item = booking.getItem();
        BookingResponseDto.ItemDto itemDto = new BookingResponseDto.ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        dto.setItem(itemDto);
        return dto;
    }
}
