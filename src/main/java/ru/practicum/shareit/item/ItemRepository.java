package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {

    Item addItem(Item item);

    Item updateItem(Item item);

    List<Item> getAll();

    Optional<Item> getItemById(Long id);

    List<Item> getItemsToSearch(String text);

    void deleteItem(Long id);
}
