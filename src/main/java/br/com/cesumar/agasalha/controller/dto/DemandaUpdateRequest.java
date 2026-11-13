
package br.com.cesumar.agasalha.controller.dto;

import br.com.cesumar.agasalha.model.Genero;
import br.com.cesumar.agasalha.model.Tamanho;
import br.com.cesumar.agasalha.model.TipoPeca;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record DemandaUpdateRequest(
        @NotNull TipoPeca tipoPeca,
        @NotNull Tamanho tamanho,
        @NotNull Genero genero,
        @NotNull @Positive Integer quantidadeNecessaria,
        @NotNull @PositiveOrZero Integer quantidadeAtendida) {
}
