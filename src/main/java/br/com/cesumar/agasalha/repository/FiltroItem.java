package br.com.cesumar.agasalha.repository;

import br.com.cesumar.agasalha.model.Genero;
import br.com.cesumar.agasalha.model.StatusItem;
import br.com.cesumar.agasalha.model.Tamanho;
import br.com.cesumar.agasalha.model.TipoPeca;

public record FiltroItem(Tamanho tamanho, TipoPeca tipoPeca, Genero genero, StatusItem status) {

    public static FiltroItem vazio() {
        return new FiltroItem(null, null, null, null);
    }

    public boolean temAlgumCriterio() {
        return tamanho != null || tipoPeca != null || genero != null || status != null;
    }
}
