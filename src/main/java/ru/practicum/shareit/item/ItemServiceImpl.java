package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserService userService;

    @Override
    public Item createItem(Item item, Long userId) {
        User user = userService.getUserById(userId);
        item.setUser(user);
        return itemRepository.addItem(item);
    }

    @Override
    public Item updateItem(Item item, Long userId) {
        Item currentItem = getItemById(item.getId());
        Item updatedItem = new Item(currentItem.getId(), currentItem.getName(), currentItem.getDescription(),
                currentItem.getAvailable(), currentItem.getUser());
        if (item.getName() != null) {
            updatedItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            updatedItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            updatedItem.setAvailable(item.getAvailable());
        }
        User user = userService.getUserById(userId);
        if (user != null) {
            updatedItem.setUser(user);
        }
        return itemRepository.updateItem(updatedItem);
    }

    @Override
    public List<Item> findAll() {
        return itemRepository.getAll();
    }

    @Override
    public Item getItemById(Long id) {
        return itemRepository.getItemById(id)
                .orElseThrow(() -> new NotFoundException("Вещь ID= " + id + " не найдена!"));
    }

    @Override
    public List<Item> searchByString(String text) {
        if (text == null || text.isEmpty()) {
            return List.of();
        }
        return itemRepository.getItemsToSearch(text);
    }

    @Override
    public void deleteItem(Long id) {
        getItemById(id);
        itemRepository.deleteItem(id);
    }
}