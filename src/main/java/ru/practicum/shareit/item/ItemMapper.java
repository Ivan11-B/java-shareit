package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoForRequest;
import ru.practicum.shareit.item.dto.ItemDtoUpdate;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class ItemMapper {

    private final UserMapper userMapper;
    private final CommentMapper commentMapper;

    public ItemDto toDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .owner(userMapper.toDto(item.getOwner()))
                .build();
    }

    public List<ItemDto> toDto(List<Item> items) {
        return items.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public ItemWithBookingsDto toDto(Item item, List<Booking> bookings, List<Comment> comments) {
        ItemWithBookingsDto dto = ItemWithBookingsDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .owner(userMapper.toDto(item.getOwner()))
                .build();
        Optional<Booking> lastBooking = bookings.stream()
                .filter(booking -> booking.getEnd().isBefore(LocalDateTime.now()))
                .max(Comparator.comparing(Booking::getEnd));
        Optional<Booking> nextBooking = bookings.stream()
                .filter(booking -> booking.getStart().isAfter(LocalDateTime.now()))
                .min(Comparator.comparing(Booking::getStart));
        lastBooking.ifPresent(b -> dto.setLastBooking(b.getEnd()));
        nextBooking.ifPresent(booking -> dto.setNextBooking(booking.getStart()));
        dto.setComments(commentMapper.toDto(comments));
        return dto;
    }

    public Item toEntity(ItemDto itemDto) {
        return Item.builder()
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .requestId(itemDto.getRequestId())
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

    public ItemDtoForRequest itemDtoForRequest(Item item) {
        return ItemDtoForRequest.builder()
                .id(item.getId())
                .name(item.getName())
                .userId(item.getOwner().getId())
                .build();
    }

    public List<ItemDtoForRequest> itemDtoForRequest(List<Item> items) {
        return items.stream()
                .map(this::itemDtoForRequest)
                .collect(Collectors.toList());
    }
}