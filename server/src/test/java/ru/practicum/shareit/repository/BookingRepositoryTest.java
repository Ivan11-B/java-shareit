package ru.practicum.shareit.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    private User owner;
    private User booker;
    private Item item;
    private Booking pastBooking;
    private Booking currentBooking;
    private Booking futureBooking;
    private LocalDateTime now;

    String name = "Tom";
    String email = "Tom@mail.ru";

    String email2 = "Tom2@mail.ru";
    String description = "description";

    LocalDateTime created = LocalDateTime.now();
    boolean available = true;


    @BeforeEach
    void setUp() {
        bookingRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();

        owner = userRepository.save(User.builder().name(name).email(email).build());
        booker = userRepository.save(User.builder().name(name).email(email2).build());

        item = Item.builder().name(name).description(description)
                .available(available).owner(owner).build();
        item = itemRepository.save(item);

        now = LocalDateTime.now();

        pastBooking = Booking.builder().start(now.minusDays(2)).end(now.minusDays(1)).item(item)
                .bookingStatus(BookingStatus.APPROVED).booker(booker).build();
        bookingRepository.save(pastBooking);

        currentBooking = Booking.builder().start(now.minusHours(1)).end(now.plusHours(1)).item(item)
                .bookingStatus(BookingStatus.APPROVED).booker(booker).build();
        bookingRepository.save(currentBooking);

        futureBooking = Booking.builder().start(now.minusDays(1)).end(now.plusDays(2)).item(item)
                .bookingStatus(BookingStatus.WAITING).booker(booker).build();
        bookingRepository.save(futureBooking);
    }

    @Test
    void findAllByItemOwnerIdOrderByStartDesc_shouldReturnAllBookingsForAllItemOwner() {
        List<Booking> result = bookingRepository.findAllByItemOwnerIdOrderByStartDesc(owner.getId());

        assertEquals(3, result.size());
    }

    @Test
    void findByBookerIdOrderByStartDesc_shouldReturnAllBookingsBooker() {
        List<Booking> result = bookingRepository.findByBookerIdOrderByStartDesc(booker.getId());

        assertEquals(3, result.size());
    }

    @Test
    void findCurrentBookingsByBookerId_shouldReturnCurrentBookingsForBooker() {
        List<Booking> result = bookingRepository.findCurrentBookingsByBookerId(booker.getId(), now);

        assertEquals(2, result.size());
    }
}
