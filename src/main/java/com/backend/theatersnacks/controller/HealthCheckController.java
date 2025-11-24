package com.backend.theatersnacks.controller;

import com.backend.theatersnacks.dto.response.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/health")
public class HealthCheckController {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> healthCheck() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", LocalDateTime.now());
        health.put("application", "Theater Food Backend");
        health.put("version", "1.0.0");

        // Check database connection
        try (Connection connection = dataSource.getConnection()) {
            health.put("database", "Connected - PostgreSQL");
        } catch (Exception e) {
            health.put("database", "Error: " + e.getMessage());
        }

        // Check Redis connection
        try {
            redisTemplate.opsForValue().set("health-check", "OK");
            String value = redisTemplate.opsForValue().get("health-check");
            health.put("redis", "Connected - " + value);
        } catch (Exception e) {
            health.put("redis", "Error: " + e.getMessage());
        }

        return ResponseEntity.ok(ApiResponse.success("Health check completed", health));
    }
}
