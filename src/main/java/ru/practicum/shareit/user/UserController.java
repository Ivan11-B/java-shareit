package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoUpdate;
import ru.practicum.shareit.user.model.User;

import java.net.URI;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@AllArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping
    public ResponseEntity<List<UserDto>> findAll() {
        log.debug("Получение списка пользователей");
        List<UserDto> users = userMapper.toDto(userService.getAll());
        log.info("Список пользователей получен");
        return ResponseEntity.ok(users);
    }

    @PostMapping
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserDto userDto) {
        log.debug("Создание пользователя: {}", userDto);
        User user = userMapper.toEntity(userDto);
        UserDto createdUser = userMapper.toDto(userService.create(user));
        log.info("Создан пользователь ID = " + createdUser.getId());
        return ResponseEntity.created(URI.create("/users/" + createdUser.getId())).body(createdUser);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(@Valid @RequestBody UserDtoUpdate userDtoUpdate,
                                              @PathVariable @Min(value = 1, message = "ID должен быть ≥ 1") Long id) {
        userDtoUpdate.setId(id);
        User user = userMapper.toEntity(userDtoUpdate);
        user.setId(id);
        log.debug("Обновление пользователя: {}", user);
        UserDto updatedUser = userMapper.toDto(userService.update(user));
        log.info("Пользователь ID = " + updatedUser.getId() + " обновлен");
        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(
            @PathVariable @Min(value = 1, message = "ID должен быть ≥ 1") Long id) {
        log.debug("Получение пользователя по ID: {}", id);
        User user = userService.getUserById(id);
        UserDto userDto = userMapper.toDto(user);
        log.info("Пользователь ID = " + userDto.getId() + " получен");
        return ResponseEntity.ok(userDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @PathVariable @Min(value = 1, message = "ID должен быть ≥ 1") Long id) {
        log.debug("Удаление пользователя ID: {}", id);
        userService.delete(id);
        log.info("Пользователь ID = " + id + " удален");
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}