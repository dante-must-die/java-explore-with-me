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

    private final RestTemplate rest = new RestTemplate();
    private final String serverUrl = "http://localhost:9090";

    /**
     * POST /hit — сохранение информации о запросе.
     */
    public void hit(EndpointHit endpointHit) {
        rest.postForEntity(serverUrl + "/hit", endpointHit, Void.class);
    }

    /**
     * GET /stats — получение статистики.
     */
    public List<ViewStats> getStats(String start, String end, List<String> uris, boolean unique) {
        // URLEncoder для safety
        String startEnc = URLEncoder.encode(start, StandardCharsets.UTF_8);
        String endEnc   = URLEncoder.encode(end, StandardCharsets.UTF_8);

        // Собираем URL: /stats?start=...&end=...&uris=...&unique=...
        StringBuilder sb = new StringBuilder(serverUrl + "/stats?");
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

