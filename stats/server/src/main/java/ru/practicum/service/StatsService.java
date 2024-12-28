package ru.practicum.service;

import ru.practicum.EndpointHit;
import ru.practicum.ViewStats;

import java.util.List;

public interface StatsService {

    /**
     * Сохраняет запрос (EndpointHit) в хранилище.
     *
     * @param hitDto DTO с информацией о запросе.
     */
    void saveHit(EndpointHit hitDto);

    /**
     * Возвращает список ViewStats,
     * учитывая параметры start, end, uris, unique.
     *
     * @param start  дата-время начала интервала (в формате "yyyy-MM-dd HH:mm:ss")
     * @param end    дата-время конца интервала (в формате "yyyy-MM-dd HH:mm:ss")
     * @param uris   список URI, по которым нужно собрать статистику (может быть пустым)
     * @param unique если true — считать только уникальные IP
     * @return список ViewStats
     */
    List<ViewStats> getStats(String start, String end, List<String> uris, Boolean unique);
}
