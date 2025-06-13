package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDto {

    @Positive(message = "ID должен быть положительный")
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    @Email(message = "Некорректный email")
    private String email;
}