package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.EndpointHit;
import ru.practicum.ViewStats;
import ru.practicum.model.HitEntity;
import ru.practicum.repository.HitRepository;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final HitRepository repository;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void saveHit(EndpointHit hitDto) {
        // Преобразуем EndpointHit -> HitEntity
        HitEntity entity = new HitEntity();
        entity.setApp(hitDto.getApp());
        entity.setUri(hitDto.getUri());
        entity.setIp(hitDto.getIp());
        entity.setTimestamp(LocalDateTime.parse(hitDto.getTimestamp(), FORMATTER));

        repository.save(entity);
    }

    @Override
    public List<ViewStats> getStats(String startStr, String endStr, List<String> uris, Boolean unique) {
        LocalDateTime start = LocalDateTime.parse(startStr, FORMATTER);
        LocalDateTime end = LocalDateTime.parse(endStr, FORMATTER);

        // Если null — сделаем пустой список, чтобы мы могли игнорировать условие по uri
        if (uris == null) {
            uris = Collections.emptyList();
        }

        boolean urisEmpty = uris.isEmpty();

        if (unique != null && unique) {
            // Считаем уникальные IP
            return repository.getUniqueHits(start, end, uris, urisEmpty);
        } else {
            // Считаем все запросы
            return repository.getAllHits(start, end, uris, urisEmpty);
        }
    }
}
