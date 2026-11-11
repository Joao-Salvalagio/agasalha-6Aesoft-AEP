
package br.com.cesumar.agasalha.controller.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.List;

public record AbrigoCreateRequest(
        @NotBlank String nome,
        @NotNull @Valid EnderecoRequest endereco,
        @NotNull @Positive Integer capacidadeEstoque,
        @PositiveOrZero Integer estoqueAtual,
        @Valid List<DemandaCreateRequest> demandas) {

    public AbrigoCreateRequest {
        if (estoqueAtual == null) {
            estoqueAtual = 0;
        }
        if (demandas == null) {
            demandas = List.of();
        }
    }
}
