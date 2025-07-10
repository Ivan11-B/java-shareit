package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
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

    @NotBlank
    private String description;

    private LocalDateTime created;

    private UserDto requester;

    private List<ItemDtoForRequest> items;
}
