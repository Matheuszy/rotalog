package com.rotalog.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Veiculo entity - Legacy code with TABLE_PER_CLASS inheritance
 * This is intentional technical debt for the course
 * 
 * FIXME: TABLE_PER_CLASS is a bad strategy for this use case
 * FIXME: No Builder pattern
 * FIXME: Using Lombok only partially (some getters/setters still manual)
 */
@Entity
@Table(name = "veiculos")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Getter
@Setter
@NoArgsConstructor
public class Veiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "placa", nullable = false, unique = true)
    private String placa;

    @Column(name = "modelo")
    private String modelo;

    @Column(name = "ano_fabricacao")
    private Integer anoFabricacao;

    @Column(name = "quilometragem")
    private Long quilometragem;

    @Column(name = "status")
    private String status; // ATIVO, INATIVO, MANUTENCAO - FIXME: deveria ser enum

    @Column(name = "data_cadastro")
    private LocalDateTime dataCadastro;

    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;

    // FIXME: Sem @PrePersist e @PreUpdate para datas automáticas
    // FIXME: Sem toString, equals, hashCode
    // FIXME: Status deveria ser enum
}
