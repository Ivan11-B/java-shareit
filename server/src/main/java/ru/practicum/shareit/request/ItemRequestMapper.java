package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDtoForRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemResponseDto;
import ru.practicum.shareit.user.UserMapper;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ItemRequestMapper {

    private final UserMapper userMapper;


    public ItemRequestDto toDto(ItemRequest itemRequest) {
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .requester(userMapper.toDto(itemRequest.getUser()))
                .build();

    }

    public List<ItemRequestDto> toDto(List<ItemRequest> itemRequests) {
        return itemRequests.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public ItemRequest toEntity(ItemRequestDto itemRequestDto) {
        return ItemRequest.builder()
                .description(itemRequestDto.getDescription())
                .build();
    }

    public ItemResponseDto toDto(ItemRequest itemRequest, List<ItemDtoForRequest> items) {
        return ItemResponseDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .requester(userMapper.toDto(itemRequest.getUser()))
                .items(items)
                .build();
    }

    public List<ItemResponseDto> toDto(List<ItemRequest> itemRequests, List<ItemDtoForRequest> items) {
        return itemRequests.stream()
                .map(itemRequest -> toDto(itemRequest, items))
                .collect(Collectors.toList());
    }
}
