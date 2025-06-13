package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class ItemRepositoryImpl implements ItemRepository {
    private Integer id = 1;
    private final Map<Integer, Item> items = new HashMap<>();

    @Override
    public Item addItem(Item item) {
        Integer itemId = nextId();
        item.setId(Long.valueOf(itemId));
        item.setAvailable(false);
        items.put(itemId, item);
        return item;
    }

    @Override
    public Item updateItem(Item item) {
        return null;
    }

    @Override
    public List<Item> getAll() {
        return null;
    }

    @Override
    public Optional<Item> getItemById(Long id) {
        return Optional.empty();
    }

    @Override
    public void deleteItem(Long id) {

    }

    private Integer nextId() {
        return id++;
    }
}