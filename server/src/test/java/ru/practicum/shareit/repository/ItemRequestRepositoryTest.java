package ru.practicum.shareit.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class ItemRequestRepositoryTest {

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    private User requester1;
    private User requester2;
    private ItemRequest request1;
    private ItemRequest request2;
    private ItemRequest request3;

    Long id = 1L;
    String name = "Tom";
    String email = "Tom@mail.ru";

    String email2 = "Tom2@mail.ru";
    String description = "description";

    LocalDateTime created1 = LocalDateTime.now().minusDays(2);
    LocalDateTime created2 = LocalDateTime.now().minusDays(1);
    LocalDateTime created3 = LocalDateTime.now();

    boolean available = true;

    @BeforeEach
    void setUp() {
        requester1 = userRepository.save(User.builder().name(name).email(email).build());
        requester2 = userRepository.save(User.builder().name(name).email(email2).build());

        request1 = ItemRequest.builder().description(description).created(created1).user(requester1).build();
        itemRequestRepository.save(request1);

        request2 = ItemRequest.builder().description(description).created(created2).user(requester2).build();
        itemRequestRepository.save(request2);

        request3 = ItemRequest.builder().description(description).created(created3).user(requester1).build();
        itemRequestRepository.save(request3);

        Item item1 = Item.builder().name(name).description(description)
                .available(available).owner(requester2).requestId(request1.getId()).build();
        itemRepository.save(item1);

        Item item2 = Item.builder().name(name).description(description)
                .available(available).owner(requester2).requestId(request2.getId()).build();
        itemRepository.save(item2);
    }

    @Test
    void findAllByUserIdOrderByCreatedDesc_shouldReturnRequestsForUser() {
        List<ItemRequest> result = itemRequestRepository.findAllByUserIdOrderByCreatedDesc(requester1.getId());

        assertEquals(2, result.size());
        assertEquals(description, result.get(0).getDescription());
        assertEquals(description, result.get(1).getDescription());
    }

    @Test
    void findAllByUserNotOrderByCreatedDesc_shouldReturnListOtherUsers() {
        List<ItemRequest> result = itemRequestRepository.findAllByUserNotOrderByCreatedDesc(requester1);

        assertEquals(1, result.size());
    }
}