package ru.practicum.event.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.EndpointHit;
import ru.practicum.client.StatsClient;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.service.EventService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Публичный доступ к событиям: /events
 */
@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class PublicEventController {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final EventService eventService;
    private final StatsClient statsClient;  // <-- Добавили внедрение StatsClient

    /**
     * GET /events
     * Публичный поиск опубликованных событий с фильтром.
     */
    @GetMapping
    public List<EventShortDto> getEvents(@Validated @RequestParam(required = false) String text,
                                         @RequestParam(required = false) List<Long> categories,
                                         @RequestParam(required = false) Boolean paid,
                                         @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                         @RequestParam(required = false) LocalDateTime rangeStart,
                                         @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                         @RequestParam(required = false) LocalDateTime rangeEnd,
                                         @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                                         @RequestParam(required = false) String sort,
                                         @RequestParam(defaultValue = "0") int from,
                                         @RequestParam(defaultValue = "10") int size,
                                         HttpServletRequest request) { // <-- добавили request

        // 1) Получаем список событий через сервис
        List<EventShortDto> events = eventService.publicGetEvents(
                text,
                categories,
                paid,
                rangeStart,
                rangeEnd,
                onlyAvailable,
                sort,
                from,
                size
        );

        // 2) Отправляем hit в сервис статистики
        try {
            EndpointHit hit = new EndpointHit();
            hit.setApp("ewm-main-service"); // Название приложения, ожидаемое тестом
            hit.setIp(request.getRemoteAddr());
            hit.setUri(request.getRequestURI());          // Должно получиться "/events"
            hit.setTimestamp(LocalDateTime.now().format(FORMATTER));
            statsClient.hit(hit);
        } catch (Exception ex) {
            System.err.println("Failed to send hit to stats-service: " + ex.getMessage());
        }

        // 3) Возвращаем результат
        return events;
    }

    @GetMapping("/{id}")
    public EventFullDto getEvent(@PathVariable Long id,
                                 HttpServletRequest request) {
        // передаём request (с IP) в сервис, чтобы учитывалось в статистике
        return eventService.publicGetEvent(id, request);
    }
}
