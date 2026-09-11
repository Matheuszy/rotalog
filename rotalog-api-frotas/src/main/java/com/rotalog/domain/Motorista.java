package com.rotalog.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Motorista entity
 * 
 * FIXME: Sem relacionamento bidirecional com Veiculo
 * FIXME: Sem validação de CNH
 * FIXME: Sem auditoria
 */
@Entity
@Table(name = "motoristas")
@Getter
@Setter
@NoArgsConstructor
public class Motorista {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome", nullable = false)
    private String nome;

    @Column(name = "cnh", nullable = false, unique = true)
    private String cnh;

    @Column(name = "categoria_cnh")
    private String categoriaCnh;

    @Column(name = "vencimento_cnh")
    private LocalDate vencimentoCnh;

    @Column(name = "status")
    private String status; // ATIVO, INATIVO, FERIAS - FIXME: deveria ser enum

    @Column(name = "data_cadastro")
    private LocalDateTime dataCadastro;

    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;

    // FIXME: Falta relacionamento com Veiculo (motorista pode estar vinculado a um veículo)
    // FIXME: Falta campo email e telefone na tabela
    // FIXME: Sem validação de formato de CNH
}
