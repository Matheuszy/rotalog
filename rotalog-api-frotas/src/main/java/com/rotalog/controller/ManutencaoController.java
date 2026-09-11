package com.rotalog.controller;

import com.rotalog.domain.Manutencao;
import com.rotalog.dto.ManutencaoRequest;
import com.rotalog.service.ManutencaoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ManutencaoController - REST controller para gerenciamento de manutenções
 * 
 * FIXME: Mesmo padrão copy-paste de tratamento de erro
 * FIXME: Sem @ControllerAdvice
 */
@Slf4j
@RestController
@RequestMapping("/manutencoes")
public class ManutencaoController {

    @Autowired
    private ManutencaoService manutencaoService;

    @GetMapping
    public ResponseEntity<List<Manutencao>> listarTodas() {
        return ResponseEntity.ok(manutencaoService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(manutencaoService.buscarPorId(id));
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("erro", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    @GetMapping("/veiculo/{veiculoId}")
    public ResponseEntity<List<Manutencao>> listarPorVeiculo(@PathVariable Long veiculoId) {
        return ResponseEntity.ok(manutencaoService.listarPorVeiculo(veiculoId));
    }

    @GetMapping("/pendentes")
    public ResponseEntity<List<Manutencao>> listarPendentes() {
        return ResponseEntity.ok(manutencaoService.listarPendentes());
    }

    @GetMapping("/veiculo/{veiculoId}/ultima")
    public ResponseEntity<?> obterUltimaManutencao(@PathVariable Long veiculoId) {
        try {
            return ResponseEntity.ok(manutencaoService.obterUltimaManutencao(veiculoId));
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("erro", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    @PostMapping
    public ResponseEntity<?> agendarManutencao(@RequestBody ManutencaoRequest request) {
        try {
            Manutencao manutencao = manutencaoService.agendarManutencao(
                request.getVeiculoId(),
                request.getTipoManutencao(),
                request.getDescricao(),
                request.getCustoEstimado()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(manutencao);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("erro", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @PatchMapping("/{id}/iniciar")
    public ResponseEntity<?> iniciarManutencao(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(manutencaoService.iniciarManutencao(id));
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("erro", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @PatchMapping("/{id}/concluir")
    public ResponseEntity<?> concluirManutencao(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        try {
            BigDecimal custoFinal = null;
            if (body.containsKey("custoFinal")) {
                custoFinal = new BigDecimal(body.get("custoFinal").toString());
            }
            return ResponseEntity.ok(manutencaoService.concluirManutencao(id, custoFinal));
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("erro", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<?> cancelarManutencao(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(manutencaoService.cancelarManutencao(id));
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("erro", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
}
