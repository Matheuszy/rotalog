package com.rotalog.service;

import com.rotalog.domain.Motorista;
import com.rotalog.repository.MotoristaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * MotoristaService - Serviço de gerenciamento de motoristas
 * 
 * FIXME: Sem validação de CNH real
 * FIXME: Sem integração com DETRAN
 * FIXME: Sem controle de disponibilidade
 */
@Slf4j
@Service
public class MotoristaService {

    @Autowired // FIXME: deveria usar injeção por construtor
    private MotoristaRepository motoristaRepository;

    @Autowired
    private NotificacaoClient notificacaoClient;

    public List<Motorista> listarTodos() {
        return motoristaRepository.findAll(); // FIXME: sem paginação
    }

    public Motorista buscarPorId(Long id) {
        return motoristaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Motorista não encontrado: " + id));
    }

    public Motorista buscarPorCnh(String cnh) {
        return motoristaRepository.findByCnh(cnh)
                .orElseThrow(() -> new RuntimeException("Motorista não encontrado com CNH: " + cnh));
    }

    /**
     * Cadastra novo motorista
     * 
     * FIXME: Validação de CNH é apenas tamanho
     * FIXME: Sem verificação de duplicidade por nome
     */
    public Motorista cadastrarMotorista(String nome, String cnh, String categoriaCnh, LocalDate vencimentoCnh) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new RuntimeException("Nome é obrigatório");
        }

        if (cnh == null || cnh.length() != 11) {
            throw new RuntimeException("CNH deve ter 11 dígitos"); // FIXME: validação fraca
        }

        // Verificar duplicidade
        if (motoristaRepository.findByCnh(cnh).isPresent()) {
            throw new RuntimeException("Motorista com CNH " + cnh + " já existe");
        }

        // FIXME: Sem verificação se CNH está vencida no momento do cadastro
        if (vencimentoCnh != null && vencimentoCnh.isBefore(LocalDate.now())) {
            log.warn("Cadastrando motorista com CNH vencida: {}", cnh); // apenas log, não impede
        }

        Motorista motorista = new Motorista();
        motorista.setNome(nome);
        motorista.setCnh(cnh);
        motorista.setCategoriaCnh(categoriaCnh != null ? categoriaCnh : "B"); // FIXME: default hardcoded
        motorista.setVencimentoCnh(vencimentoCnh);
        motorista.setStatus("ATIVO");
        motorista.setDataCadastro(LocalDateTime.now());
        motorista.setDataAtualizacao(LocalDateTime.now());

        Motorista salvo = motoristaRepository.save(motorista);
        log.info("Motorista cadastrado: {} - CNH: {}", salvo.getNome(), salvo.getCnh());

        try {
            notificacaoClient.enviarNotificacao(
                "NOVO_MOTORISTA",
                "rh@rotalog.com",
                "Novo motorista cadastrado: " + salvo.getNome()
            );
        } catch (Exception e) {
            log.error("Falha ao notificar cadastro de motorista: {}", e.getMessage());
        }

        return salvo;
    }

    /**
     * Atualiza dados do motorista
     */
    public Motorista atualizarMotorista(Long id, String nome, String categoriaCnh, LocalDate vencimentoCnh) {
        Motorista motorista = buscarPorId(id);

        if (nome != null && !nome.trim().isEmpty()) {
            motorista.setNome(nome);
        }
        if (categoriaCnh != null) {
            motorista.setCategoriaCnh(categoriaCnh);
        }
        if (vencimentoCnh != null) {
            motorista.setVencimentoCnh(vencimentoCnh);
        }

        motorista.setDataAtualizacao(LocalDateTime.now());
        return motoristaRepository.save(motorista);
    }

    /**
     * Lista motoristas por status
     */
    public List<Motorista> listarPorStatus(String status) {
        return motoristaRepository.findByStatus(status);
    }

    /**
     * Lista motoristas com CNH vencida
     * 
     * FIXME: Deveria enviar notificação automática
     */
    public List<Motorista> listarComCnhVencida() {
        List<Motorista> vencidos = motoristaRepository.findMotoristasComCnhVencida();
        
        // FIXME: Notificação em loop - pode gerar muitas chamadas HTTP
        for (Motorista m : vencidos) {
            try {
                notificacaoClient.enviarNotificacao(
                    "CNH_VENCIDA",
                    "rh@rotalog.com",
                    "Motorista " + m.getNome() + " está com CNH vencida desde " + m.getVencimentoCnh()
                );
            } catch (Exception e) {
                log.error("Falha ao notificar CNH vencida do motorista {}: {}", m.getNome(), e.getMessage());
            }
        }

        return vencidos;
    }

    /**
     * Desativa motorista
     */
    public Motorista desativarMotorista(Long id) {
        Motorista motorista = buscarPorId(id);
        motorista.setStatus("INATIVO");
        motorista.setDataAtualizacao(LocalDateTime.now());

        log.info("Motorista desativado: {}", motorista.getNome());
        return motoristaRepository.save(motorista);
    }

    /**
     * Reativa motorista
     */
    public Motorista reativarMotorista(Long id) {
        Motorista motorista = buscarPorId(id);
        motorista.setStatus("ATIVO");
        motorista.setDataAtualizacao(LocalDateTime.now());

        log.info("Motorista reativado: {}", motorista.getNome());
        return motoristaRepository.save(motorista);
    }
}
