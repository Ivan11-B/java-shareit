package ru.practicum.shareit.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.dto.ItemDtoForRequest;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemResponseDto;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;

@ExtendWith(MockitoExtension.class)
public class ItemRequestMapperTest {
    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private ItemRequestMapper itemRequestMapper;

    Long userId = 1L;
    String nameUser = "Tom";
    String email = "Tom@mail.ru";

    Long id = 1L;
    String name = "Phone";
    String description = "Call";
    LocalDateTime created = LocalDateTime.now();

    @Test
    void toDto() {
        User user = User.builder().id(userId).name(nameUser).email(email).build();
        ItemRequest request = ItemRequest.builder().description(description).created(created).user(user).build();
        List<ItemDtoForRequest> items = List.of(ItemDtoForRequest.builder().name(name).userId(userId).id(id).build());
        ItemResponseDto result = itemRequestMapper.toDto(request, items);

        assertNotNull(result);
    }
}
