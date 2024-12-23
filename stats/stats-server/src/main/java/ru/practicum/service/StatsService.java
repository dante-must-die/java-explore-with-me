package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.statsdto.EndpointHit;
import ru.practicum.statsdto.ViewStats;
import ru.practicum.model.EndpointHitEntity;
import ru.practicum.repository.EndpointHitRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsService {
    private final EndpointHitRepository repository;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public void saveHit(EndpointHit hit) {
        EndpointHitEntity entity = new EndpointHitEntity();
        entity.setApp(hit.getApp());
        entity.setUri(hit.getUri());
        entity.setIp(hit.getIp());
        entity.setTimestamp(LocalDateTime.parse(hit.getTimestamp(), formatter));
        repository.save(entity);
    }

    public List<ViewStats> getStats(String start, String end, List<String> uris, boolean unique) {

        return List.of();
    }
}
