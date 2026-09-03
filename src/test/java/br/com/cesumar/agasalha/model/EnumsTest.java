package br.com.cesumar.agasalha.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class EnumsTest {

    @Test
    void tipoPeca_temAsSeisConstantes() {
        assertArrayEquals(new TipoPeca[]{TipoPeca.CASACO, TipoPeca.BLUSA, TipoPeca.CALCA,
                TipoPeca.COBERTOR, TipoPeca.MEIA, TipoPeca.OUTRO}, TipoPeca.values());
    }

    @Test
    void tamanho_temCincoConstantesNaOrdem() {
        assertArrayEquals(new Tamanho[]{Tamanho.PP, Tamanho.P, Tamanho.M, Tamanho.G, Tamanho.GG},
                Tamanho.values());
    }

    @Test
    void genero_temTresConstantes() {
        assertEquals(3, Genero.values().length);
        assertEquals(Genero.UNISSEX, Genero.valueOf("UNISSEX"));
    }

    @Test
    void estadoConservacao_temTresConstantes() {
        assertArrayEquals(new EstadoConservacao[]{EstadoConservacao.NOVO, EstadoConservacao.USADO_BOM,
                EstadoConservacao.USADO_REGULAR}, EstadoConservacao.values());
    }

    @Test
    void statusItem_temTresConstantes() {
        assertArrayEquals(new StatusItem[]{StatusItem.DISPONIVEL, StatusItem.RESERVADO,
                StatusItem.ENTREGUE}, StatusItem.values());
    }

    @Test
    void acaoStatus_temDuasConstantes() {
        assertArrayEquals(new AcaoStatus[]{AcaoStatus.RESERVAR, AcaoStatus.ENTREGAR},
                AcaoStatus.values());
    }
}
