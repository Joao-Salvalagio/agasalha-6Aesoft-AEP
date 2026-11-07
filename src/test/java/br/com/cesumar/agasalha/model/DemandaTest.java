package br.com.cesumar.agasalha.model;

import br.com.cesumar.agasalha.exception.DadosInvalidosException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DemandaTest {

    private Demanda valida() {
        return new Demanda(TipoPeca.CASACO, Tamanho.M, Genero.UNISSEX, 10, 4);
    }

    @ParameterizedTest
    @CsvSource({
            "10, 0, 10",
            "10, 4, 6",
            "10, 10, 0",
            "1, 0, 1"
    })
    void deficit_necessariaMenosAtendida_retornaDiferenca(int necessaria, int atendida, int esperado) {
        Demanda demanda = new Demanda(TipoPeca.CASACO, Tamanho.M, Genero.UNISSEX, necessaria, atendida);

        assertEquals(esperado, demanda.deficit());
    }

    @Test
    void emAberto_deficitPositivo_retornaVerdadeiro() {
        assertTrue(valida().emAberto());
    }

    @Test
    void emAberto_deficitZero_retornaFalso() {
        Demanda demanda = new Demanda(TipoPeca.CASACO, Tamanho.M, Genero.UNISSEX, 5, 5);

        assertFalse(demanda.emAberto());
    }

    @Test
    void validar_demandaValida_naoLanca() {
        assertDoesNotThrow(() -> valida().validar());
    }

    @Test
    void validar_atendidaIgualANecessaria_naoLanca() {
        Demanda demanda = new Demanda(TipoPeca.CASACO, Tamanho.M, Genero.UNISSEX, 5, 5);

        assertDoesNotThrow(demanda::validar);
    }

    @Test
    void validar_tipoPecaNulo_lancaDadosInvalidos() {
        Demanda demanda = valida();
        demanda.setTipoPeca(null);

        assertThrows(DadosInvalidosException.class, demanda::validar);
    }

    @Test
    void validar_tamanhoNulo_lancaDadosInvalidos() {
        Demanda demanda = valida();
        demanda.setTamanho(null);

        assertThrows(DadosInvalidosException.class, demanda::validar);
    }

    @Test
    void validar_generoNulo_lancaDadosInvalidos() {
        Demanda demanda = valida();
        demanda.setGenero(null);

        assertThrows(DadosInvalidosException.class, demanda::validar);
    }

    @ParameterizedTest
    @CsvSource({
            "0, 0",
            "-1, 0",
            "5, -1",
            "5, 6"
    })
    void validar_quantidadesInvalidas_lancaDadosInvalidos(int necessaria, int atendida) {
        Demanda demanda = new Demanda(TipoPeca.CASACO, Tamanho.M, Genero.UNISSEX, necessaria, atendida);

        assertThrows(DadosInvalidosException.class, demanda::validar);
    }
}
