package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    Item createItem(Item item, Long userId);

    Item updateItem(Item item);

    List<Item> findAll();

    Item getItemById(Long id);

    void deleteItem(Long id);
}
