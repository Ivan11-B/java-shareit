package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.model.User;

@Data
@Builder
public class ItemDtoUpdate {
    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private User user;
}