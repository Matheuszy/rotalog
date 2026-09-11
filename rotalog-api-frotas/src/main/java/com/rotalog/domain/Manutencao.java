package com.rotalog.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Manutencao entity
 * 
 * FIXME: Sem FK constraint no banco (veiculo_id é só BIGINT)
 * FIXME: Sem cascade operations
 * FIXME: Sem índices para queries frequentes
 */
@Entity
@Table(name = "manutencoes")
@Getter
@Setter
@NoArgsConstructor
public class Manutencao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "veiculo_id")
    private Long veiculoId; // FIXME: deveria ser @ManyToOne com Veiculo

    @Column(name = "tipo_manutencao")
    private String tipoManutencao; // PREVENTIVA, CORRETIVA, REVISAO - FIXME: deveria ser enum

    @Column(name = "data_manutencao")
    private LocalDateTime dataManutencao;

    @Column(name = "quilometragem_manutencao")
    private Long quilometragemManutencao;

    @Column(name = "custo")
    private BigDecimal custo;

    @Column(name = "descricao")
    private String descricao;

    @Column(name = "status")
    private String status; // PENDENTE, EM_ANDAMENTO, CONCLUIDA, CANCELADA - FIXME: deveria ser enum

    @Column(name = "data_criacao")
    private LocalDateTime dataCriacao;

    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;

    // FIXME: Sem @ManyToOne para Veiculo
    // FIXME: Sem validação de custo negativo
    // FIXME: Sem histórico de alterações
}
