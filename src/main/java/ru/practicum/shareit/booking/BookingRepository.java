package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBookerIdOrderByStartDesc(Long bookerId);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :bookerId AND b.start <= :now AND b.end >= :now ORDER BY b.start DESC")
    List<Booking> findCurrentBookingsByBookerId(@Param("bookerId") Long bookerId,
                                                @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :bookerId AND b.end < :now ORDER BY b.start DESC")
    List<Booking> findPastBookingsByBookerId(@Param("bookerId") Long bookerId,
                                             @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :bookerId AND b.end > :now ORDER BY b.start DESC")
    List<Booking> findFutureBookingsByBookerId(@Param("bookerId") Long bookerId,
                                               @Param("now") LocalDateTime now);

    List<Booking> findByBookerIdAndStatus(Long bookerId, Status status);

    List<Booking> findByItemOwnerIdOrderByStartDesc(Long ownerId);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :ownerId AND b.start <= :now AND b.end >= :now ORDER BY b.start DESC")
    List<Booking> findCurrentBookingsAllItemsByBookerId(@Param("ownerId") Long ownerId,
                                                        @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :ownerId AND b.end < :now ORDER BY b.start DESC")
    List<Booking> findPastBookingsAllItemsByBookerId(@Param("ownerId") Long ownerId,
                                                     @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :ownerId AND b.end > :now ORDER BY b.start DESC")
    List<Booking> findFutureBookingsAllItemsByBookerId(@Param("ownerId") Long ownerId,
                                                       @Param("now") LocalDateTime now);

    List<Booking> findByItemOwnerIdAndStatus(Long bookerId, Status status);

    boolean existsByBookerIdAndItemIdAndStatusAndEndBefore(Long userId, Long itemId, Status status, LocalDateTime now);

    List<Booking> findAllByItemOwnerIdOrderByStartDesc(Long bookerId);
}