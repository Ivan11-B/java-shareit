package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestUpdateDto {

    @Positive
    private Long id;

    private String name;

    private String description;

    private Boolean available;
}