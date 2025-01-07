package ru.practicum.client;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.practicum.EndpointHit;
import ru.practicum.ViewStats;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class StatsClient {

    private static final String SERVER_URL = "http://stats-server:9090";
    private final RestTemplate rest;

    /**
     * Инжектим RestTemplate через конструктор.
     */
    public StatsClient(RestTemplate rest) {
        this.rest = rest;
    }

    /**
     * POST /hit — сохранение информации о запросе.
     */
    public void hit(EndpointHit endpointHit) {
        rest.postForEntity(SERVER_URL + "/hit", endpointHit, Void.class);
    }

    /**
     * GET /stats — получение статистики.
     */
    public List<ViewStats> getStats(String start, String end, List<String> uris, boolean unique) {
        String startEnc = URLEncoder.encode(start, StandardCharsets.UTF_8);
        String endEnc   = URLEncoder.encode(end, StandardCharsets.UTF_8);

        StringBuilder sb = new StringBuilder(SERVER_URL + "/stats?");
        sb.append("start=").append(startEnc);
        sb.append("&end=").append(endEnc);

        if (uris != null && !uris.isEmpty()) {
            for (String uri : uris) {
                sb.append("&uris=").append(URLEncoder.encode(uri, StandardCharsets.UTF_8));
            }
        }
        sb.append("&unique=").append(unique);

        ViewStats[] response = rest.getForObject(sb.toString(), ViewStats[].class);
        if (response == null) {
            return List.of();
        }
        return List.of(response);
    }
}