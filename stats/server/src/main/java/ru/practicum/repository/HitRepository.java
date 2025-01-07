package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.ViewStats;
import ru.practicum.model.HitEntity;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface HitRepository extends JpaRepository<HitEntity, Long> {

    // Получение всех хитов с фильтрацией по URIs
    @Query("""
           SELECT new ru.practicum.ViewStats(
             h.app,
             h.uri,
             COUNT(h.id)
           )
           FROM HitEntity h
           WHERE h.timestamp BETWEEN :start AND :end
             AND h.uri IN :uris
           GROUP BY h.app, h.uri
           ORDER BY COUNT(h.id) DESC
           """)
    List<ViewStats> getAllHitsWithUris(LocalDateTime start,
                                       LocalDateTime end,
                                       List<String> uris);

    // Получение всех хитов без фильтрации по URIs
    @Query("""
           SELECT new ru.practicum.ViewStats(
             h.app,
             h.uri,
             COUNT(h.id)
           )
           FROM HitEntity h
           WHERE h.timestamp BETWEEN :start AND :end
           GROUP BY h.app, h.uri
           ORDER BY COUNT(h.id) DESC
           """)
    List<ViewStats> getAllHits(LocalDateTime start, LocalDateTime end);

    // Получение уникальных хитов с фильтрацией по URIs
    @Query("""
           SELECT new ru.practicum.ViewStats(
             h.app,
             h.uri,
             COUNT(DISTINCT h.ip)
           )
           FROM HitEntity h
           WHERE h.timestamp BETWEEN :start AND :end
             AND h.uri IN :uris
           GROUP BY h.app, h.uri
           ORDER BY COUNT(DISTINCT h.ip) DESC
           """)
    List<ViewStats> getUniqueHitsWithUris(LocalDateTime start,
                                          LocalDateTime end,
                                          List<String> uris);

    // Получение уникальных хитов без фильтрации по URIs
    @Query("""
           SELECT new ru.practicum.ViewStats(
             h.app,
             h.uri,
             COUNT(DISTINCT h.ip)
           )
           FROM HitEntity h
           WHERE h.timestamp BETWEEN :start AND :end
           GROUP BY h.app, h.uri
           ORDER BY COUNT(DISTINCT h.ip) DESC
           """)
    List<ViewStats> getUniqueHits(LocalDateTime start, LocalDateTime end);
}
