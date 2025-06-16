package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoUpdate;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.stream.Collectors;

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

    public List<ItemDto> toDto(List<Item> items) {
        return items.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public Item toEntity(ItemDto itemDto) {
        return Item.builder()
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .build();
    }

    public Item toEntity(ItemDtoUpdate itemDtoUpdate) {
        return Item.builder()
                .id(itemDtoUpdate.getId())
                .name(itemDtoUpdate.getName())
                .description(itemDtoUpdate.getDescription())
                .available(itemDtoUpdate.getAvailable())
                .build();
    }
}
