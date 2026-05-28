package ru.practicum.shareit.booking;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Data
public class Booking {
    private Long id;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Long itemId;
    private Long bookerId;
    private BookingStatus status;
}
