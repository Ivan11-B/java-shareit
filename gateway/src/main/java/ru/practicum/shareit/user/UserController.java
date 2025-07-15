package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserRequestUpdateDto;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {

    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> createUser(@Valid @RequestBody UserRequestDto userRequestDto) {
        log.info("Создание пользователя: name= {}, email= {}", userRequestDto.getName(), userRequestDto.getEmail());
        return userClient.createUser(userRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getUsers() {
        log.info("Получение списка пользователей");
        return userClient.getUsers();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateUser(@Valid @RequestBody UserRequestUpdateDto userDtoUpdate,
                                             @PathVariable @Min(value = 1, message = "ID должен быть ≥ 1") Long id) {

        log.info("Обновление пользователя: ID= {}, name= {}, email= {}",
                userDtoUpdate.getId(), userDtoUpdate.getName(), userDtoUpdate.getEmail());
        return userClient.updateUser(userDtoUpdate, id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUser(
            @PathVariable @Min(value = 1, message = "ID должен быть ≥ 1") Long id) {
        log.info("Получение пользователя по ID: {}", id);
        return userClient.getUser(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteUser(
            @PathVariable @Min(value = 1, message = "ID должен быть ≥ 1") Long id) {
        log.info("Удаление пользователя ID: {}", id);
        return userClient.deleteUser(id);
    }
}
