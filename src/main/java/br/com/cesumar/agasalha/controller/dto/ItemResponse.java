package br.com.cesumar.agasalha.controller.dto;

import br.com.cesumar.agasalha.model.EstadoConservacao;
import br.com.cesumar.agasalha.model.Genero;
import br.com.cesumar.agasalha.model.StatusItem;
import br.com.cesumar.agasalha.model.Tamanho;
import br.com.cesumar.agasalha.model.TipoPeca;

import java.time.Instant;

public record ItemResponse(
        String id,
        TipoPeca tipoPeca,
        Tamanho tamanho,
        Genero genero,
        EstadoConservacao estadoConservacao,
        String nomeDoador,
        String contatoDoador,
        StatusItem status,
        Instant dataCadastro) {
}
