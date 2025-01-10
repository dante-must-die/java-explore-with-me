package ru.practicum.request.service;

import ru.practicum.event.dto.EventRequestStatusUpdateRequest;
import ru.practicum.event.dto.EventRequestStatusUpdateResult;
import ru.practicum.request.dto.ParticipationRequestDto;

import java.util.List;

public interface RequestService {

    /**
     * Получить все заявки пользователя (на чужие события).
     */
    List<ParticipationRequestDto> getUserRequests(Long userId);

    /**
     * Добавить заявку пользователя userId на участие в событии eventId.
     */
    ParticipationRequestDto addRequest(Long userId, Long eventId);

    /**
     * Отменить заявку requestId у пользователя userId.
     */
    ParticipationRequestDto cancelRequest(Long userId, Long requestId);

    /**
     * Изменение статуса заявок на участие в событии (инициатор события).
     * Возвращает результат с подтверждёнными/отклонёнными заявками.
     */
    EventRequestStatusUpdateResult changeRequestStatus(Long userId,
                                                       Long eventId,
                                                       EventRequestStatusUpdateRequest updateRequest);
}
