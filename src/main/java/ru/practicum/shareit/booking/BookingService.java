package ru.practicum.shareit.booking;

import java.util.List;

public interface BookingService {

    Booking createBooking(Booking booking, Long userId);

    Booking confirmBooking(Long bookingId, String approved, Long userId);

    Booking getBookingById(Long bookingId);

    List<Booking> getAllBookingToUser(Long userId, String state);

    List<Booking> getBookingAllItemsToUser(Long userId, String state);
}