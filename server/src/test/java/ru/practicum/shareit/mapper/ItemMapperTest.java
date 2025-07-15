package ru.practicum.shareit.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.CommentMapper;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDtoForRequest;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;

@ExtendWith(MockitoExtension.class)
public class ItemMapperTest {
    @Mock
    private UserMapper userMapper;
    @Mock
    private CommentMapper commentMapper;
    @InjectMocks
    private ItemMapper itemMapper;


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
    void toDto() {
        User user = User.builder().id(userId).name(nameUser).email(email).build();
        Item item = Item.builder().name(name).description(description).available(available).owner(user).build();
        List<Booking> bookings = List.of(Booking.builder().id(id).start(start).end(end).item(item).build());
        List<Comment> comments = List.of(Comment.builder().text(text).build());

        ItemWithBookingsDto itemWithBookingsDto = itemMapper.toDto(item, bookings, comments);

        assertNotNull(itemWithBookingsDto);
    }

    @Test
    void itemDtoForRequest() {
        User user = User.builder().id(userId).name(nameUser).email(email).build();
        List<Item> items = List.of(Item.builder().id(id).id(id).name(name)
                .description(description).available(available).owner(user).build());
        List<ItemDtoForRequest> result = itemMapper.itemDtoForRequest(items);

        assertNotNull(result);
    }
}
