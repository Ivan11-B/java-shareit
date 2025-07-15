package ru.practicum.shareit.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.CommentRepository;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemServiceImpl;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static junit.framework.Assert.assertNotNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserService userService;
    @Mock
    private ItemMapper itemMapper;

    @InjectMocks
    private ItemServiceImpl itemService;

    Long userId = 1L;
    String nameUser = "Tom";
    String email = "Tom@mail.ru";

    Long id = 1L;
    String name = "Phone";
    String description = "Call";
    boolean available = true;
    LocalDateTime start = LocalDateTime.now();
    LocalDateTime end = LocalDateTime.now().plus(1, ChronoUnit.MINUTES);
    String text = "Text";

    @Test
    void createItem_shouldReturnItem() {
        User user = User.builder().id(userId).name(nameUser).email(email).build();
        when(userService.getUserById(userId)).thenReturn(user);
        Item item = Item.builder().name(name).description(description).available(available).build();
        when(itemRepository.save(item))
                .thenReturn(Item.builder().id(id).name(name).description(description).available(available).owner(user).build());

        Item createdItem = itemService.createItem(item, userId);

        assertNotNull(createdItem.getId());
        assertThat(createdItem).usingRecursiveComparison().ignoringFields("id").isEqualTo(item);
    }

    @Test
    void updateItem_shouldReturnUpdateItem() {
        User user = User.builder().id(userId).name(nameUser).email(email).build();
        Item item = Item.builder().id(id).id(id).name(name).description(description).available(available).owner(user).build();
        when(itemRepository.findById(id)).thenReturn(Optional.ofNullable(item));
        when(itemRepository.save(item))
                .thenReturn(Item.builder().id(id).name(name).description(description).available(available).owner(user).build());

        Item updatedItem = itemService.updateItem(item, userId);

        assertNotNull(updatedItem.getId());
        assertThat(updatedItem).usingRecursiveComparison().isEqualTo(item);
    }

    @Test
    void findAll_shouldReturnListItems() {
        User user = User.builder().id(userId).name(nameUser).email(email).build();
        List<Item> items = List.of(Item.builder().id(id).id(id).name(name)
                .description(description).available(available).owner(user).build());
        List<Booking> bookings = List.of(Booking.builder().id(id).start(start).end(end).bookingStatus(BookingStatus.APPROVED).booker(user).build());
        List<Comment> comments = List.of(Comment.builder().text(text).build());
        ItemWithBookingsDto item = ItemWithBookingsDto.builder().id(id).name(name).description(description)
                .available(available).build();
        List<ItemWithBookingsDto> itemsTest = List.of(item);
        when(itemRepository.findAllByOwnerId(userId)).thenReturn(items);
        when(bookingRepository.findAllByItemOwnerIdOrderByStartDesc(id)).thenReturn(bookings);
        when(commentRepository.findByItemId(id)).thenReturn(comments);
        when(itemMapper.toDto(any(Item.class), eq(bookings), eq(comments))).thenReturn(item);

        List<ItemWithBookingsDto> actualItems = itemService.findAll(userId);

        assertNotNull(actualItems);
        assertThat(actualItems).containsExactlyElementsOf(itemsTest);
    }

    @Test
    void getItemById_shouldReturnItem() {
        User user = User.builder().id(userId).name(nameUser).email(email).build();
        Item item = Item.builder().id(id).id(id).name(name).description(description).available(available).owner(user).build();
        when(itemRepository.findById(id)).thenReturn(Optional.ofNullable(item));

        Item actualItem = itemService.getItemById(id);

        assertNotNull(actualItem);
        assertThat(actualItem).usingRecursiveComparison().isEqualTo(item);
    }

    @Test
    void getItemByIdWithBooking_shouldReturnItem() {
        User user = User.builder().id(userId).name(nameUser).email(email).build();
        Item item = Item.builder().id(id).id(id).name(name).description(description).available(available).owner(user).build();
        ItemWithBookingsDto itemTest = ItemWithBookingsDto.builder().id(id).name(name).description(description)
                .available(available).build();
        List<Booking> bookings = List.of(Booking.builder().id(id).start(start).end(end).bookingStatus(BookingStatus.APPROVED).booker(user).build());
        List<Comment> comments = List.of(Comment.builder().text(text).build());
        when(itemMapper.toDto(any(Item.class), eq(bookings), eq(comments))).thenReturn(itemTest);
        when(itemRepository.findById(id)).thenReturn(Optional.ofNullable(item));
        when(bookingRepository.findAllByItemOwnerIdOrderByStartDesc(id)).thenReturn(bookings);
        when(commentRepository.findByItemId(id)).thenReturn(comments);

        ItemWithBookingsDto actualItem = itemService.getItemByIdWithBooking(id);

        assertNotNull(actualItem);
        assertThat(actualItem).usingRecursiveComparison().isEqualTo(itemTest);
    }

    @Test
    void searchByString_shouldReturnListItems() {
        User user = User.builder().id(userId).name(nameUser).email(email).build();
        List<Item> items = List.of(Item.builder().id(id).id(id).name(name)
                .description(description).available(available).owner(user).build());
        when(itemRepository.searchInNameOrDescription(text)).thenReturn(items);

        List<Item> actualItems = itemService.searchByString(text);

        assertNotNull(actualItems);
        assertThat(actualItems).containsExactlyElementsOf(items);
    }

    @Test
    void addComment_shouldReturnComment() {
        User user = User.builder().id(userId).name(nameUser).email(email).build();
        when(userService.getUserById(userId)).thenReturn(user);
        Item item = Item.builder().id(id).id(id).name(name).description(description).available(available).owner(user).build();
        Comment comment = Comment.builder().text(text).build();
        Comment createdComment = Comment.builder().id(id).text(text).build();
        when(itemRepository.findById(id)).thenReturn(Optional.ofNullable(item));
        when(bookingRepository.existsByBookerIdAndItemIdAndBookingStatusAndEndBefore(
                eq(1L),
                eq(1L),
                eq(BookingStatus.APPROVED),
                any(LocalDateTime.class)))
                .thenReturn(true);
        when(commentRepository.save(comment)).thenReturn(createdComment);

        Comment actualComment = itemService.addComment(comment, userId, id);

        assertNotNull(actualComment.getId());
        assertThat(actualComment).usingRecursiveComparison().ignoringFields("id", "item", "author").isEqualTo(comment);
    }

    @Test
    void getItemsByListRequest_shouldReturnListItems() {
        List<Long> requests = List.of(1L, 2L);
        List<Item> items = List.of(Item.builder().id(id).name(name)
                .description(description).available(available).build());
        when(itemRepository.findAllByRequestIdIn(requests)).thenReturn(items);

        List<Item> actualItems = itemService.getItemsByListRequest(requests);

        assertNotNull(actualItems);
        assertThat(actualItems).containsExactlyElementsOf(items);
    }

    @Test
    void getItemsByRequest_shouldReturnListItems() {
        Long request = 1L;
        List<Item> items = List.of(Item.builder().id(id).name(name)
                .description(description).available(available).build());
        when(itemRepository.findAllByRequestId(request)).thenReturn(items);

        List<Item> actualItems = itemService.getItemsByRequest(request);

        assertNotNull(actualItems);
        assertThat(actualItems).containsExactlyElementsOf(items);
    }
}
