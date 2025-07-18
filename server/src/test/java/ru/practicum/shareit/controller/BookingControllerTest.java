package ru.practicum.shareit.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.practicum.shareit.booking.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {BookingController.class, BookingMapper.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingControllerTest {

    @MockBean
    BookingService bookingService;
    @MockBean
    UserMapper userMapper;
    @MockBean
    ItemMapper itemMapper;
    private final ObjectMapper mapper;
    private final MockMvc mvc;

    Long id = 1L;
    Long itemId;
    LocalDateTime start = LocalDateTime.now();
    LocalDateTime end = LocalDateTime.now().plus(1, ChronoUnit.MINUTES);


    String nameItem = "Phone";
    String description = "Call";
    boolean available = true;

    Long userId = 1L;
    String nameUser = "Tom";
    String emailUser = "Tom@mail.ru";

    @Test
    void create_shouldReturnBooking() throws Exception {
        User user = User.builder().id(userId).name(nameUser).email(emailUser).build();
        UserDto userDto = UserDto.builder().id(userId).name(nameUser).email(emailUser).build();
        Item item = Item.builder().id(itemId).name(nameItem).description(description).available(available).build();
        BookingRequestDto bookingRequestDto = BookingRequestDto.builder().itemId(itemId).start(start).end(end).build();
        Booking booking = Booking.builder().start(start).end(end).build();
        Booking createdBooking = Booking.builder().id(id).start(start).end(end).bookingStatus(BookingStatus.WAITING)
                .item(item).booker(user).build();
        when(userMapper.toDto(user)).thenReturn(userDto);
        when(bookingService.createBooking(booking, userId)).thenReturn(createdBooking);

        MvcResult mvcResult = mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingRequestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andReturn();
        String responseBody = mvcResult.getResponse().getContentAsString();
        BookingDto actualBookingDto = mapper.readValue(responseBody, BookingDto.class);

        assertNotNull(actualBookingDto.getId());
    }

    @Test
    void update_shouldReturnUpdateBooking() throws Exception {
        User user = User.builder().id(userId).name(nameUser).email(emailUser).build();
        UserDto userDto = UserDto.builder().id(userId).name(nameUser).email(emailUser).build();
        Item item = Item.builder().id(itemId).name(nameItem).description(description).available(available).build();
        ItemDto itemDto = ItemDto.builder().id(id).name(nameItem).description(description).available(available).build();
        String updateStatus = "APPROVED";
        Booking booking = Booking.builder().id(id).start(start).end(end).bookingStatus(BookingStatus.APPROVED).booker(user).build();
        BookingDto bookingDto = BookingDto.builder().id(id).start(start).end(end).status(BookingStatus.APPROVED).build();
        when(userMapper.toDto(user)).thenReturn(userDto);
        when(itemMapper.toDto(item)).thenReturn(itemDto);
        when(bookingService.confirmBooking(id, updateStatus, userId)).thenReturn(booking);

        MvcResult mvcResult = mvc.perform(patch("/bookings/{bookingsId}", id)
                        .param("approved", updateStatus)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andReturn();
        String responseBody = mvcResult.getResponse().getContentAsString();
        BookingDto actualBookingDto = mapper.readValue(responseBody, BookingDto.class);

        assertNotNull(actualBookingDto.getId());
        assertThat(actualBookingDto).usingRecursiveComparison().ignoringFields("booker").isEqualTo(bookingDto);
    }

    @Test
    void getBookingById_shouldReturnBooking() throws Exception {
        User user = User.builder().id(userId).name(nameUser).email(emailUser).build();
        UserDto userDto = UserDto.builder().id(userId).name(nameUser).email(emailUser).build();
        Item item = Item.builder().id(itemId).name(nameItem).description(description).available(available).build();
        ItemDto itemDto = ItemDto.builder().id(id).name(nameItem).description(description).available(available).build();
        Booking booking = Booking.builder().id(id).start(start).end(end).bookingStatus(BookingStatus.APPROVED).booker(user).build();
        BookingDto bookingDto = BookingDto.builder().id(id).start(start).end(end).status(BookingStatus.APPROVED).build();
        when(userMapper.toDto(user)).thenReturn(userDto);
        when(itemMapper.toDto(item)).thenReturn(itemDto);
        when(bookingService.getBookingById(id)).thenReturn(booking);

        MvcResult mvcResult = mvc.perform(get("/bookings/{bookingsId}", id)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andReturn();
        String responseBody = mvcResult.getResponse().getContentAsString();
        BookingDto actualBookingDto = mapper.readValue(responseBody, BookingDto.class);

        assertNotNull(actualBookingDto);
        assertThat(actualBookingDto).usingRecursiveComparison().ignoringFields("booker").isEqualTo(bookingDto);
    }

    @Test
    void findAllBookingToUser_shouldReturnListBooking() throws Exception {
        String state = "All";
        User user = User.builder().id(userId).name(nameUser).email(emailUser).build();
        UserDto userDto = UserDto.builder().id(userId).name(nameUser).email(emailUser).build();
        Item item = Item.builder().id(itemId).name(nameItem).description(description).available(available).build();
        ItemDto itemDto = ItemDto.builder().id(id).name(nameItem).description(description).available(available).build();
        List<Booking> bookings = List.of(Booking.builder().id(id).start(start).end(end).bookingStatus(BookingStatus.APPROVED).booker(user).build());
        when(userMapper.toDto(user)).thenReturn(userDto);
        when(itemMapper.toDto(item)).thenReturn(itemDto);
        when(bookingService.getAllBookingToUser(id, state)).thenReturn(bookings);

        MvcResult mvcResult = mvc.perform(get("/bookings")
                        .param("state", state)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andReturn();
        String responseBody = mvcResult.getResponse().getContentAsString();
        List<BookingDto> actualBookingDtos = mapper.readValue(responseBody, new TypeReference<List<BookingDto>>() {
        });

        assertNotNull(actualBookingDtos);
        assertEquals(1, actualBookingDtos.size());
    }

    @Test
    void findBookingToUserAllItems_shouldReturnListBooking() throws Exception {
        String state = "All";
        User user = User.builder().id(userId).name(nameUser).email(emailUser).build();
        UserDto userDto = UserDto.builder().id(userId).name(nameUser).email(emailUser).build();
        Item item = Item.builder().id(itemId).name(nameItem).description(description).available(available).build();
        ItemDto itemDto = ItemDto.builder().id(id).name(nameItem).description(description).available(available).build();
        List<Booking> bookings = List.of(Booking.builder().id(id).start(start).end(end).bookingStatus(BookingStatus.APPROVED).booker(user).build());
        when(userMapper.toDto(user)).thenReturn(userDto);
        when(itemMapper.toDto(item)).thenReturn(itemDto);
        when(bookingService.getBookingAllItemsToUser(id, state)).thenReturn(bookings);

        MvcResult mvcResult = mvc.perform(get("/bookings/owner")
                        .param("state", state)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andReturn();
        String responseBody = mvcResult.getResponse().getContentAsString();
        List<BookingDto> actualBookingDtos = mapper.readValue(responseBody, new TypeReference<List<BookingDto>>() {
        });

        assertNotNull(actualBookingDtos);
        assertEquals(1, actualBookingDtos.size());
    }
}