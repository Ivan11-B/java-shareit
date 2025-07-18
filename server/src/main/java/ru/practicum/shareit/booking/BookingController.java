package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {

    private final BookingService bookingService;
    private final BookingMapper bookingMapper;

    @PostMapping
    public ResponseEntity<BookingDto> create(@RequestBody BookingRequestDto bookingRequestDto,
                                             @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("Создание запроса бронирования: {}, пользователем: {}", bookingRequestDto, userId);
        Booking booking = bookingMapper.toEntity(bookingRequestDto);
        BookingDto createdBooking = bookingMapper.toDto(bookingService.createBooking(booking, userId));
        log.info("Создан запрос на бронирование ID = " + createdBooking.getId());
        return ResponseEntity.ok(createdBooking);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingDto> update(@PathVariable Long bookingId,
                                             @RequestParam String approved,
                                             @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("Подтверждение запроса бронирования ID: {}, статус: {}, пользователь: {}", bookingId, approved, userId);
        BookingDto bookingDto = bookingMapper.toDto(bookingService.confirmBooking(bookingId, approved, userId));
        log.info("Подтверждение запроса бронирования выполнено ID: {}, статус: {}, пользователь: {}",
                bookingDto.getId(), bookingDto.getStatus(), bookingDto.getBooker().getId());
        return ResponseEntity.ok(bookingDto);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDto> getBookingById(@PathVariable Long bookingId) {
        log.debug("Получение данных о бронировании ID: {}", bookingId);
        BookingDto bookingDto = bookingMapper.toDto(bookingService.getBookingById(bookingId));
        return ResponseEntity.ok(bookingDto);
    }

    @GetMapping
    public ResponseEntity<List<BookingDto>> findAllBookingToUser(@RequestParam(required = false) String state,
                                                                 @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("Получение списка всех бронирований текущего пользователя ID: {}, статус: {}", userId, state);
        List<BookingDto> bookingDto = bookingMapper.toDto(bookingService.getAllBookingToUser(userId, state));
        return ResponseEntity.ok(bookingDto);
    }

    @GetMapping("/owner")
    public ResponseEntity<List<BookingDto>> findBookingToUserAllItems(@RequestParam(required = false) String state,
                                                                      @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("Получение списка бронирований для всех вещей текущего пользователя ID: {}, статус: {}",
                userId, state);
        List<BookingDto> bookingDto = bookingMapper.toDto(bookingService.getBookingAllItemsToUser(userId, state));
        return ResponseEntity.ok(bookingDto);
    }
}