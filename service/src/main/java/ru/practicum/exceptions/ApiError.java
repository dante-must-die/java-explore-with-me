package ru.practicum.exceptions;

import lombok.Data;

import java.util.List;

/**
 * DTO для описания ошибки
 */
@Data
public class ApiError {
    private List<String> errors;
    private String message;
    private String reason;
    private String status;
    private String timestamp;
}
