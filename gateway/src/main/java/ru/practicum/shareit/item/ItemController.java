package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemRequestUpdateDto;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(@Valid @RequestBody ItemRequestDto itemDto,
                                             @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Создание вещи: name= {}, description= {}, available= {}, requestId= {}, пользователем: {}",
                itemDto.getName(), itemDto.getDescription(), itemDto.getAvailable(), itemDto.getRequestId(), userId);
        return itemClient.createItem(itemDto, userId);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateItem(@Valid @RequestBody ItemRequestUpdateDto itemRequestUpdateDto,
                                             @PathVariable @Min(value = 1, message = "ID должен быть ≥ 1") Long id,
                                             @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Обновление вещи: ID= {}, name= {}, description= {}, available= {}, пользователем: {}",
                itemRequestUpdateDto.getId(), itemRequestUpdateDto.getName(),
                itemRequestUpdateDto.getDescription(), itemRequestUpdateDto.getAvailable(), userId);
        return itemClient.updateItem(itemRequestUpdateDto, id, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получение списка вещей пользователя ID= {}", userId);
        return itemClient.getItems(userId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getItem(@PathVariable @Min(value = 1, message = "ID должен быть ≥ 1") Long id) {
        log.info("Получение вещи по ID: {}", id);
        return itemClient.getItem(id);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchByString(@RequestParam String text,
                                                 @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получение вещи по ключевому слову: {}", text);
        return itemClient.searchByText(text, userId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                @Valid @RequestBody CommentRequestDto commentDto,
                                                @PathVariable @Min(value = 1, message = "ID должен быть ≥ 1") Long itemId) {
        log.info("Создание комментария: {}, для вещи: {}, пользователем: {}", commentDto.getText(), itemId, userId);
        return itemClient.createComment(commentDto, itemId, userId);
    }
}