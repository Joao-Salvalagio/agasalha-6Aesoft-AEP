package br.com.cesumar.agasalha.mapper;

import br.com.cesumar.agasalha.controller.dto.ItemDoacaoRequest;
import br.com.cesumar.agasalha.controller.dto.ItemResponse;
import br.com.cesumar.agasalha.model.EstadoConservacao;
import br.com.cesumar.agasalha.model.Genero;
import br.com.cesumar.agasalha.model.ItemAgasalho;
import br.com.cesumar.agasalha.model.StatusItem;
import br.com.cesumar.agasalha.model.Tamanho;
import br.com.cesumar.agasalha.model.TipoPeca;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ItemMapperDoacaoTest {

    private final ItemMapper mapper = new ItemMapper();

    @Test
    void paraModelDeDoacao_vinculaDoacaoEDeixaDadosDoDoadorVazios() {
        ItemAgasalho item = mapper.paraModel(new ItemDoacaoRequest(TipoPeca.CASACO, Tamanho.M,
                Genero.UNISSEX, EstadoConservacao.USADO_BOM), "o1");

        assertEquals("o1", item.getDoacaoId());
        assertEquals(StatusItem.DISPONIVEL, item.getStatus());
        assertNull(item.getNomeDoador());
        assertNull(item.getContatoDoador());
    }

    @Test
    void paraResponse_incluiReferenciasDeDoacaoEAbrigo() {
        ItemAgasalho item = new ItemAgasalho(TipoPeca.CASACO, Tamanho.M, Genero.UNISSEX,
                EstadoConservacao.USADO_BOM, null, null);
        item.setDoacaoId("o1");
        item.setAbrigoId("a1");

        ItemResponse resposta = mapper.paraResponse(item);

        assertEquals("o1", resposta.doacaoId());
        assertEquals("a1", resposta.abrigoId());
    }
}
