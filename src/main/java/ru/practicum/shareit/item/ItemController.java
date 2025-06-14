package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoUpdate;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.stream.Collectors;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@AllArgsConstructor
@Slf4j
public class ItemController {
    private final ItemMapper itemMapper;
    private final ItemService itemService;

    @PostMapping
    public ResponseEntity<ItemDto> createItem(@Valid @RequestBody ItemDto itemDto,
                                              @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("Создание вещи: {}", itemDto);
        Item item = itemMapper.toEntity(itemDto);
        ItemDto createdItem = itemMapper.toDto(itemService.createItem(item, userId));
        log.info("Создана вещь ID = " + createdItem.getId());
        return ResponseEntity.ok(createdItem);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ItemDto> updateItem(@Valid @RequestBody ItemDtoUpdate itemDtoUpdate,
                                              @PathVariable @Min(value = 1, message = "ID должен быть ≥ 1") Long id,
                                              @RequestHeader("X-Sharer-User-Id") Long userId) {
        itemDtoUpdate.setId(id);
        Item item = itemMapper.toEntityUpdate(itemDtoUpdate);
        log.debug("Обновление вещи: {}", item);
        ItemDto updatedItem = itemMapper.toDto(itemService.updateItem(item, userId));
        log.info("Вещь ID = " + updatedItem.getId() + " обновлена");
        return ResponseEntity.ok(updatedItem);
    }

    @GetMapping
    public ResponseEntity<List<ItemDto>> findAll(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("Получение списка вещей пользователя ID= {}", userId);
        List<ItemDto> items = itemService.findAll().stream()
                .filter(item -> item.getUser().getId().equals(userId))
                .map(itemMapper::toDto)
                .collect(Collectors.toList());
        log.info("Список вещей ID= {} получен", userId);
        return ResponseEntity.ok(items);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemDto> getItemById(@PathVariable @Min(value = 1, message = "ID должен быть ≥ 1") Long id) {
        log.debug("Получение вещи по ID: {}", id);
        Item item = itemService.getItemById(id);
        ItemDto itemDto = itemMapper.toDto(item);
        log.info("Вещь ID = " + itemDto.getId() + " получен");
        return ResponseEntity.ok(itemDto);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> searchByString(@RequestParam String text) {
        log.debug("Получение вещи по ключевому слову: {}", text);
        List<ItemDto> items = itemService.searchByString(text).stream()
                .map(itemMapper::toDto)
                .collect(Collectors.toList());
        log.info("Список вещей по ключевому слову: {} получен", text);
        return ResponseEntity.ok(items);
    }
}
