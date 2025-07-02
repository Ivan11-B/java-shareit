package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class BookingRequestDto {

    @NotNull
    @FutureOrPresent(message = "Дата начала должна быть в будущем или настоящем")
    private LocalDateTime start;

    @NotNull
    @Future(message = "Дата окончания должна быть в будущем")
    private LocalDateTime end;

    @NotNull
    @Positive(message = "ID должен быть положительный")
    private Long itemId;

    @AssertTrue(message = "Дата окончания должна быть после даты начала")
    private boolean isEndAfterStart() {
        return end == null || start == null || end.isAfter(start);
    }
}