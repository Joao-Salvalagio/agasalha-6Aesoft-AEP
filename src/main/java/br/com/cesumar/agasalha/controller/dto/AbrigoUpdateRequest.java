
package br.com.cesumar.agasalha.controller.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record AbrigoUpdateRequest(
        @NotBlank String nome,
        @NotNull @Valid EnderecoRequest endereco,
        @NotNull @Positive Integer capacidadeEstoque,
        @NotNull @PositiveOrZero Integer estoqueAtual) {
}
