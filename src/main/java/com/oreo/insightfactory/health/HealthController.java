package com.oreo.insightfactory.health;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

@RestController
public class HealthController {

    @GetMapping("/health")
    Map<String, Object> health() {
        return Map.of(
                "status", "UP",
                "service", "oreo-insight-factory",
                "timestamp", Instant.now()
        );
    }
}
