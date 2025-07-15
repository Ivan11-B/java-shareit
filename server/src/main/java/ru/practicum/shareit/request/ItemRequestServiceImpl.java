package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDtoForRequest;
import ru.practicum.shareit.request.dto.ItemResponseDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;

    private final UserService userService;

    private final ItemRequestMapper itemRequestMapper;

    private final ItemService itemService;

    private final ItemMapper itemMapper;

    @Override
    @Transactional
    public ItemRequest addItemRequest(ItemRequest itemRequest, Long userId) {
        User user = userService.getUserById(userId);
        itemRequest.setUser(user);
        return itemRequestRepository.save(itemRequest);
    }

    @Override
    public List<ItemResponseDto> getAllRequests(Long userId) {
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByUserIdOrderByCreatedDesc(userId);
        List<Long> requestIds = itemRequests.stream()
                .map(ItemRequest::getId)
                .collect(Collectors.toList());
        List<ItemDtoForRequest> items = itemMapper.itemDtoForRequest(itemService.getItemsByListRequest(requestIds));
        List<ItemResponseDto> itemResponseDtos = itemRequestMapper.toDto(itemRequests, items);
        return itemResponseDtos;
    }

    @Override
    public List<ItemRequest> getAllRequestsAnotherUsers(Long userId) {
        User user = userService.getUserById(userId);
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByUserNotOrderByCreatedDesc(user);
        return itemRequests;
    }

    @Override
    public ItemResponseDto getItemRequestById(Long userId, Long requestId) {
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос ID= " + requestId + " не найден!"));
        List<ItemDtoForRequest> items = itemMapper.itemDtoForRequest(itemService.getItemsByRequest(requestId));
        ItemResponseDto itemResponseDto = itemRequestMapper.toDto(itemRequest, items);
        return itemResponseDto;
    }
}
