package ru.practicum.event.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * DTO для запроса изменения статусов заявок на участие в событии.
 * Поля:
 * - requestIds: идентификаторы запросов
 * - status: требуемый статус ("CONFIRMED" / "REJECTED")
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventRequestStatusUpdateRequest {
    private List<Long> requestIds;
    private String status;  // "CONFIRMED" или "REJECTED"
}

