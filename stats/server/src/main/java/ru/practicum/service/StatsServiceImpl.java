package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.EndpointHit;
import ru.practicum.ViewStats;
import ru.practicum.model.HitEntity;
import ru.practicum.repository.HitRepository;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final HitRepository repository;

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
        String decodedStart = URLDecoder.decode(startStr, StandardCharsets.UTF_8);
        String decodedEnd = URLDecoder.decode(endStr, StandardCharsets.UTF_8);

        LocalDateTime start = LocalDateTime.parse(decodedStart, FORMATTER);
        LocalDateTime end = LocalDateTime.parse(decodedEnd, FORMATTER);

        // Если uris == null — делаем из неё пустой список,
        // чтобы в запросе использовать проверку (urisEmpty) и не падать на null
        if (uris == null) {
            uris = Collections.emptyList();
        }

        boolean urisEmpty = uris.isEmpty();

        if (Boolean.TRUE.equals(unique)) {
            // Считаем уникальные IP (distinct).
            return repository.getUniqueHits(start, end, uris, urisEmpty);
        } else {
            // Считаем все запросы
            return repository.getAllHits(start, end, uris, urisEmpty);
        }
    }
}
