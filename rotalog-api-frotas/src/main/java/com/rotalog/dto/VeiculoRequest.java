package com.rotalog.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO para criação/atualização de veículo
 * 
 * FIXME: Sem validação com Bean Validation (@NotNull, @Size, etc)
 * FIXME: Sem documentação dos campos
 */
@Getter
@Setter
public class VeiculoRequest {
    private String placa;
    private String modelo;
    private Integer anoFabricacao;
    private Long quilometragem;

    // FIXME: Sem @NotNull, @NotBlank, @Size
    // FIXME: Sem validação customizada de placa
}
