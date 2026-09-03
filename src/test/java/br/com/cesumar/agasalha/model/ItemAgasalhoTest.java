package br.com.cesumar.agasalha.model;

import br.com.cesumar.agasalha.exception.DadosInvalidosException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ItemAgasalhoTest {

    private ItemAgasalho valido() {
        return new ItemAgasalho(TipoPeca.CASACO, Tamanho.M, Genero.UNISSEX,
                EstadoConservacao.USADO_BOM, "Ana", "ana@exemplo.com");
    }

    @Test
    void construtor_setaStatusDisponivelEDataCadastro() {
        ItemAgasalho item = valido();
        assertEquals(StatusItem.DISPONIVEL, item.getStatus());
        assertNotNull(item.getDataCadastro());
    }

    @Test
    void validar_itemCompleto_naoLanca() {
        assertDoesNotThrow(() -> valido().validar());
    }

    @Test
    void validar_tipoPecaNulo_lanca() {
        ItemAgasalho i = valido();
        i.setTipoPeca(null);
        assertThrows(DadosInvalidosException.class, i::validar);
    }

    @Test
    void validar_tamanhoNulo_lanca() {
        ItemAgasalho i = valido();
        i.setTamanho(null);
        assertThrows(DadosInvalidosException.class, i::validar);
    }

    @Test
    void validar_generoNulo_lanca() {
        ItemAgasalho i = valido();
        i.setGenero(null);
        assertThrows(DadosInvalidosException.class, i::validar);
    }

    @Test
    void validar_estadoConservacaoNulo_lanca() {
        ItemAgasalho i = valido();
        i.setEstadoConservacao(null);
        assertThrows(DadosInvalidosException.class, i::validar);
    }

    @Test
    void validar_nomeDoadorEmBranco_lanca() {
        ItemAgasalho i = valido();
        i.setNomeDoador("   ");
        assertThrows(DadosInvalidosException.class, i::validar);
    }

    @Test
    void validar_contatoDoadorNulo_lanca() {
        ItemAgasalho i = valido();
        i.setContatoDoador(null);
        assertThrows(DadosInvalidosException.class, i::validar);
    }

    @Test
    void validar_statusNulo_lanca() {
        ItemAgasalho i = valido();
        i.setStatus(null);
        assertThrows(DadosInvalidosException.class, i::validar);
    }

    @Test
    void validar_dataCadastroNulo_lanca() {
        ItemAgasalho i = valido();
        i.setDataCadastro(null);
        assertThrows(DadosInvalidosException.class, i::validar);
    }
}
