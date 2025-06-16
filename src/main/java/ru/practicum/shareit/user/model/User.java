package ru.practicum.shareit.user.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class User {
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    @Email(message = "Некорректный email")
    private String email;
}