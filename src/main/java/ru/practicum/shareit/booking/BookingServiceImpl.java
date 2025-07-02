package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.IllegalStateException;
import ru.practicum.shareit.exception.ItemOwnershipException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public Booking createBooking(Booking booking, Long userId) {
        User user = validateUserById(userId);
        booking.setBooker(user);
        Item item = validateItemById(booking.getItem().getId());
        if (!item.getAvailable()) {
            throw new IllegalStateException("Вещь не доступна для бронирования");
        }
        booking.setItem(item);
        booking.setStatus(Status.WAITING);
        return bookingRepository.save(booking);
    }

    @Override
    @Transactional
    public Booking confirmBooking(Long bookingId, String approved, Long userId) {
        Booking booking = getBookingById(bookingId);
        Item item = validateItemById(booking.getItem().getId());
        if (!userId.equals(item.getOwner().getId())) {
            throw new ItemOwnershipException("Пользователь не является владельцем данного товара");
        }
        if (approved.equals("true")) {
            booking.setStatus(Status.APPROVED);
            item.setAvailable(false);
            itemRepository.save(item);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        return bookingRepository.save(booking);
    }

    @Override
    public Booking getBookingById(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование ID= " + bookingId + " не найдено!"));
    }

    @Override
    public List<Booking> getAllBookingToUser(Long userId, String state) {
        validateUserById(userId);
        if (state == null) {
            return bookingRepository.findByBookerIdOrderByStartDesc(userId);
        }
        State status = State.valueOf(state);
        switch (status) {
            case CURRENT:
                return bookingRepository.findCurrentBookingsByBookerId(userId, LocalDateTime.now());
            case PAST:
                return bookingRepository.findPastBookingsByBookerId(userId, LocalDateTime.now());
            case FUTURE:
                return bookingRepository.findFutureBookingsByBookerId(userId, LocalDateTime.now());
            case WAITING:
                return bookingRepository.findByBookerIdAndStatus(userId, Status.WAITING);
            case REJECTED:
                return bookingRepository.findByBookerIdAndStatus(userId, Status.REJECTED);
            default:
                return bookingRepository.findByBookerIdOrderByStartDesc(userId);
        }
    }

    @Override
    public List<Booking> getBookingAllItemsToUser(Long userId, String state) {
        validateUserById(userId);
        if (state == null) {
            return bookingRepository.findByItemOwnerIdOrderByStartDesc(userId);
        }
        State status = State.valueOf(state);
        switch (status) {
            case CURRENT:
                return bookingRepository.findCurrentBookingsAllItemsByBookerId(userId, LocalDateTime.now());
            case PAST:
                return bookingRepository.findPastBookingsAllItemsByBookerId(userId, LocalDateTime.now());
            case FUTURE:
                return bookingRepository.findFutureBookingsAllItemsByBookerId(userId, LocalDateTime.now());
            case WAITING:
                return bookingRepository.findByItemOwnerIdAndStatus(userId, Status.WAITING);
            case REJECTED:
                return bookingRepository.findByItemOwnerIdAndStatus(userId, Status.REJECTED);
            default:
                return bookingRepository.findByItemOwnerIdOrderByStartDesc(userId);
        }
    }

    private User validateUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User ID= " + id + " не найден!"));
    }

    private Item validateItemById(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Вещь ID= " + id + " не найдена!"));
    }
}