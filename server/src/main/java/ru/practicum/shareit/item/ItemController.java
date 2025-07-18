package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoUpdate;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/items")
@AllArgsConstructor
@Slf4j
public class ItemController {
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;
    private final ItemService itemService;

    @PostMapping
    public ResponseEntity<ItemDto> createItem(@RequestBody ItemDto itemDto,
                                              @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("Создание вещи: {}, пользователем: {}", itemDto, userId);
        Item item = itemMapper.toEntity(itemDto);
        ItemDto createdItem = itemMapper.toDto(itemService.createItem(item, userId));
        log.info("Создана вещь ID = " + createdItem.getId());
        return ResponseEntity.created(URI.create("/items/" + createdItem.getId())).body(createdItem);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ItemDto> updateItem(@RequestBody ItemDtoUpdate itemDtoUpdate,
                                              @PathVariable Long id,
                                              @RequestHeader("X-Sharer-User-Id") Long userId) {
        itemDtoUpdate.setId(id);
        Item item = itemMapper.toEntity(itemDtoUpdate);
        log.debug("Обновление вещи: {}", item);
        ItemDto updatedItem = itemMapper.toDto(itemService.updateItem(item, userId));
        log.info("Вещь ID = " + updatedItem.getId() + " обновлена");
        return ResponseEntity.ok(updatedItem);
    }

    @GetMapping
    public ResponseEntity<List<ItemWithBookingsDto>> getAll(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("Получение списка вещей пользователя ID= {}", userId);
        List<ItemWithBookingsDto> items = itemService.findAll(userId);
        log.info("Список вещей ID= {} получен", userId);
        return ResponseEntity.ok(items);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemWithBookingsDto> getItemById(@PathVariable Long id) {
        log.debug("Получение вещи по ID: {}", id);
        ItemWithBookingsDto itemDto = itemService.getItemByIdWithBooking(id);
        log.info("Вещь ID = " + itemDto.getId() + " получен");
        return ResponseEntity.ok(itemDto);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> searchByString(@RequestParam String text) {
        log.debug("Получение вещи по ключевому слову: {}", text);
        List<ItemDto> items = itemMapper.toDto(itemService.searchByString(text));
        log.info("Список вещей по ключевому слову: {} получен", text);
        return ResponseEntity.ok(items);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CommentDto> createComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                    @RequestBody CommentDto commentDto,
                                                    @PathVariable Long itemId) {
        log.debug("Создание комментария: {}, для вещи: {}, пользователем: {}", commentDto, itemId, userId);
        Comment comment = commentMapper.toEntity(commentDto);
        CommentDto createdComment = commentMapper.toDto(itemService.addComment(comment, userId, itemId));
        log.info("Создан комментарий ID = " + createdComment.getId());
        return ResponseEntity.ok(createdComment);
    }
}