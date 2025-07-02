package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;

import java.util.List;

public interface ItemService {

    Item createItem(Item item, Long userId);

    Item updateItem(Item item, Long userId);

    List<ItemWithBookingsDto> findAll(Long userId);

    Item getItemById(Long id);

    ItemWithBookingsDto getItemByIdWithBooking(Long id);

    List<Item> searchByString(String text);

    Comment addComment(Comment comment, Long userId, Long itemId);
}
