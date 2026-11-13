package br.com.cesumar.agasalha.model;

import br.com.cesumar.agasalha.exception.DadosInvalidosException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ItemAgasalhoRelacionamentoTest {

    private ItemAgasalho item(String nomeDoador, String contatoDoador) {
        return new ItemAgasalho(TipoPeca.CASACO, Tamanho.M, Genero.UNISSEX,
                EstadoConservacao.USADO_BOM, nomeDoador, contatoDoador);
    }

    @Test
    void validar_itemVinculadoADoacaoSemDadosDoDoador_naoLanca() {
        ItemAgasalho item = item(null, null);
        item.setDoacaoId("doacao1");

        assertDoesNotThrow(item::validar);
    }

    @Test
    void validar_itemSemDoacaoESemNomeDoador_lanca() {
        assertThrows(DadosInvalidosException.class, () -> item(null, "ana@exemplo.com").validar());
    }

    @Test
    void validar_itemSemDoacaoESemContatoDoador_lanca() {
        assertThrows(DadosInvalidosException.class, () -> item("Ana", " ").validar());
    }

    @Test
    void validar_itemVinculadoADoacaoComAbrigo_naoLanca() {
        ItemAgasalho item = item(null, null);
        item.setDoacaoId("doacao1");
        item.setAbrigoId("abrigo1");

        assertDoesNotThrow(item::validar);
    }

    @Test
    void validar_itemVinculadoADoacaoMasSemTipoPeca_lanca() {
        ItemAgasalho item = item(null, null);
        item.setDoacaoId("doacao1");
        item.setTipoPeca(null);

        assertThrows(DadosInvalidosException.class, item::validar);
    }
}
