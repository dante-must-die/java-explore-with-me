package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.practicum.EndpointHit;
import ru.practicum.ViewStats;
import ru.practicum.model.HitEntity;
import ru.practicum.repository.HitRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private static final Logger logger = LoggerFactory.getLogger(StatsServiceImpl.class);

    private final HitRepository repository;

    @Override
    public void saveHit(EndpointHit hitDto) {
        try {
            HitEntity entity = new HitEntity();
            entity.setApp(hitDto.getApp());
            entity.setUri(hitDto.getUri());
            entity.setIp(hitDto.getIp());
            entity.setTimestamp(hitDto.getTimestamp()); // Предполагается, что hitDto.getTimestamp() уже LocalDateTime

            repository.save(entity);
            logger.info("Saved hit: {}", entity);
        } catch (Exception e) {
            logger.error("Error saving hit: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        try {
            logger.info("Fetching stats from {} to {}", start, end);

            boolean hasUris = uris != null && !uris.isEmpty();
            List<ViewStats> stats;

            if (Boolean.TRUE.equals(unique)) {
                if (hasUris) {
                    logger.info("Fetching unique hits with URIs: {}", uris);
                    stats = repository.getUniqueHitsWithUris(start, end, uris);
                } else {
                    logger.info("Fetching unique hits without URIs filter");
                    stats = repository.getUniqueHits(start, end);
                }
            } else {
                if (hasUris) {
                    logger.info("Fetching all hits with URIs: {}", uris);
                    stats = repository.getAllHitsWithUris(start, end, uris);
                } else {
                    logger.info("Fetching all hits without URIs filter");
                    stats = repository.getAllHits(start, end);
                }
            }

            logger.info("Fetched {} stats records", stats.size());
            return stats;
        } catch (Exception e) {
            logger.error("Error fetching stats: {}", e.getMessage());
            throw e;
        }
    }
}
