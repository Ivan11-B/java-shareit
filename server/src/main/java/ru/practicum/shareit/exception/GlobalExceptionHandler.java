package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> handleEntityNotFound(NotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(DuplicateException.class)
    public ResponseEntity<ErrorDto> handleDuplicateEmail(DuplicateException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorDto("Дубликат", "Email уже занят"));
    }

    @ExceptionHandler(ItemOwnershipException.class)
    public ResponseEntity<ErrorDto> handleItemOwnership(ItemOwnershipException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorDto("Ошибка запроса", ex.getMessage()));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> handleItemStatus(IllegalStateException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<String> handleMissingHeader(MissingRequestHeaderException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Не заполнен заголовок " + ex.getHeaderName());
    }
}