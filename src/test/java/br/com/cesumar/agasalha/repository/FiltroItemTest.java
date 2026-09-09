package br.com.cesumar.agasalha.repository;

import br.com.cesumar.agasalha.model.Tamanho;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FiltroItemTest {

    @Test
    void vazio_naoTemCriterio() {
        assertFalse(FiltroItem.vazio().temAlgumCriterio());
    }

    @Test
    void comUmCampo_temCriterio() {
        assertTrue(new FiltroItem(Tamanho.M, null, null, null).temAlgumCriterio());
    }
}
