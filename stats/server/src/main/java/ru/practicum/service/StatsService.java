package ru.practicum.service;

import ru.practicum.EndpointHit;
import ru.practicum.ViewStats;

import java.util.List;

public interface StatsService {
    void saveHit(EndpointHit hitDto);

    /**
     * Возвращает список ViewStats,
     * учитывая параметры start, end, uris, unique.
     */
    List<ViewStats> getStats(String start, String end, List<String> uris, Boolean unique);
}
