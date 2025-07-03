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
        User user = getUserById(userId);
        booking.setBooker(user);
        Item item = getItemById(booking.getItem().getId());
        if (!item.getAvailable()) {
            throw new IllegalStateException("Вещь не доступна для бронирования");
        }
        booking.setItem(item);
        booking.setBookingStatus(BookingStatus.WAITING);
        return bookingRepository.save(booking);
    }

    @Override
    @Transactional
    public Booking confirmBooking(Long bookingId, String approved, Long userId) {
        Booking booking = getBookingById(bookingId);
        Item item = getItemById(booking.getItem().getId());
        if (!userId.equals(item.getOwner().getId())) {
            throw new ItemOwnershipException("Пользователь не является владельцем данного товара");
        }
        if (approved.equals("true")) {
            booking.setBookingStatus(BookingStatus.APPROVED);
            item.setAvailable(false);
            itemRepository.save(item);
        } else {
            booking.setBookingStatus(BookingStatus.REJECTED);
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
        getUserById(userId);
        if (state == null) {
            return bookingRepository.findByBookerIdOrderByStartDesc(userId);
        }
        BookingState status = BookingState.valueOf(state);
        return switch (status) {
            case CURRENT -> bookingRepository.findCurrentBookingsByBookerId(userId, LocalDateTime.now());
            case PAST -> bookingRepository.findPastBookingsByBookerId(userId, LocalDateTime.now());
            case FUTURE -> bookingRepository.findFutureBookingsByBookerId(userId, LocalDateTime.now());
            case WAITING -> bookingRepository.findByBookerIdAndBookingStatus(userId, BookingStatus.WAITING);
            case REJECTED -> bookingRepository.findByBookerIdAndBookingStatus(userId, BookingStatus.REJECTED);
            default -> bookingRepository.findByBookerIdOrderByStartDesc(userId);
        };
    }

    @Override
    public List<Booking> getBookingAllItemsToUser(Long userId, String state) {
        getUserById(userId);
        if (state == null) {
            return bookingRepository.findByItemOwnerIdOrderByStartDesc(userId);
        }
        BookingState status = BookingState.valueOf(state);
        return switch (status) {
            case CURRENT -> bookingRepository.findCurrentBookingsAllItemsByBookerId(userId, LocalDateTime.now());
            case PAST -> bookingRepository.findPastBookingsAllItemsByBookerId(userId, LocalDateTime.now());
            case FUTURE -> bookingRepository.findFutureBookingsAllItemsByBookerId(userId, LocalDateTime.now());
            case WAITING -> bookingRepository.findByItemOwnerIdAndBookingStatus(userId, BookingStatus.WAITING);
            case REJECTED -> bookingRepository.findByItemOwnerIdAndBookingStatus(userId, BookingStatus.REJECTED);
            default -> bookingRepository.findByItemOwnerIdOrderByStartDesc(userId);
        };
    }

    private User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User ID= " + id + " не найден!"));
    }

    private Item getItemById(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Вещь ID= " + id + " не найдена!"));
    }
}