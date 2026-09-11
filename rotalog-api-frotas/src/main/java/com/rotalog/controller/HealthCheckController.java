package com.rotalog.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * HealthCheckController - Endpoint de health check
 * 
 * FIXME: Deveria usar Spring Actuator em vez de controller manual
 * FIXME: Sem métricas
 * FIXME: Sem readiness/liveness probes separados
 */
@Slf4j
@RestController
@RequestMapping("/health")
public class HealthCheckController {

    // FIXME: RestTemplate criado manualmente
    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> health = new HashMap<>();
        health.put("service", "api-frotas");
        health.put("status", "UP");
        health.put("timestamp", LocalDateTime.now().toString());
        health.put("version", "1.0-SNAPSHOT"); // FIXME: hardcoded

        // Verificar dependências
        Map<String, String> dependencies = new HashMap<>();
        
        // Check api-entregas
        try {
            restTemplate.getForEntity("http://localhost:3000/api/health", String.class);
            dependencies.put("api-entregas", "UP");
        } catch (Exception e) {
            dependencies.put("api-entregas", "DOWN");
        }

        // Check api-notificacoes
        try {
            restTemplate.getForEntity("http://localhost:5000/api/health", String.class);
            dependencies.put("api-notificacoes", "UP");
        } catch (Exception e) {
            dependencies.put("api-notificacoes", "DOWN");
        }

        health.put("dependencies", dependencies);
        return ResponseEntity.ok(health);
    }
}
