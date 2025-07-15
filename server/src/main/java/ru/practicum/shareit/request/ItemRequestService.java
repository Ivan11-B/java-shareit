package ru.practicum.shareit.request;


import ru.practicum.shareit.request.dto.ItemResponseDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequest addItemRequest(ItemRequest itemRequest, Long userId);

    List<ItemResponseDto> getAllRequests(Long userId);

    List<ItemRequest> getAllRequestsAnotherUsers(Long userId);

    ItemResponseDto getItemRequestById(Long userId, Long requestId);
}
