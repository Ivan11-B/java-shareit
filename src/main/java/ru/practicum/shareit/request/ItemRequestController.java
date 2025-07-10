package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemResponseDto;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@Slf4j
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestMapper itemRequestMapper;
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ResponseEntity<ItemRequestDto> addItemRequest(@Valid @RequestBody ItemRequestDto itemRequestDto,
                                                         @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("Создание запроса вещи: {}", itemRequestDto);
        ItemRequest itemRequest = itemRequestMapper.toEntity(itemRequestDto);
        ItemRequestDto createdItemRequest = itemRequestMapper.toDto(itemRequestService.addItemRequest(itemRequest, userId));
        log.info("Создан запрос вещи ID = " + createdItemRequest.getId());
        return ResponseEntity.created(URI.create("/requests/" + createdItemRequest.getId())).body(createdItemRequest);
    }

    @GetMapping
    public ResponseEntity<List<ItemResponseDto>> getAllRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("Получение списка своих запросов пользователя ID= {}", userId);
        List<ItemResponseDto> itemResponseDto = itemRequestService.getAllRequests(userId);
        log.info("Список своих запросов пользователя ID= {} получен", userId);
        return ResponseEntity.ok(itemResponseDto);
    }

    @GetMapping("/all")
    public ResponseEntity<List<ItemRequestDto>> getAllRequestAnotherUser(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("Получение списка запросов других пользователей");
        List<ItemRequestDto> itemRequestDto = itemRequestMapper.toDto(itemRequestService.getAllRequestsAnotherUsers(userId));
        log.info("Список запросов других пользователей получен");
        return ResponseEntity.ok(itemRequestDto);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<ItemResponseDto> getItemRequestById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                              @PathVariable @Min(value = 1, message = "ID должен быть ≥ 1") Long requestId) {
        log.debug("Получение запроса по ID: {}", requestId);
        ItemResponseDto itemResponseDto = itemRequestService.getItemRequestById(userId, requestId);
        log.info("Запрос ID = " + itemResponseDto.getId() + " получен");
        return ResponseEntity.ok(itemResponseDto);
    }
}