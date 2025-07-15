package ru.practicum.shareit.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDtoForRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.ItemRequestServiceImpl;
import ru.practicum.shareit.request.dto.ItemResponseDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static junit.framework.Assert.assertNotNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceTest {

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private UserService userService;

    @Mock
    private ItemRequestMapper itemRequestMapper;

    @Mock
    private ItemService itemService;

    @Mock
    private ItemMapper itemMapper;

    @InjectMocks
    private ItemRequestServiceImpl requestService;

    Long id = 1L;
    String description = "description";
    String name = "Phone";
    boolean available = true;

    LocalDateTime created = LocalDateTime.now();
    Long userId = 1L;
    String nameUser = "Tom";
    String email = "Tom@mail.ru";

    @Test
    void addItemRequest_shouldReturnItemRequest() {
        User user = User.builder().id(userId).name(nameUser).email(email).build();
        when(userService.getUserById(userId)).thenReturn(user);
        ItemRequest request = ItemRequest.builder().description(description).created(created).user(user).build();
        ItemRequest createdRequest = ItemRequest.builder().id(id).description(description).created(created).user(user).build();
        when(itemRequestRepository.save(request)).thenReturn(createdRequest);

        ItemRequest actualItemRequest = requestService.addItemRequest(request, userId);

        assertNotNull(actualItemRequest.getId());
        assertThat(actualItemRequest).usingRecursiveComparison().ignoringFields("id").isEqualTo(request);
    }

    @Test
    void getAllRequests_shouldReturnListItemResponseDto() {
        User user = User.builder().id(userId).name(nameUser).email(email).build();
        UserDto userDto = UserDto.builder().id(userId).name(nameUser).email(email).build();
        List<ItemRequest> itemRequests = List.of(ItemRequest.builder().id(id)
                .description(description).created(created).user(user).build());
        when(itemRequestRepository.findAllByUserIdOrderByCreatedDesc(userId)).thenReturn(itemRequests);
        List<Long> requests = List.of(1L);
        List<Item> items = List.of(Item.builder().id(id).name(name)
                .description(description).available(available).build());
        when(itemService.getItemsByListRequest(requests)).thenReturn(items);
        List<ItemDtoForRequest> itemsForRequest = List.of(ItemDtoForRequest.builder()
                .id(id).name(name).userId(userId).build());
        when(itemMapper.itemDtoForRequest(items)).thenReturn(itemsForRequest);
        List<ItemResponseDto> createdList = List.of(ItemResponseDto.builder().id(id)
                .description(description).created(created).requester(userDto).build());
        when(itemRequestMapper.toDto(itemRequests, itemsForRequest)).thenReturn(createdList);

        List<ItemResponseDto> actualList = requestService.getAllRequests(userId);

        assertNotNull(actualList);
        assertThat(actualList).containsExactlyElementsOf(createdList);
    }

    @Test
    void getAllRequestsAnotherUsers_shouldReturnListItemRequest() {
        User user = User.builder().id(userId).name(nameUser).email(email).build();
        when(userService.getUserById(userId)).thenReturn(user);
        List<ItemRequest> itemRequests = List.of(ItemRequest.builder().id(id)
                .description(description).created(created).user(user).build());
        when(itemRequestRepository.findAllByUserNotOrderByCreatedDesc(user)).thenReturn(itemRequests);

        List<ItemRequest> actualList = requestService.getAllRequestsAnotherUsers(userId);

        assertNotNull(actualList);
        assertThat(actualList).containsExactlyElementsOf(itemRequests);
    }

    @Test
    void getItemRequestById_shouldReturnItemResponseDto() {
        Long requestId = 1L;
        User user = User.builder().id(userId).name(nameUser).email(email).build();
        UserDto userDto = UserDto.builder().id(userId).name(nameUser).email(email).build();
        ItemRequest request = ItemRequest.builder().description(description).created(created).user(user).build();
        when(itemRequestRepository.findById(requestId)).thenReturn(Optional.ofNullable(request));
        List<Item> items = List.of(Item.builder().id(id).name(name)
                .description(description).available(available).build());
        when(itemService.getItemsByRequest(requestId)).thenReturn(items);
        List<ItemDtoForRequest> itemsForRequest = List.of(ItemDtoForRequest.builder()
                .id(id).name(name).userId(userId).build());
        when(itemMapper.itemDtoForRequest(items)).thenReturn(itemsForRequest);
        ItemResponseDto itemResponseDto = ItemResponseDto.builder().id(id).description(description)
                .created(created).requester(userDto).items(List.of()).build();
        when(itemRequestMapper.toDto(request, itemsForRequest)).thenReturn(itemResponseDto);

        ItemResponseDto actualItemRequest = requestService.getItemRequestById(userId, id);

        assertNotNull(actualItemRequest);
    }
}