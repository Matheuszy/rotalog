package com.rotalog.controller;

import com.rotalog.domain.Veiculo;
import com.rotalog.dto.VeiculoRequest;
import com.rotalog.exception.ResourceNotFoundException;
import com.rotalog.service.VeiculoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * VeiculoController - REST controller para gerenciamento de veículos
 * 
 * FIXME: Sem tratamento global de exceções (@ControllerAdvice)
 * FIXME: Sem CORS configurado
 * FIXME: Sem Swagger/OpenAPI
 * FIXME: Sem HATEOAS
 * FIXME: Retornando entidade diretamente em vez de DTO de resposta
 */
@Slf4j
@RestController
@RequestMapping("/veiculos")
public class VeiculoController {

    @Autowired // FIXME: deveria usar injeção por construtor
    private VeiculoService veiculoService;

    /**
     * Listar todos os veículos
     * 
     * FIXME: Sem paginação
     * FIXME: Retorna entidade diretamente
     */
    @GetMapping
    public ResponseEntity<List<Veiculo>> listarTodos() {
        List<Veiculo> veiculos = veiculoService.listarTodos();
        return ResponseEntity.ok(veiculos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Veiculo> buscarPorId(@PathVariable Long id) {
        log.info("Buscando veículo por id: {}", id);
        Veiculo veiculo = veiculoService.buscarPorId(id);
        return ResponseEntity.ok(veiculo);
    }

    /**
     * Buscar veículo por placa
     */
    @GetMapping("/placa/{placa}")
    public ResponseEntity<?> buscarPorPlaca(@PathVariable String placa) {
        try {
            Veiculo veiculo = veiculoService.buscarPorPlaca(placa);
            return ResponseEntity.ok(veiculo);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("erro", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    /**
     * Listar veículos por status
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<?> listarPorStatus(@PathVariable String status) {
        try {
            List<Veiculo> veiculos = veiculoService.obterVeiculosPorStatus(status);
            return ResponseEntity.ok(veiculos);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("erro", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * Registrar novo veículo
     * 
     * FIXME: Sem @Valid no request body
     * FIXME: Sem URI no header Location
     */
    @PostMapping
    public ResponseEntity<?> registrarVeiculo(@RequestBody VeiculoRequest request) {
        try {
            Veiculo veiculo = veiculoService.registrarVeiculo(
                request.getPlaca(),
                request.getModelo(),
                request.getAnoFabricacao()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(veiculo);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("erro", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * Atualizar veículo
     * 
     * FIXME: Sem @Valid
     * FIXME: Sem verificação de campos nulos
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarVeiculo(@PathVariable Long id, @RequestBody VeiculoRequest request) {
        try {
            Veiculo veiculo = veiculoService.atualizarVeiculo(
                id,
                request.getModelo(),
                request.getAnoFabricacao(),
                request.getQuilometragem()
            );
            return ResponseEntity.ok(veiculo);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("erro", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * Atualizar quilometragem
     */
    @PatchMapping("/{id}/quilometragem")
    public ResponseEntity<?> atualizarQuilometragem(@PathVariable Long id, @RequestBody Map<String, Long> body) {
        try {
            Long quilometragem = body.get("quilometragem");
            if (quilometragem == null) {
                Map<String, String> error = new HashMap<>();
                error.put("erro", "Campo 'quilometragem' é obrigatório");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }
            Veiculo veiculo = veiculoService.atualizarQuilometragem(id, quilometragem);
            return ResponseEntity.ok(veiculo);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("erro", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * Desativar veículo
     */
    @PatchMapping("/{id}/desativar")
    public ResponseEntity<?> desativarVeiculo(@PathVariable Long id) {
        try {
            Veiculo veiculo = veiculoService.desativarVeiculo(id);
            return ResponseEntity.ok(veiculo);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("erro", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    /**
     * Reativar veículo
     */
    @PatchMapping("/{id}/reativar")
    public ResponseEntity<?> reativarVeiculo(@PathVariable Long id) {
        try {
            Veiculo veiculo = veiculoService.reativarVeiculo(id);
            return ResponseEntity.ok(veiculo);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("erro", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    /**
     * Obter estatísticas da frota
     * 
     * FIXME: Retorna String JSON em vez de objeto tipado
     */
    @GetMapping("/estatisticas")
    public ResponseEntity<String> obterEstatisticas() {
        // FIXME: Chamando método com typo no nome
        String stats = veiculoService.obterEstatisticasFreita();
        return ResponseEntity.ok(stats);
    }
}
