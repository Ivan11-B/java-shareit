package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class ItemRepositoryImpl implements ItemRepository {
    private Long id = 1L;
    private final Map<Long, Item> items = new HashMap<>();

    @Override
    public Item addItem(Item item) {
        Long itemId = nextId();
        item.setId(itemId);
        items.put(itemId, item);
        return item;
    }

    @Override
    public Item updateItem(Item item) {
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public List<Item> getAll() {
        return items.values().stream().collect(Collectors.toList());
    }

    @Override
    public Optional<Item> getItemById(Long id) {
        Item currentItem = items.get(id);
        if (currentItem == null) {
            return Optional.empty();
        } else {
            return Optional.of(currentItem);
        }
    }

    @Override
    public List<Item> getItemsToSearch(String text) {
        String searchText = text.toLowerCase();
        List<Item> itemsToSearch = items.values().stream()
                .filter(item -> item.getAvailable().equals(true))
                .filter(item -> item.getName().toLowerCase().contains(searchText) ||
                        item.getDescription().toLowerCase().contains(searchText))
                .distinct()
                .collect(Collectors.toList());
        return itemsToSearch;
    }

    @Override
    public void deleteItem(Long id) {
        items.remove(id);
    }

    private Long nextId() {
        return id++;
    }
}