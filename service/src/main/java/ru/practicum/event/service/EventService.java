package ru.practicum.event.service;

import jakarta.servlet.http.HttpServletRequest;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventRequestStatusUpdateRequest;
import ru.practicum.event.dto.EventRequestStatusUpdateResult;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Интерфейс для работы со событиями (Event).
 */
public interface EventService {

    // ----------------------- PUBLIC -----------------------
    /**
     * Получение списка (коротких DTO) опубликованных событий с фильтрацией.
     */
    List<EventShortDto> publicGetEvents(String text,
                                        List<Long> categories,
                                        Boolean paid,
                                        LocalDateTime rangeStart,
                                        LocalDateTime rangeEnd,
                                        Boolean onlyAvailable,
                                        String sort,
                                        int from,
                                        int size);

    /**
     * Получение полной информации о PUBLISHED событии (по id).
     * Включает логику обращения к сервису статистики:
     *  - сохранение "хита" (IP + URI)
     *  - получение уникальных IP из статистики
     *  - обновление/вычисление поля views
     */
    EventFullDto publicGetEvent(Long id, HttpServletRequest request);


    // ----------------------- ADMIN -----------------------
    /**
     * Поиск событий (полных DTO) по критериям (для админа).
     */
    List<EventFullDto> adminSearchEvents(List<Long> users,
                                         List<String> states,
                                         List<Long> categories,
                                         LocalDateTime rangeStart,
                                         LocalDateTime rangeEnd,
                                         int from,
                                         int size);

    /**
     * Админ-редактирование/публикация/отклонение события.
     */
    EventFullDto adminUpdateEvent(Long eventId,
                                  String annotation,
                                  Long categoryId,
                                  String description,
                                  LocalDateTime eventDate,
                                  Float lat,
                                  Float lon,
                                  Boolean paid,
                                  Long participantLimit,
                                  Boolean requestModeration,
                                  String stateAction,
                                  String title);


    // ----------------------- PRIVATE (пользователи) -----------------------
    /**
     * Получение списка (коротких DTO) событий текущего пользователя.
     */
    List<EventShortDto> getUserEvents(Long userId, int from, int size);

    /**
     * Создание нового события (дату >= сейчас+2ч).
     */
    EventFullDto addEvent(Long userId, NewEventDto dto);

    /**
     * Получение полной информации о конкретном событии текущего пользователя.
     */
    EventFullDto getUserEvent(Long userId, Long eventId);

    /**
     * Редактирование события самим пользователем (только PENDING или CANCELED).
     */
    EventFullDto updateUserEvent(Long userId,
                                 Long eventId,
                                 String annotation,
                                 Long categoryId,
                                 String description,
                                 LocalDateTime eventDate,
                                 Float lat,
                                 Float lon,
                                 Boolean paid,
                                 Long participantLimit,
                                 Boolean requestModeration,
                                 String stateAction,
                                 String title);

    /**
     * Обработка заявок на участие (инициатором).
     * Если у вас логика вынесена в RequestService, можно не дублировать здесь.
     */
    default EventRequestStatusUpdateResult changeRequestStatus(
            Long userId,
            Long eventId,
            EventRequestStatusUpdateRequest updateRequest
    ) {
        throw new UnsupportedOperationException("Not implemented here");
    }
}
