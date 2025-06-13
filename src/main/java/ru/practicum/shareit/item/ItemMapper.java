package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoUpdate;
import ru.practicum.shareit.item.model.Item;

@Component
public class ItemMapper {

    public ItemDto toDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .user(item.getUser())
                .build();
    }

    public Item toEntity(ItemDto itemDto) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .build();
    }

    public Item toEntityUpdate(ItemDtoUpdate itemDtoUpdate) {
        return Item.builder()
                .id(itemDtoUpdate.getId())
                .name(itemDtoUpdate.getName())
                .description(itemDtoUpdate.getDescription())
                .build();
    }
}
