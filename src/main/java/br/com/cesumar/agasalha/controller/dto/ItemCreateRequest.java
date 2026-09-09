package br.com.cesumar.agasalha.controller.dto;

import br.com.cesumar.agasalha.model.EstadoConservacao;
import br.com.cesumar.agasalha.model.Genero;
import br.com.cesumar.agasalha.model.Tamanho;
import br.com.cesumar.agasalha.model.TipoPeca;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ItemCreateRequest(
        @NotNull TipoPeca tipoPeca,
        @NotNull Tamanho tamanho,
        @NotNull Genero genero,
        @NotNull EstadoConservacao estadoConservacao,
        @NotBlank String nomeDoador,
        @NotBlank String contatoDoador) {
}
