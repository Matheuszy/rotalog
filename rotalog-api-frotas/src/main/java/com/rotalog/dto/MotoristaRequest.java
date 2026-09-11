package com.rotalog.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * DTO para criação/atualização de motorista
 * 
 * FIXME: Sem Bean Validation
 */
@Getter
@Setter
public class MotoristaRequest {
    private String nome;
    private String cnh;
    private String categoriaCnh;
    private LocalDate vencimentoCnh;

    // FIXME: Sem @NotNull, @NotBlank
    // FIXME: Sem validação de formato de CNH
}
