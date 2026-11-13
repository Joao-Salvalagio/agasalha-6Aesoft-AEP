
package br.com.cesumar.agasalha.controller.dto;

import br.com.cesumar.agasalha.model.Genero;
import br.com.cesumar.agasalha.model.Tamanho;
import br.com.cesumar.agasalha.model.TipoPeca;

public record DemandaResponse(
        int posicao,
        TipoPeca tipoPeca,
        Tamanho tamanho,
        Genero genero,
        int quantidadeNecessaria,
        int quantidadeAtendida,
        int deficit) {
}
