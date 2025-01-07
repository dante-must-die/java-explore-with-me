package ru.practicum.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ru.practicum.ViewStats;
import ru.practicum.model.HitEntity;

import java.time.LocalDateTime;
import java.util.List;

public interface HitRepository extends JpaRepository<HitEntity, Long> {


    @Query("""
           SELECT new ru.practicum.ViewStats(
             h.app,
             h.uri,
             COUNT(h.id)
           )
           FROM HitEntity h
           WHERE h.timestamp BETWEEN :start AND :end
             AND (:urisEmpty = TRUE OR h.uri IN :uris)
           GROUP BY h.app, h.uri
           ORDER BY COUNT(h.id) DESC
           """)
    List<ViewStats> getAllHits(@Param("start") LocalDateTime start,
                               @Param("end") LocalDateTime end,
                               @Param("uris") List<String> uris,
                               @Param("urisEmpty") boolean urisEmpty);

    /**
     * Подсчёт УНИКАЛЬНЫХ IP (distinct).
     */
    @Query("""
           SELECT new ru.practicum.ViewStats(
             h.app,
             h.uri,
             COUNT(DISTINCT h.ip)
           )
           FROM HitEntity h
           WHERE h.timestamp BETWEEN :start AND :end
             AND (:urisEmpty = TRUE OR h.uri IN :uris)
           GROUP BY h.app, h.uri
           ORDER BY COUNT(DISTINCT h.ip) DESC
           """)
    List<ViewStats> getUniqueHits(@Param("start") LocalDateTime start,
                                  @Param("end") LocalDateTime end,
                                  @Param("uris") List<String> uris,
                                  @Param("urisEmpty") boolean urisEmpty);
}