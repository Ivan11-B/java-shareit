package ru.practicum.shareit.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingServiceImpl;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exception.IllegalStateException;
import ru.practicum.shareit.exception.ItemOwnershipException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static junit.framework.Assert.assertNotNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemService itemService;
    @Mock
    private UserService userService;
    @InjectMocks
    private BookingServiceImpl bookingService;
    Long id = 1L;
    Long itemId;
    LocalDateTime start = LocalDateTime.now();
    LocalDateTime end = LocalDateTime.now().plus(1, ChronoUnit.MINUTES);

    Long userId = 1L;
    String nameUser = "Tom";
    String email = "Tom@mail.ru";
    String nameItem = "Phone";
    String description = "Call";
    boolean available = true;

    @Test
    void createBooking_shouldReturnBooking() {
        User user = User.builder().id(userId).name(nameUser).email(email).build();
        when(userService.getUserById(userId)).thenReturn(user);
        Item item = Item.builder().id(itemId).name(nameItem).description(description).available(available).build();
        Booking booking = Booking.builder().start(start).end(end).item(item).build();
        when(itemService.getItemById(itemId)).thenReturn(item);
        Booking createdBooking = Booking.builder().id(id).start(start).end(end).build();
        when(bookingRepository.save(booking)).thenReturn(createdBooking);

        Booking actualBooking = bookingService.createBooking(booking, userId);

        assertNotNull(actualBooking.getId());
        assertThat(actualBooking).usingRecursiveComparison()
                .ignoringFields("id", "booker", "bookingStatus", "item").isEqualTo(booking);

    }

    @Test
    void createBooking_IllegalState_shouldReturnIllegalStateException() {
        User user = User.builder().id(userId).name(nameUser).email(email).build();
        when(userService.getUserById(userId)).thenReturn(user);
        Item item = Item.builder().id(itemId).name(nameItem).description(description).available(false).build();
        Booking booking = Booking.builder().start(start).end(end).item(item).build();
        when(itemService.getItemById(itemId)).thenReturn(item);

        assertThrows(IllegalStateException.class, () -> bookingService.createBooking(booking, userId));
    }

    @Test
    void confirmBooking_ItemOwnership_shouldReturnItemOwnershipException() {
        User user = User.builder().id(userId).name(nameUser).email(email).build();
        Item item = Item.builder().id(itemId).name(nameItem).description(description).owner(user).available(available).build();
        when(itemService.getItemById(itemId)).thenReturn(item);
        Booking booking = Booking.builder().id(id).start(start).end(end).item(item).build();
        when(bookingRepository.findById(id)).thenReturn(Optional.ofNullable(booking));

        assertThrows(ItemOwnershipException.class, () -> bookingService.confirmBooking(id, "true", 10L));
    }

    @Test
    void confirmBooking_shouldReturnUpdateBooking() {
        User user = User.builder().id(userId).name(nameUser).email(email).build();
        Item item = Item.builder().id(itemId).name(nameItem).description(description).owner(user).available(available).build();
        when(itemService.getItemById(itemId)).thenReturn(item);
        Booking booking = Booking.builder().id(id).start(start).end(end).item(item).build();
        when(bookingRepository.findById(id)).thenReturn(Optional.ofNullable(booking));
        Booking updatedBooking = Booking.builder().id(id).start(start).end(end).bookingStatus(BookingStatus.APPROVED).build();
        when(bookingRepository.save(booking)).thenReturn(updatedBooking);

        Booking actualBooking = bookingService.confirmBooking(id, "true", userId);

        assertNotNull(actualBooking.getId());
        assertThat(actualBooking).usingRecursiveComparison()
                .ignoringFields("id", "booker", "item").isEqualTo(booking);
    }

    @Test
    void getBookingById_shouldReturnBooking() {
        Item item = Item.builder().id(itemId).name(nameItem).description(description).available(available).build();
        Booking booking = Booking.builder().id(id).start(start).end(end).item(item).build();
        when(bookingRepository.findById(id)).thenReturn(Optional.ofNullable(booking));

        Booking actualBooking = bookingService.getBookingById(id);

        assertNotNull(actualBooking.getId());
        assertThat(actualBooking).usingRecursiveComparison()
                .ignoringFields("id", "booker", "item").isEqualTo(booking);
    }

    @Test
    void getAllBookingToUser_shouldReturnListBooking() {
        User user = User.builder().id(userId).name(nameUser).email(email).build();
        Item item = Item.builder().id(itemId).name(nameItem).description(description).available(available).build();
        when(userService.getUserById(userId)).thenReturn(user);
        List<Booking> bookings = List.of(Booking.builder().id(id).start(start).end(end).item(item).build());
        when(bookingRepository.findByBookerIdOrderByStartDesc(userId)).thenReturn(bookings);

        List<Booking> actualBookings = bookingService.getAllBookingToUser(userId, null);

        assertNotNull(actualBookings);
        assertThat(actualBookings).containsExactlyElementsOf(bookings);
    }

    @Test
    void getAllBookingToUser_shouldReturnListBookingToState() {
        User user = User.builder().id(userId).name(nameUser).email(email).build();
        Item item = Item.builder().id(itemId).name(nameItem).description(description).available(available).build();
        when(userService.getUserById(userId)).thenReturn(user);
        List<Booking> bookings = List.of(Booking.builder().id(id).start(start).end(end).item(item).build());
        when(bookingRepository.findByBookerIdAndBookingStatus(userId, BookingStatus.WAITING)).thenReturn(bookings);

        List<Booking> actualBookings = bookingService.getAllBookingToUser(userId, "WAITING");

        assertNotNull(actualBookings);
        assertThat(actualBookings).containsExactlyElementsOf(bookings);
    }

    @Test
    void getBookingAllItemsToUser_shouldReturnListBooking() {
        User user = User.builder().id(userId).name(nameUser).email(email).build();
        Item item = Item.builder().id(itemId).name(nameItem).description(description).available(available).build();
        when(userService.getUserById(userId)).thenReturn(user);
        List<Booking> bookings = List.of(Booking.builder().id(id).start(start).end(end).item(item).build());
        when(bookingRepository.findByItemOwnerIdOrderByStartDesc(userId)).thenReturn(bookings);

        List<Booking> actualBookings = bookingService.getBookingAllItemsToUser(userId, null);

        assertNotNull(actualBookings);
        assertThat(actualBookings).containsExactlyElementsOf(bookings);
    }
}
