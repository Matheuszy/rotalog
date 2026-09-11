package com.rotalog.service;

import com.rotalog.domain.Manutencao;
import com.rotalog.domain.Veiculo;
import com.rotalog.repository.ManutencaoRepository;
import com.rotalog.repository.VeiculoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * ManutencaoService - Serviço de gerenciamento de manutenções
 * 
 * FIXME: Lógica de negócio misturada com infraestrutura
 * FIXME: Sem transações explícitas
 * FIXME: Sem validação de estados
 */
@Slf4j
@Service
public class ManutencaoService {

    @Autowired // FIXME: deveria usar injeção por construtor
    private ManutencaoRepository manutencaoRepository;

    @Autowired
    private VeiculoRepository veiculoRepository;

    @Autowired
    private NotificacaoClient notificacaoClient;

    public List<Manutencao> listarTodas() {
        return manutencaoRepository.findAll(); // FIXME: sem paginação
    }

    public Manutencao buscarPorId(Long id) {
        return manutencaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Manutenção não encontrada: " + id));
    }

    public List<Manutencao> listarPorVeiculo(Long veiculoId) {
        return manutencaoRepository.findByVeiculoId(veiculoId);
    }

    /**
     * Agendar manutenção
     * 
     * FIXME: Sem validação se veículo já tem manutenção pendente
     * FIXME: Sem transação explícita
     * FIXME: Atualiza status do veículo diretamente (acoplamento)
     */
    public Manutencao agendarManutencao(Long veiculoId, String tipoManutencao, String descricao, BigDecimal custoEstimado) {
        // Verificar se veículo existe
        Veiculo veiculo = veiculoRepository.findById(veiculoId)
                .orElseThrow(() -> new RuntimeException("Veículo não encontrado: " + veiculoId));

        if (tipoManutencao == null || tipoManutencao.trim().isEmpty()) {
            throw new RuntimeException("Tipo de manutenção é obrigatório");
        }

        // FIXME: Sem validação se já existe manutenção pendente para o mesmo veículo
        Manutencao manutencao = new Manutencao();
        manutencao.setVeiculoId(veiculoId);
        manutencao.setTipoManutencao(tipoManutencao);
        manutencao.setDescricao(descricao);
        manutencao.setCusto(custoEstimado);
        manutencao.setQuilometragemManutencao(veiculo.getQuilometragem());
        manutencao.setStatus("PENDENTE");
        manutencao.setDataCriacao(LocalDateTime.now());
        manutencao.setDataAtualizacao(LocalDateTime.now());

        Manutencao salva = manutencaoRepository.save(manutencao);
        log.info("Manutenção agendada: veículo={}, tipo={}", veiculo.getPlaca(), tipoManutencao);

        // Notificar sobre agendamento
        try {
            notificacaoClient.enviarNotificacao(
                "MANUTENCAO_AGENDADA",
                "oficina@rotalog.com",
                "Manutenção " + tipoManutencao + " agendada para veículo " + veiculo.getPlaca()
            );
        } catch (Exception e) {
            log.error("Falha ao notificar agendamento: {}", e.getMessage());
        }

        return salva;
    }

    /**
     * Iniciar manutenção
     * 
     * FIXME: Sem validação de transição de estado
     * FIXME: Atualiza veículo sem transação
     */
    public Manutencao iniciarManutencao(Long manutencaoId) {
        Manutencao manutencao = buscarPorId(manutencaoId);

        // FIXME: Sem validação de transição de estado (poderia ir de CONCLUIDA para EM_ANDAMENTO)
        if (!"PENDENTE".equals(manutencao.getStatus())) {
            log.warn("Iniciando manutenção que não está PENDENTE: status={}", manutencao.getStatus());
            // FIXME: deveria lançar exceção, mas apenas loga
        }

        manutencao.setStatus("EM_ANDAMENTO");
        manutencao.setDataManutencao(LocalDateTime.now());
        manutencao.setDataAtualizacao(LocalDateTime.now());

        // FIXME: Atualiza status do veículo diretamente - acoplamento forte
        Veiculo veiculo = veiculoRepository.findById(manutencao.getVeiculoId())
                .orElse(null);
        if (veiculo != null) {
            veiculo.setStatus("MANUTENCAO");
            veiculo.setDataAtualizacao(LocalDateTime.now());
            veiculoRepository.save(veiculo); // FIXME: sem transação com a manutenção
        }

        return manutencaoRepository.save(manutencao);
    }

    /**
     * Concluir manutenção
     * 
     * FIXME: Sem validação de custo final
     * FIXME: Sem transação
     */
    public Manutencao concluirManutencao(Long manutencaoId, BigDecimal custoFinal) {
        Manutencao manutencao = buscarPorId(manutencaoId);

        manutencao.setStatus("CONCLUIDA");
        if (custoFinal != null) {
            manutencao.setCusto(custoFinal);
        }
        manutencao.setDataAtualizacao(LocalDateTime.now());

        // Reativar veículo
        Veiculo veiculo = veiculoRepository.findById(manutencao.getVeiculoId())
                .orElse(null);
        if (veiculo != null) {
            veiculo.setStatus("ATIVO");
            veiculo.setDataAtualizacao(LocalDateTime.now());
            veiculoRepository.save(veiculo); // FIXME: sem transação
        }

        Manutencao concluida = manutencaoRepository.save(manutencao);

        // Notificar conclusão
        try {
            String placaVeiculo = veiculo != null ? veiculo.getPlaca() : "N/A";
            notificacaoClient.enviarNotificacao(
                "MANUTENCAO_CONCLUIDA",
                "gestor@rotalog.com",
                "Manutenção concluída para veículo " + placaVeiculo + ". Custo: R$ " + custoFinal
            );
        } catch (Exception e) {
            log.error("Falha ao notificar conclusão de manutenção: {}", e.getMessage());
        }

        return concluida;
    }

    /**
     * Cancelar manutenção
     */
    public Manutencao cancelarManutencao(Long manutencaoId) {
        Manutencao manutencao = buscarPorId(manutencaoId);
        manutencao.setStatus("CANCELADA");
        manutencao.setDataAtualizacao(LocalDateTime.now());

        // Se veículo estava em manutenção, reativar
        Veiculo veiculo = veiculoRepository.findById(manutencao.getVeiculoId()).orElse(null);
        if (veiculo != null && "MANUTENCAO".equals(veiculo.getStatus())) {
            veiculo.setStatus("ATIVO");
            veiculo.setDataAtualizacao(LocalDateTime.now());
            veiculoRepository.save(veiculo);
        }

        log.info("Manutenção cancelada: id={}", manutencaoId);
        return manutencaoRepository.save(manutencao);
    }

    /**
     * Listar manutenções pendentes
     */
    public List<Manutencao> listarPendentes() {
        return manutencaoRepository.findByStatus("PENDENTE");
    }

    /**
     * Obter última manutenção de um veículo
     */
    public Manutencao obterUltimaManutencao(Long veiculoId) {
        Manutencao ultima = manutencaoRepository.findUltimaManutencao(veiculoId);
        if (ultima == null) {
            throw new RuntimeException("Nenhuma manutenção encontrada para veículo: " + veiculoId);
        }
        return ultima;
    }
}
