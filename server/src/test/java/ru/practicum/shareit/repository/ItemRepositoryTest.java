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
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class ItemRepositoryTest {
    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    private User owner;
    private User requester;
    private ItemRequest request;
    private Item item1;
    private Item item2;

    Long id = 1L;
    String name = "Tom";
    String email = "Tom@mail.ru";

    String email2 = "Tom2@mail.ru";
    String description = "description";

    LocalDateTime created = LocalDateTime.now();
    boolean available = true;

    @BeforeEach
    void setUp() {
        owner = userRepository.save(User.builder().name(name).email(email).build());
        requester = userRepository.save(User.builder().name(name).email(email2).build());

        request = ItemRequest.builder().description(description).created(created).user(requester).build();
        request = itemRequestRepository.save(request);

        item1 = Item.builder().name(name).description(description)
                .available(available).owner(owner).requestId(request.getId()).build();
        itemRepository.save(item1);

        item2 = Item.builder().name(name).description(description)
                .available(available).owner(owner).build();
        itemRepository.save(item2);
    }

    @Test
    void findAllByOwnerId_shouldReturnOwnerItems() {
        List<Item> result = itemRepository.findAllByOwnerId(owner.getId());

        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(i -> i.getName().equals(name)));
        assertTrue(result.stream().anyMatch(i -> i.getDescription().equals(description)));
    }

    @Test
    void search_shouldFindAvailableItemsByText() {
        List<Item> result = itemRepository.searchInNameOrDescription(description);

        assertEquals(2, result.size());
        assertEquals(name, result.get(0).getName());
    }


    @Test
    void findAllByRequestIdIn_shouldReturnItemsForRequest() {
        List<Item> result = itemRepository.findAllByRequestIdIn(List.of(request.getId()));

        assertEquals(1, result.size());
        assertEquals(name, result.get(0).getName());
    }

    @Test
    void findAllByRequestId_shouldReturnItemsForRequest() {
        List<Item> result = itemRepository.findAllByRequestId(request.getId());

        assertEquals(1, result.size());
    }
}