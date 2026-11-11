package br.com.cesumar.agasalha.mapper;

import br.com.cesumar.agasalha.controller.dto.DoadorCreateRequest;
import br.com.cesumar.agasalha.controller.dto.DoadorResponse;
import br.com.cesumar.agasalha.controller.dto.DoadorUpdateRequest;
import br.com.cesumar.agasalha.model.Doador;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DoadorMapperTest {

    private final DoadorMapper mapper = new DoadorMapper();

    @Test
    void paraModel_copiaNomeEContato() {
        Doador doador = mapper.paraModel(new DoadorCreateRequest("Ana", "ana@exemplo.com"));

        assertEquals("Ana", doador.getNome());
        assertEquals("ana@exemplo.com", doador.getContato());
    }

    @Test
    void aplicar_atualizaCamposEPreservaId() {
        Doador alvo = new Doador("Ana", "ana@exemplo.com");
        alvo.setId("d1");

        mapper.aplicar(new DoadorUpdateRequest("Bia", "bia@exemplo.com"), alvo);

        assertEquals("d1", alvo.getId());
        assertEquals("Bia", alvo.getNome());
        assertEquals("bia@exemplo.com", alvo.getContato());
    }

    @Test
    void paraResponse_copiaTodosOsCampos() {
        Doador doador = new Doador("Ana", "ana@exemplo.com");
        doador.setId("d1");

        DoadorResponse resposta = mapper.paraResponse(doador);

        assertEquals("d1", resposta.id());
        assertEquals("Ana", resposta.nome());
        assertEquals("ana@exemplo.com", resposta.contato());
    }
}
