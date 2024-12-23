package ru.practicum.statsclient;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.practicum.statsdto.EndpointHit;
import ru.practicum.statsdto.ViewStats;

import java.util.List;
import java.util.Map;

@Component
public class StatsClient {
    private final RestTemplate restTemplate = new RestTemplate();
    private static final String BASE_URL = "http://localhost:9090";

    public void hit(EndpointHit endpointHit) {
        restTemplate.postForEntity(
                BASE_URL + "/hit",
                endpointHit,
                Void.class
        );
    }

    public List<ViewStats> getStats(String start, String end, List<String> uris, boolean unique) {
        String uriString = BASE_URL + "/stats?start={start}&end={end}&unique={unique}";
        /*if (uris != null && !uris.isEmpty()) {

        }*/
        ResponseEntity<ViewStats[]> response = restTemplate.getForEntity(
                uriString,
                ViewStats[].class,
                Map.of(
                        "start", start,
                        "end", end,
                        "unique", unique
                )
        );
        ViewStats[] body = response.getBody();
        return body == null ? List.of() : List.of(body);
    }
}
