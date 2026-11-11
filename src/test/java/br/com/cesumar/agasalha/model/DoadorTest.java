package br.com.cesumar.agasalha.model;

import br.com.cesumar.agasalha.exception.DadosInvalidosException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DoadorTest {

    @Test
    void validar_doadorCompleto_naoLanca() {
        assertDoesNotThrow(() -> new Doador("Ana", "ana@exemplo.com").validar());
    }

    @Test
    void validar_nomeEmBranco_lanca() {
        assertThrows(DadosInvalidosException.class, () -> new Doador("  ", "ana@exemplo.com").validar());
    }

    @Test
    void validar_nomeNulo_lanca() {
        assertThrows(DadosInvalidosException.class, () -> new Doador(null, "ana@exemplo.com").validar());
    }

    @Test
    void validar_contatoEmBranco_lanca() {
        assertThrows(DadosInvalidosException.class, () -> new Doador("Ana", "").validar());
    }

    @Test
    void validar_contatoNulo_lanca() {
        assertThrows(DadosInvalidosException.class, () -> new Doador("Ana", null).validar());
    }
}
