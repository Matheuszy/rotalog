package com.rotalog.service;

import com.rotalog.domain.Veiculo;
import com.rotalog.exception.ResourceNotFoundException;
import com.rotalog.repository.VeiculoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * VeiculoService - Legacy service with mixed responsibilities
 * This service contains business logic, validation, and notification concerns
 * Intentional technical debt for the course
 * 
 * TODO: Break this into smaller services
 * TODO: Extract validation logic
 * TODO: Move notification logic to separate service
 * TODO: Add proper error handling
 */
@Slf4j
@Service
public class VeiculoService {

    @Autowired
    private VeiculoRepository veiculoRepository;

    @Autowired
    private NotificacaoClient notificacaoClient;

    /**
     * Lista todos os veículos
     * 
     * FIXME: Sem paginação - pode retornar milhares de registros
     * FIXME: Sem cache
     */
    public List<Veiculo> listarTodos() {
        log.info("Listando todos os veículos"); // misturando pt/en nos logs
        return veiculoRepository.findAll();
    }

    /**
     * Busca veículo por ID
     */
    public Veiculo buscarPorId(Long id) {
        return veiculoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Veículo não encontrado: " + id));
    }

    /**
     * Busca veículo por placa
     */
    public Veiculo buscarPorPlaca(String placa) {
        return veiculoRepository.findByPlaca(placa)
                .orElseThrow(() -> new RuntimeException("Veículo não encontrado com placa: " + placa));
    }

    /**
     * Registra um novo veículo no sistema
     * 
     * FIXME: This method does too many things
     * FIXME: Validation logic is mixed with business logic
     * FIXME: No proper error handling
     */
    public Veiculo registrarVeiculo(String placa, String modelo, Integer anoFabricacao) {
        // Validação misturada com lógica de negócio
        if (placa == null || placa.isEmpty()) {
            throw new RuntimeException("Placa é obrigatória"); // FIXME: Use proper exception
        }

        if (placa.length() != 7) {
            throw new RuntimeException("Placa deve ter 7 caracteres"); // FIXME: Use proper exception
        }

        // Verifica duplicidade
        Optional<Veiculo> existente = veiculoRepository.findByPlaca(placa);
        if (existente.isPresent()) {
            throw new RuntimeException("Veículo com placa " + placa + " já existe"); // FIXME: Use proper exception
        }

        // Validação do modelo
        if (modelo == null || modelo.isEmpty()) {
            throw new RuntimeException("Modelo é obrigatório"); // FIXME: Use proper exception
        }

        // Validação do ano
        if (anoFabricacao == null || anoFabricacao < 1900 || anoFabricacao > 2100) {
            throw new RuntimeException("Ano de fabricação inválido"); // FIXME: Use proper exception
        }

        // Criar veículo
        Veiculo veiculo = new Veiculo();
        veiculo.setPlaca(placa.toUpperCase());
        veiculo.setModelo(modelo);
        veiculo.setAnoFabricacao(anoFabricacao);
        veiculo.setStatus("ATIVO");
        veiculo.setQuilometragem(0L);
        veiculo.setDataCadastro(LocalDateTime.now());
        veiculo.setDataAtualizacao(LocalDateTime.now());

        Veiculo salvo = veiculoRepository.save(veiculo);
        log.info("Veículo registrado: {} - {}", salvo.getPlaca(), salvo.getModelo());

        // Notificar api-notificacoes sobre novo veículo
        try {
            notificacaoClient.enviarNotificacao(
                "NOVO_VEICULO",
                "gestor@rotalog.com",
                "Novo veículo cadastrado: " + salvo.getPlaca() + " - " + salvo.getModelo()
            );
        } catch (Exception e) {
            // FIXME: Engolindo exceção - se a notificação falhar, o veículo já foi salvo
            log.error("Erro ao enviar notificação de novo veículo: {}", e.getMessage());
            System.out.println("WARN: Falha ao notificar sobre novo veículo"); // FIXME: misturando System.out com logger
        }

        return salvo;
    }

    /**
     * Atualiza dados do veículo
     * 
     * FIXME: Atualiza todos os campos sem verificar quais mudaram
     * FIXME: Sem validação de campos individuais
     */
    public Veiculo atualizarVeiculo(Long id, String modelo, Integer anoFabricacao, Long quilometragem) {
        Veiculo veiculo = buscarPorId(id);

        if (modelo != null && !modelo.isEmpty()) {
            veiculo.setModelo(modelo);
        }
        if (anoFabricacao != null) {
            veiculo.setAnoFabricacao(anoFabricacao);
        }
        if (quilometragem != null) {
            if (quilometragem < veiculo.getQuilometragem()) {
                // FIXME: deveria lançar exceção, não apenas logar
                log.warn("Tentativa de reduzir quilometragem do veículo {}: {} -> {}", 
                    id, veiculo.getQuilometragem(), quilometragem);
            }
            veiculo.setQuilometragem(quilometragem);
        }

        veiculo.setDataAtualizacao(LocalDateTime.now());
        return veiculoRepository.save(veiculo);
    }

    /**
     * Atualiza quilometragem do veículo
     * 
     * FIXME: Mixing concerns - this should be in a separate service
     * FIXME: No audit trail
     */
    public Veiculo atualizarQuilometragem(Long veiculoId, Long novaQuilometragem) {
        Veiculo veiculo = buscarPorId(veiculoId);

        if (novaQuilometragem < 0) {
            throw new RuntimeException("Quilometragem não pode ser negativa"); // FIXME: Use proper exception
        }

        if (novaQuilometragem < veiculo.getQuilometragem()) {
            System.out.println("AVISO: Quilometragem menor que a atual!"); // FIXME: Use logger
        }

        veiculo.setQuilometragem(novaQuilometragem);
        veiculo.setDataAtualizacao(LocalDateTime.now());
        
        Veiculo atualizado = veiculoRepository.save(veiculo);

        // Verificar se precisa de manutenção preventiva
        if (precisaDeManutencao(veiculoId)) {
            try {
                notificacaoClient.enviarNotificacao(
                    "ALERTA_MANUTENCAO",
                    "gestor@rotalog.com",
                    "Veículo " + veiculo.getPlaca() + " atingiu " + novaQuilometragem + " km. Agendar manutenção preventiva."
                );
            } catch (Exception e) {
                log.error("Falha ao enviar alerta de manutenção: {}", e.getMessage());
            }
        }

        return atualizado;
    }

    /**
     * Obtém veículos por status
     * 
     * FIXME: Hardcoded status values
     * FIXME: No pagination
     */
    public List<Veiculo> obterVeiculosPorStatus(String status) {
        if (!status.equals("ATIVO") && !status.equals("INATIVO") && !status.equals("MANUTENCAO")) {
            throw new RuntimeException("Status inválido: " + status); // FIXME: Use proper exception
        }

        return veiculoRepository.findByStatus(status);
    }

    /**
     * Agenda manutenção preventiva
     * 
     * FIXME: Complex business logic mixed with infrastructure concerns
     * FIXME: Hardcoded maintenance intervals
     */
    public void agendarManutencaoPreventiva(Long veiculoId, Long quilometragemLimite) {
        Veiculo veiculo = buscarPorId(veiculoId);

        // FIXME: Hardcoded intervals
        Long intervaloQuilometragem = 10000L;
        Integer intervaloMeses = 3;

        log.info("Manutenção preventiva agendada para veículo {} em {} km", 
            veiculo.getPlaca(), quilometragemLimite);

        // Notificar sobre agendamento
        try {
            notificacaoClient.enviarNotificacao(
                "MANUTENCAO_AGENDADA",
                "gestor@rotalog.com",
                "Manutenção preventiva agendada para veículo " + veiculo.getPlaca() + " em " + quilometragemLimite + " km"
            );
        } catch (Exception e) {
            log.error("Falha ao notificar agendamento de manutenção: {}", e.getMessage());
        }
    }

    /**
     * Calcula custo de manutenção
     * 
     * FIXME: Business logic hardcoded
     * FIXME: No configuration management
     */
    public Double calcularCustoManutencao(String modelo, Long quilometragem) {
        // FIXME: Hardcoded costs
        Double custoPorKm = 0.05;
        Double custoBase = 500.0;

        return custoBase + (quilometragem * custoPorKm);
    }

    /**
     * Verifica se veículo precisa de manutenção
     * 
     * FIXME: Hardcoded thresholds
     */
    public Boolean precisaDeManutencao(Long veiculoId) {
        Veiculo veiculo = buscarPorId(veiculoId);

        // FIXME: Hardcoded threshold
        Long limiteQuilometragem = 50000L;

        return veiculo.getQuilometragem() != null && veiculo.getQuilometragem() >= limiteQuilometragem;
    }

    /**
     * Desativa veículo
     * 
     * FIXME: No soft delete
     * FIXME: No cascade handling
     */
    public Veiculo desativarVeiculo(Long veiculoId) {
        Veiculo veiculo = buscarPorId(veiculoId);
        veiculo.setStatus("INATIVO");
        veiculo.setDataAtualizacao(LocalDateTime.now());

        Veiculo desativado = veiculoRepository.save(veiculo);
        log.info("Veículo desativado: {}", veiculo.getPlaca());

        // Notificar sobre desativação
        try {
            notificacaoClient.enviarNotificacao(
                "VEICULO_DESATIVADO",
                "gestor@rotalog.com",
                "Veículo " + veiculo.getPlaca() + " foi desativado"
            );
        } catch (Exception e) {
            log.error("Falha ao notificar desativação: {}", e.getMessage());
        }

        return desativado;
    }

    /**
     * Reativa veículo
     */
    public Veiculo reativarVeiculo(Long veiculoId) {
        Veiculo veiculo = buscarPorId(veiculoId);
        veiculo.setStatus("ATIVO");
        veiculo.setDataAtualizacao(LocalDateTime.now());

        log.info("Veículo reativado: {}", veiculo.getPlaca());
        return veiculoRepository.save(veiculo);
    }

    /**
     * Obtém estatísticas de frota
     * 
     * FIXME: Typo in method name (Freita instead of Frota)
     * FIXME: No proper response object
     */
    public String obterEstatisticasFreita() {
        // FIXME: Typo no nome do método nunca foi corrigido
        long totalVeiculos = veiculoRepository.count();
        long ativos = veiculoRepository.findByStatus("ATIVO").size(); // FIXME: Deveria ser count query
        long inativos = veiculoRepository.findByStatus("INATIVO").size();
        long emManutencao = veiculoRepository.findByStatus("MANUTENCAO").size();

        // FIXME: Retornando JSON como String em vez de usar DTO
        return String.format(
            "{\"total\": %d, \"ativos\": %d, \"inativos\": %d, \"em_manutencao\": %d}",
            totalVeiculos, ativos, inativos, emManutencao
        );
    }

    /**
     * Sincroniza dados com sistema externo
     * 
     * FIXME: No retry logic
     * FIXME: No circuit breaker
     */
    public void sincronizarComSistemaExterno() {
        // FIXME: URL hardcoded
        log.info("Sincronização com sistema externo iniciada");
        System.out.println("Sincronização iniciada"); // FIXME: Use logger

        // TODO: Implementar integração real
        // TODO: Adicionar retry logic
        // TODO: Adicionar circuit breaker
    }

    // TODO: Add proper exception handling
    // TODO: Add metrics
    // TODO: Add caching
    // TODO: Add authorization checks
    // TODO: Add audit trail
}
