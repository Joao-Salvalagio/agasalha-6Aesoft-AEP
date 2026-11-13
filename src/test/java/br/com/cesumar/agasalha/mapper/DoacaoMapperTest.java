package br.com.cesumar.agasalha.mapper;

import br.com.cesumar.agasalha.controller.dto.DoacaoCreateRequest;
import br.com.cesumar.agasalha.controller.dto.DoacaoResponse;
import br.com.cesumar.agasalha.model.Doacao;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DoacaoMapperTest {

    private final DoacaoMapper mapper = new DoacaoMapper();

    @Test
    void paraModel_guardaApenasOsIdentificadoresEDefineData() {
        Doacao doacao = mapper.paraModel(new DoacaoCreateRequest("d1", "a1"));

        assertEquals("d1", doacao.getDoadorId());
        assertEquals("a1", doacao.getAbrigoId());
        assertNotNull(doacao.getDataDoacao());
    }

    @Test
    void paraResponse_copiaTodosOsCampos() {
        Doacao doacao = new Doacao("d1", "a1");
        doacao.setId("o1");

        DoacaoResponse resposta = mapper.paraResponse(doacao);

        assertEquals("o1", resposta.id());
        assertEquals("d1", resposta.doadorId());
        assertEquals("a1", resposta.abrigoId());
        assertEquals(doacao.getDataDoacao(), resposta.dataDoacao());
    }
}
