package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.statsclient.StatsClient;
import ru.practicum.statsdto.EndpointHit;

@RestController
@RequiredArgsConstructor
public class TestController {
    private final StatsClient statsClient;

    @GetMapping("/test-stat")
    public String testStat() {
        EndpointHit hit = new EndpointHit();
        hit.setApp("ewm-main-service");
        hit.setUri("/test-stat");
        hit.setIp("127.0.0.1");
        hit.setTimestamp("2024-12-21 12:00:00");

        statsClient.hit(hit);

        return "Hit sent!";
    }
}

