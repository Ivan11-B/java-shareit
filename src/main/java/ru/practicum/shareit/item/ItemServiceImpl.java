package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService{

    private final ItemRepository itemRepository;
    private final UserService userService;

    @Override
    public Item createItem(Item item, Long userId) {
        User user = userService.getUserById(userId);
        item.setUser(user);
        return itemRepository.addItem(item);
    }

    @Override
    public Item updateItem(Item item) {
        return null;
    }

    @Override
    public List<Item> findAll() {
        return null;
    }

    @Override
    public Item getItemById(Long id) {
        return null;
    }

    @Override
    public void deleteItem(Long id) {

    }
}
