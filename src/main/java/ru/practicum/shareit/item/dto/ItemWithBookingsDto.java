package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemWithBookingsDto {

    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private User owner;
    private LocalDateTime lastBooking;
    private LocalDateTime nextBooking;
    private List<Comment> comments;
}
