package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.EndpointHit;
import ru.practicum.ViewStats;
import ru.practicum.service.StatsService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statsService;

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
    public List<ViewStats> getStats(@RequestParam String start,
                                    @RequestParam String end,
                                    @RequestParam(required = false) List<String> uris,
                                    @RequestParam(defaultValue = "false") Boolean unique) {
        return statsService.getStats(start, end, uris, unique);
    }
}