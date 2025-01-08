package ru.practicum.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.EndpointHit;
import ru.practicum.ViewStats;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class StatsClient {

    @Value("${stats.server.url:http://stats-service:9090}")
    private String serverUrl;

    private final RestTemplate rest;

    // Формат даты, ожидаемый stats-service
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public StatsClient(RestTemplate rest) {
        this.rest = rest;
    }

    /**
     * POST /hit — сохранение информации о запросе.
     */
    public void hit(EndpointHit endpointHit) {
        try {
            rest.postForEntity(serverUrl + "/hit", endpointHit, Void.class);
        } catch (Exception e) {
            // Логирование ошибки и проброс исключения
            // Например, используя SLF4J:
            // log.error("Failed to send hit to stats-service", e);
            throw new RuntimeException("Failed to send hit to stats-service", e);
        }
    }

    /**
     * GET /stats — получение статистики.
     */
    public List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        try {
            String startStr = start.format(FORMATTER);
            String endStr = end.format(FORMATTER);

            // Построение URI с параметрами
            UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(serverUrl + "/stats")
                    .queryParam("start", startStr)
                    .queryParam("end", endStr)
                    .queryParam("unique", unique);

            if (uris != null && !uris.isEmpty()) {
                uris.forEach(uri -> uriBuilder.queryParam("uris", uri));
            }

            String uri = uriBuilder.encode(StandardCharsets.UTF_8).toUriString();

            // Логирование построенного URI (опционально)
            // log.debug("Requesting stats with URI: {}", uri);

            ResponseEntity<ViewStats[]> response = rest.getForEntity(uri, ViewStats[].class);
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return List.of(response.getBody());
            } else {
                // Обработка неудачных ответов
                // log.warn("Received non-OK status from stats-service: {}", response.getStatusCode());
                return List.of();
            }
        } catch (Exception e) {
            // Логирование ошибки и проброс исключения
            // log.error("Failed to get stats from stats-service", e);
            throw new RuntimeException("Failed to get stats from stats-service", e);
        }
    }
}
