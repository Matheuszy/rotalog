package com.rotalog.controller;

import com.rotalog.domain.Motorista;
import com.rotalog.dto.MotoristaRequest;
import com.rotalog.service.MotoristaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MotoristaController - REST controller para gerenciamento de motoristas
 * 
 * FIXME: Mesmo padrão de tratamento de erro do VeiculoController (copy-paste)
 * FIXME: Sem @ControllerAdvice
 * FIXME: Retornando entidade diretamente
 */
@Slf4j
@RestController
@RequestMapping("/motoristas")
public class MotoristaController {

    @Autowired
    private MotoristaService motoristaService;

    @GetMapping
    public ResponseEntity<List<Motorista>> listarTodos() {
        return ResponseEntity.ok(motoristaService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(motoristaService.buscarPorId(id));
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("erro", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    @GetMapping("/cnh/{cnh}")
    public ResponseEntity<?> buscarPorCnh(@PathVariable String cnh) {
        try {
            return ResponseEntity.ok(motoristaService.buscarPorCnh(cnh));
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("erro", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Motorista>> listarPorStatus(@PathVariable String status) {
        return ResponseEntity.ok(motoristaService.listarPorStatus(status));
    }

    @GetMapping("/cnh-vencida")
    public ResponseEntity<List<Motorista>> listarComCnhVencida() {
        return ResponseEntity.ok(motoristaService.listarComCnhVencida());
    }

    @PostMapping
    public ResponseEntity<?> cadastrarMotorista(@RequestBody MotoristaRequest request) {
        try {
            Motorista motorista = motoristaService.cadastrarMotorista(
                request.getNome(),
                request.getCnh(),
                request.getCategoriaCnh(),
                request.getVencimentoCnh()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(motorista);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("erro", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarMotorista(@PathVariable Long id, @RequestBody MotoristaRequest request) {
        try {
            Motorista motorista = motoristaService.atualizarMotorista(
                id,
                request.getNome(),
                request.getCategoriaCnh(),
                request.getVencimentoCnh()
            );
            return ResponseEntity.ok(motorista);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("erro", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @PatchMapping("/{id}/desativar")
    public ResponseEntity<?> desativarMotorista(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(motoristaService.desativarMotorista(id));
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("erro", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    @PatchMapping("/{id}/reativar")
    public ResponseEntity<?> reativarMotorista(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(motoristaService.reativarMotorista(id));
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("erro", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }
}
