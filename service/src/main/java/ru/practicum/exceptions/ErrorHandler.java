package ru.practicum.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.context.support.DefaultMessageSourceResolvable;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class ErrorHandler {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ApiError> handleNotFound(NotFoundException e) {
        log.error("NOT_FOUND: {}", e.getMessage());
        ApiError error = new ApiError();
        error.setStatus(HttpStatus.NOT_FOUND.name());
        error.setReason("The required object was not found.");
        error.setMessage(e.getMessage());
        error.setTimestamp(LocalDateTime.now().format(FORMATTER));
        error.setErrors(Collections.emptyList());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<ApiError> handleConflict(ConflictException e) {
        log.error("CONFLICT: {}", e.getMessage());
        ApiError error = new ApiError();
        error.setStatus(HttpStatus.CONFLICT.name());
        error.setReason("For the requested operation the conditions are not met.");
        error.setMessage(e.getMessage());
        error.setTimestamp(LocalDateTime.now().format(FORMATTER));
        error.setErrors(Collections.emptyList());
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleOther(Exception e) {
        log.error("INTERNAL_SERVER_ERROR: {}", e.getMessage(), e);
        ApiError error = new ApiError();
        error.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.name());
        error.setReason("Error was not handled.");
        error.setMessage(e.getMessage());
        error.setTimestamp(LocalDateTime.now().format(FORMATTER));
        error.setErrors(Collections.emptyList());
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiError> handleBadRequest(BadRequestException e) {
        log.error("BAD_REQUEST: {}", e.getMessage());
        ApiError error = new ApiError();
        error.setStatus(HttpStatus.BAD_REQUEST.name());
        error.setReason("Invalid request data.");
        error.setMessage(e.getMessage());
        error.setTimestamp(LocalDateTime.now().format(FORMATTER));
        error.setErrors(Collections.emptyList());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<ApiError> handleDataIntegrityViolation(
            DataIntegrityViolationException e
    ) {
        log.error("CONFLICT: {}", e.getMessage());
        ApiError error = new ApiError();
        error.setStatus(HttpStatus.CONFLICT.name());
        // Текст, который тесты обычно ждут в "reason"
        error.setReason("Integrity constraint has been violated.");
        // Можно положить сам текст SQL-ошибки в message:
        error.setMessage(e.getRootCause() != null ? e.getRootCause().getMessage() : e.getMessage());
        error.setTimestamp(LocalDateTime.now().format(FORMATTER));
        error.setErrors(Collections.emptyList());
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiError> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        log.error("BAD_REQUEST: validation failed {}", e.getMessage());

        List<String> allErrors = e.getBindingResult().getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());

        ApiError error = new ApiError();
        error.setStatus(HttpStatus.BAD_REQUEST.name());
        error.setReason("Validation failed.");
        error.setMessage(e.getMessage());
        error.setTimestamp(LocalDateTime.now().format(FORMATTER));
        error.setErrors(allErrors);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // 2) Обработка отсутствия обязательного @RequestParam
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiError> handleMissingRequestParam(MissingServletRequestParameterException e) {
        log.error("BAD_REQUEST: missing request param {}", e.getMessage());
        ApiError error = new ApiError();
        error.setStatus(HttpStatus.BAD_REQUEST.name());
        error.setReason("Required request parameter is missing.");
        error.setMessage(e.getMessage());
        error.setTimestamp(LocalDateTime.now().format(FORMATTER));
        error.setErrors(Collections.emptyList());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // 3) Если JSON некорректный (нет поля description и т.п.)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiError> handleHttpMessageNotReadable(HttpMessageNotReadableException e) {
        log.error("BAD_REQUEST: unreadable http message {}", e.getMessage());
        ApiError error = new ApiError();
        error.setStatus(HttpStatus.BAD_REQUEST.name());
        error.setReason("Invalid request body.");
        error.setMessage(e.getMessage());
        error.setTimestamp(LocalDateTime.now().format(FORMATTER));
        error.setErrors(Collections.emptyList());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

}
