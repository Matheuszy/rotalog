package com.rotalog.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * DTO para criação/atualização de manutenção
 * 
 * FIXME: Sem Bean Validation
 */
@Getter
@Setter
public class ManutencaoRequest {
    private Long veiculoId;
    private String tipoManutencao;
    private String descricao;
    private BigDecimal custoEstimado;

    // FIXME: Sem @NotNull
    // FIXME: Sem validação de tipo de manutenção
}
