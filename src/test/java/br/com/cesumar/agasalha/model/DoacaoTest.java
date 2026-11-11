package br.com.cesumar.agasalha.model;

import br.com.cesumar.agasalha.exception.DadosInvalidosException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DoacaoTest {

    @Test
    void construtor_defineADataDaDoacao() {
        assertNotNull(new Doacao("doador1", "abrigo1").getDataDoacao());
    }

    @Test
    void validar_doacaoCompleta_naoLanca() {
        assertDoesNotThrow(() -> new Doacao("doador1", "abrigo1").validar());
    }

    @Test
    void validar_doadorIdEmBranco_lanca() {
        assertThrows(DadosInvalidosException.class, () -> new Doacao(" ", "abrigo1").validar());
    }

    @Test
    void validar_doadorIdNulo_lanca() {
        assertThrows(DadosInvalidosException.class, () -> new Doacao(null, "abrigo1").validar());
    }

    @Test
    void validar_abrigoIdEmBranco_lanca() {
        assertThrows(DadosInvalidosException.class, () -> new Doacao("doador1", "").validar());
    }

    @Test
    void validar_abrigoIdNulo_lanca() {
        assertThrows(DadosInvalidosException.class, () -> new Doacao("doador1", null).validar());
    }

    @Test
    void validar_dataDaDoacaoNula_lanca() {
        Doacao doacao = new Doacao("doador1", "abrigo1");
        doacao.setDataDoacao(null);
        assertThrows(DadosInvalidosException.class, doacao::validar);
    }
}
