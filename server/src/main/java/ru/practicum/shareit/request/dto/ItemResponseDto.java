package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDtoForRequest;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
public class ItemResponseDto {

    private Long id;

    private String description;

    private LocalDateTime created;

    private UserDto requester;

    private List<ItemDtoForRequest> items;
}
