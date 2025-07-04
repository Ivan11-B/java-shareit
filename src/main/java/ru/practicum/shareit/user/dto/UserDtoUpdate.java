package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDtoUpdate {
    private Long id;

    private String name;

    @Email(message = "Некорректный email")
    private String email;
}