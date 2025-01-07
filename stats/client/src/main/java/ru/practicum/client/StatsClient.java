package ru.practicum.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.EndpointHit;
import ru.practicum.ViewStats;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class StatsClient {

    private final String serverUrl;
    private final RestTemplate rest;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Инжектим RestTemplate и URL сервера статистики через конструктор.
     */
    public StatsClient(RestTemplate rest, @Value("${stats.server.url}") String serverUrl) {
        this.rest = rest;
        this.serverUrl = serverUrl;
    }

    /**
     * POST /hit — сохранение информации о запросе.
     */
    public void hit(EndpointHit endpointHit) {
        String url = UriComponentsBuilder.fromHttpUrl(serverUrl)
                .path("/hit")
                .toUriString();
        rest.postForEntity(url, endpointHit, Void.class);
    }

    /**
     * GET /stats — получение статистики.
     */
    public List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        String startStr = start.format(FORMATTER);
        String endStr = end.format(FORMATTER);

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(serverUrl)
                .path("/stats")
                .queryParam("start", startStr)
                .queryParam("end", endStr)
                .queryParam("unique", unique);

        if (uris != null && !uris.isEmpty()) {
            for (String uri : uris) {
                uriBuilder.queryParam("uris", uri);
            }
        }

        String url = uriBuilder.toUriString();
        ViewStats[] response = rest.getForObject(url, ViewStats[].class);
        if (response == null) {
            return List.of();
        }
        return List.of(response);
    }

}
