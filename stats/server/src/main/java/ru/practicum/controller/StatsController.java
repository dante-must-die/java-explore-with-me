package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.EndpointHit;
import ru.practicum.ViewStats;
import ru.practicum.service.StatsService;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statsService;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * POST /hit
     * Сохранение информации о том, что к эндпоинту был запрос.
     * Возвращает 201 Created при успехе.
     */
    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void hit(@RequestBody EndpointHit hitDto) {
        statsService.saveHit(hitDto);
    }

    /**
     * GET /stats
     * Получение статистики по посещениям за период [start, end].
     * Параметры:
     *  - start (String "yyyy-MM-dd HH:mm:ss")
     *  - end (String "yyyy-MM-dd HH:mm:ss")
     *  - uris (List<String>) (необязательный)
     *  - unique (boolean, default = false)
     */
    @GetMapping("/stats")
    public List<ViewStats> getStats(
            @RequestParam String start,
            @RequestParam String end,
            @RequestParam(required = false) List<String> uris,
            @RequestParam(defaultValue = "false") Boolean unique) {

        try {
            // Декодируем параметры, заменяя %20 на пробелы
            String decodedStart = URLDecoder.decode(start, StandardCharsets.UTF_8);
            String decodedEnd = URLDecoder.decode(end, StandardCharsets.UTF_8);

            // Парсим строки в LocalDateTime
            LocalDateTime startDateTime = LocalDateTime.parse(decodedStart, FORMATTER);
            LocalDateTime endDateTime = LocalDateTime.parse(decodedEnd, FORMATTER);

            return statsService.getStats(startDateTime, endDateTime, uris, unique);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid date format. Expected format: yyyy-MM-dd HH:mm:ss", e);
        }
    }
}
