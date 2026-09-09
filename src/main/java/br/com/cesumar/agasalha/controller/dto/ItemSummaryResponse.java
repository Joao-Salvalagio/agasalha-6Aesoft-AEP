package br.com.cesumar.agasalha.controller.dto;

import br.com.cesumar.agasalha.model.Genero;
import br.com.cesumar.agasalha.model.StatusItem;
import br.com.cesumar.agasalha.model.Tamanho;
import br.com.cesumar.agasalha.model.TipoPeca;

public record ItemSummaryResponse(
        String id,
        TipoPeca tipoPeca,
        Tamanho tamanho,
        Genero genero,
        StatusItem status) {
}
