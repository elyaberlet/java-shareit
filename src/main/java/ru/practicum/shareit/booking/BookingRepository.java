package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("SELECT COUNT(b) > 0 FROM Booking b WHERE b.item = :item " +
            "AND b.status = 'APPROVED'" +
            "AND b.startDate < :end AND b.endDate > :start")
    boolean isBookingDateFree(@Param("item") Item item,
                              @Param("start") LocalDateTime start,
                              @Param("end") LocalDateTime end);

    @Query("SELECT b FROM Booking b WHERE b.item.owner = :owner ORDER BY b.startDate DESC")
    List<Booking> findByItemOwner(@Param("owner") User owner, Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.booker = :booker " +
            "AND b.startDate <= :now AND b.endDate >= :now ORDER BY b.startDate DESC")
    List<Booking> findCurrentByBooker(@Param("booker") User booker,
                                      @Param("now") LocalDateTime now,
                                      Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.booker = :booker " +
            "AND b.startDate > :now ORDER BY b.startDate DESC")
    List<Booking> findFutureByBooker(@Param("booker") User booker,
                                     @Param("now") LocalDateTime now,
                                     Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.booker = :booker " +
            "AND b.endDate < :now ORDER BY b.startDate DESC")
    List<Booking> findPastByBooking(@Param("booker") User booker,
                                    @Param("now") LocalDateTime now,
                                    Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.item.owner = :owner " +
            "AND b.status = :status ORDER BY b.startDate DESC")
    List<Booking> findByItemOwnerAndStatus(@Param("owner") User owner,
                                           @Param("status") BookingStatus status,
                                           Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.item.owner = :owner " +
            "AND b.startDate <= :now AND b.endDate >= :now ORDER BY b.startDate DESC")
    List<Booking> findCurrentByItemOwner(@Param("owner") User owner,
                                         @Param("now") LocalDateTime now,
                                         Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.item.owner = :owner " +
            "AND b.startDate > :now ORDER BY b.startDate DESC")
    List<Booking> findFutureByItemOwner(@Param("owner") User owner,
                                        @Param("now") LocalDateTime now,
                                        Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.item.owner = :owner " +
            "AND b.endDate < :now ORDER BY b.startDate DESC")
    List<Booking> findPastByItemOwner(@Param("owner") User owner,
                                      @Param("now") LocalDateTime now,
                                      Pageable pageable);

    @Query(value = "SELECT * FROM bookings b WHERE b.item_id = :itemId " +
            "AND b.status = 'APPROVED' " +
            "AND b.start_date < :now " +
            "ORDER BY b.start_date DESC LIMIT 1", nativeQuery = true)
    Optional<Booking> findLastBookingByItem(@Param("itemId") Long itemId,
                                            @Param("now") LocalDateTime now);

    @Query(value = "SELECT * FROM bookings b WHERE b.item_id = :itemId " +
            "AND b.status = 'APPROVED' " +
            "AND b.start_date > :now " +
            "ORDER BY b.start_date ASC LIMIT 1", nativeQuery = true)
    Optional<Booking> findNextBookingByItem(@Param("itemId") Long itemId,
                                            @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.booker = :user " +
            "AND b.item = :item " +
            "AND b.status = 'APPROVED' " +
            "AND b.endDate < :now")
    List<Booking> findFinishedBookingsByUserAndItem(@Param("user") User user,
                                                    @Param("item") Item item,
                                                    @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.booker = :booker ORDER BY b.startDate DESC")
    List<Booking> findAllByBooker(@Param("booker") User booker, Pageable pageable);
}
