package com.rotalog.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * EntregaClient - HTTP client for api-entregas (Node.js)
 * 
 * FIXME: URL hardcoded
 * FIXME: Sem circuit breaker
 * FIXME: Sem retry logic
 * FIXME: Sem timeout configurável
 * FIXME: Sem tratamento de erro adequado
 * FIXME: Retornando Map em vez de DTO tipado
 */
@Slf4j
@Component
public class EntregaClient {

    // FIXME: URL hardcoded - deveria estar em application.properties
    private static final String ENTREGAS_API_URL = "http://localhost:3000";

    // FIXME: RestTemplate criado manualmente
    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Busca entregas ativas de um veículo
     * 
     * FIXME: Retorna List<Map> em vez de List<EntregaDTO>
     * FIXME: Sem tratamento de erro de deserialização
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> buscarEntregasPorVeiculo(String placa) {
        try {
            String url = ENTREGAS_API_URL + "/api/entregas?veiculo=" + placa;
            log.info("Buscando entregas para veículo {} em {}", placa, url);

            ResponseEntity<List> response = restTemplate.getForEntity(url, List.class);
            
            if (response.getBody() != null) {
                return response.getBody();
            }
            return Collections.emptyList();
        } catch (Exception e) {
            log.error("Erro ao buscar entregas do veículo {}: {}", placa, e.getMessage());
            return Collections.emptyList(); // FIXME: engolindo exceção
        }
    }

    /**
     * Verifica se veículo tem entregas em andamento
     * 
     * FIXME: Faz GET completo quando poderia ser HEAD ou COUNT
     */
    public boolean veiculoTemEntregasAtivas(String placa) {
        List<Map<String, Object>> entregas = buscarEntregasPorVeiculo(placa);
        return entregas.stream()
                .anyMatch(e -> "em_transito".equals(e.get("status")) || "pendente".equals(e.get("status")));
    }

    /**
     * Busca estatísticas de entregas
     * 
     * FIXME: Retorna Map genérico
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> buscarEstatisticasEntregas() {
        try {
            String url = ENTREGAS_API_URL + "/api/entregas/stats";
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            return response.getBody() != null ? response.getBody() : Collections.emptyMap();
        } catch (Exception e) {
            log.error("Erro ao buscar estatísticas de entregas: {}", e.getMessage());
            return Collections.emptyMap();
        }
    }
}
