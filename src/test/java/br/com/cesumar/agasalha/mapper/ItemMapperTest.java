package br.com.cesumar.agasalha.mapper;

import br.com.cesumar.agasalha.controller.dto.ItemCreateRequest;
import br.com.cesumar.agasalha.controller.dto.ItemResponse;
import br.com.cesumar.agasalha.controller.dto.ItemSummaryResponse;
import br.com.cesumar.agasalha.controller.dto.ItemUpdateRequest;
import br.com.cesumar.agasalha.model.EstadoConservacao;
import br.com.cesumar.agasalha.model.Genero;
import br.com.cesumar.agasalha.model.ItemAgasalho;
import br.com.cesumar.agasalha.model.StatusItem;
import br.com.cesumar.agasalha.model.Tamanho;
import br.com.cesumar.agasalha.model.TipoPeca;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ItemMapperTest {

    private final ItemMapper mapper = new ItemMapper();

    @Test
    void paraModel_criaItemDisponivel() {
        ItemAgasalho item = mapper.paraModel(new ItemCreateRequest(TipoPeca.CASACO, Tamanho.M,
                Genero.UNISSEX, EstadoConservacao.USADO_BOM, "Ana", "ana@exemplo.com"));
        assertEquals(TipoPeca.CASACO, item.getTipoPeca());
        assertEquals(StatusItem.DISPONIVEL, item.getStatus());
    }

    @Test
    void aplicar_atualizaDescritivosEPreservaCicloDeVida() {
        ItemAgasalho alvo = new ItemAgasalho(TipoPeca.CASACO, Tamanho.M, Genero.UNISSEX,
                EstadoConservacao.USADO_BOM, "Ana", "ana@exemplo.com");
        alvo.setId("id1");
        alvo.setStatus(StatusItem.RESERVADO);
        Instant data = alvo.getDataCadastro();

        mapper.aplicar(new ItemUpdateRequest(TipoPeca.COBERTOR, Tamanho.G, Genero.MASCULINO,
                EstadoConservacao.NOVO, "Bia", "bia@exemplo.com"), alvo);

        assertEquals(TipoPeca.COBERTOR, alvo.getTipoPeca());
        assertEquals(Tamanho.G, alvo.getTamanho());
        assertEquals("Bia", alvo.getNomeDoador());
        assertEquals("id1", alvo.getId());
        assertEquals(StatusItem.RESERVADO, alvo.getStatus());
        assertEquals(data, alvo.getDataCadastro());
    }

    @Test
    void paraResponse_copiaTodosOsCampos() {
        ItemAgasalho item = new ItemAgasalho(TipoPeca.MEIA, Tamanho.P, Genero.FEMININO,
                EstadoConservacao.NOVO, "Ana", "ana@exemplo.com");
        item.setId("id9");
        ItemResponse r = mapper.paraResponse(item);
        assertEquals("id9", r.id());
        assertEquals(TipoPeca.MEIA, r.tipoPeca());
        assertEquals(Genero.FEMININO, r.genero());
        assertEquals(StatusItem.DISPONIVEL, r.status());
        assertEquals("ana@exemplo.com", r.contatoDoador());
    }

    @Test
    void paraSummary_exponeApenasResumo() {
        ItemAgasalho item = new ItemAgasalho(TipoPeca.BLUSA, Tamanho.GG, Genero.UNISSEX,
                EstadoConservacao.USADO_BOM, "Ana", "ana@exemplo.com");
        item.setId("id7");
        ItemSummaryResponse s = mapper.paraSummary(item);
        assertEquals("id7", s.id());
        assertEquals(TipoPeca.BLUSA, s.tipoPeca());
        assertEquals(Tamanho.GG, s.tamanho());
        assertEquals(StatusItem.DISPONIVEL, s.status());
    }
}
