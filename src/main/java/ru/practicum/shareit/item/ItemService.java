package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    Item createItem(Item item, Long userId);

    Item updateItem(Item item, Long userId);

    List<Item> findAll();

    Item getItemById(Long id);

    List<Item> searchByString(String text);

    void deleteItem(Long id);
}
