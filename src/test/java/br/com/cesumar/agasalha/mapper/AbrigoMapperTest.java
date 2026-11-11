package br.com.cesumar.agasalha.mapper;

import br.com.cesumar.agasalha.controller.dto.AbrigoCreateRequest;
import br.com.cesumar.agasalha.controller.dto.AbrigoResponse;
import br.com.cesumar.agasalha.controller.dto.AbrigoUpdateRequest;
import br.com.cesumar.agasalha.controller.dto.DemandaCreateRequest;
import br.com.cesumar.agasalha.controller.dto.DemandaResponse;
import br.com.cesumar.agasalha.controller.dto.DemandaUpdateRequest;
import br.com.cesumar.agasalha.controller.dto.EnderecoRequest;
import br.com.cesumar.agasalha.model.Abrigo;
import br.com.cesumar.agasalha.model.Demanda;
import br.com.cesumar.agasalha.model.Endereco;
import br.com.cesumar.agasalha.model.Genero;
import br.com.cesumar.agasalha.model.Tamanho;
import br.com.cesumar.agasalha.model.TipoPeca;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class AbrigoMapperTest {

    private final AbrigoMapper mapper = new AbrigoMapper();

    private EnderecoRequest endereco() {
        return new EnderecoRequest("Rua A", "120", "Centro", "Maringa", "PR");
    }

    @Test
    void paraModel_copiaCamposEConverteDemandasAninhadas() {
        AbrigoCreateRequest req = new AbrigoCreateRequest("Abrigo Central", endereco(), 100, 20,
                List.of(new DemandaCreateRequest(TipoPeca.CASACO, Tamanho.M, Genero.UNISSEX, 30, 10)));

        Abrigo abrigo = mapper.paraModel(req);

        assertEquals("Abrigo Central", abrigo.getNome());
        assertEquals("Maringa", abrigo.getEndereco().cidade());
        assertEquals("120", abrigo.getEndereco().numero());
        assertEquals(100, abrigo.getCapacidadeEstoque());
        assertEquals(20, abrigo.getEstoqueAtual());
        assertEquals(1, abrigo.getDemandas().size());
        assertEquals(20, abrigo.getDemandas().get(0).deficit());
    }

    @Test
    void paraModel_semEstoqueEDemandasInformados_usaPadroes() {
        Abrigo abrigo = mapper.paraModel(new AbrigoCreateRequest("Abrigo", endereco(), 10, null, null));

        assertEquals(0, abrigo.getEstoqueAtual());
        assertEquals(0, abrigo.getDemandas().size());
    }

    @Test
    void paraModel_listaDeDemandasResultanteAceitaNovasDemandas() {
        Abrigo abrigo = mapper.paraModel(new AbrigoCreateRequest("Abrigo", endereco(), 10, 0, null));

        assertDoesNotThrow(() -> abrigo.adicionarDemanda(
                new Demanda(TipoPeca.CASACO, Tamanho.M, Genero.UNISSEX, 5, 0)));
    }

    @Test
    void aplicar_atualizaDadosEPreservaIdEDemandas() {
        Abrigo alvo = new Abrigo("Antigo", new Endereco("Rua B", "1", "Zona 7", "Londrina", "PR"), 5, 1);
        alvo.setId("a1");
        alvo.adicionarDemanda(new Demanda(TipoPeca.MEIA, Tamanho.P, Genero.FEMININO, 3, 0));

        mapper.aplicar(new AbrigoUpdateRequest("Novo", endereco(), 20, 4), alvo);

        assertEquals("a1", alvo.getId());
        assertEquals("Novo", alvo.getNome());
        assertEquals("Maringa", alvo.getEndereco().cidade());
        assertEquals(20, alvo.getCapacidadeEstoque());
        assertEquals(4, alvo.getEstoqueAtual());
        assertEquals(1, alvo.getDemandas().size());
    }

    @Test
    void paraDemanda_deCriacao_copiaCamposEQuantidades() {
        Demanda demanda = mapper.paraDemanda(
                new DemandaCreateRequest(TipoPeca.CASACO, Tamanho.M, Genero.UNISSEX, 5, null));

        assertEquals(TipoPeca.CASACO, demanda.getTipoPeca());
        assertEquals(5, demanda.getQuantidadeNecessaria());
        assertEquals(0, demanda.getQuantidadeAtendida());
    }

    @Test
    void paraDemanda_deAtualizacao_copiaCamposEQuantidades() {
        Demanda demanda = mapper.paraDemanda(
                new DemandaUpdateRequest(TipoPeca.COBERTOR, Tamanho.G, Genero.MASCULINO, 8, 3));

        assertEquals(TipoPeca.COBERTOR, demanda.getTipoPeca());
        assertEquals(Tamanho.G, demanda.getTamanho());
        assertEquals(Genero.MASCULINO, demanda.getGenero());
        assertEquals(8, demanda.getQuantidadeNecessaria());
        assertEquals(3, demanda.getQuantidadeAtendida());
    }

    @Test
    void paraResponse_copiaTodosOsCamposComPosicaoEDeficitDasDemandas() {
        Abrigo abrigo = new Abrigo("Abrigo Central", new Endereco("Rua A", "120", "Centro", "Maringa", "PR"), 100, 20);
        abrigo.setId("a1");
        abrigo.adicionarDemanda(new Demanda(TipoPeca.CASACO, Tamanho.M, Genero.UNISSEX, 30, 10));
        abrigo.adicionarDemanda(new Demanda(TipoPeca.COBERTOR, Tamanho.G, Genero.UNISSEX, 20, 5));

        AbrigoResponse resposta = mapper.paraResponse(abrigo);

        assertEquals("a1", resposta.id());
        assertEquals("Rua A", resposta.endereco().logradouro());
        assertEquals("Centro", resposta.endereco().bairro());
        assertEquals(100, resposta.capacidadeEstoque());
        assertEquals(20, resposta.estoqueAtual());
        assertEquals(2, resposta.demandas().size());
        DemandaResponse segunda = resposta.demandas().get(1);
        assertEquals(1, segunda.posicao());
        assertEquals(15, segunda.deficit());
    }

    @Test
    void paraResponse_enderecoSemNumeroEBairro_mantemCamposNulos() {
        Abrigo abrigo = mapper.paraModel(new AbrigoCreateRequest("Abrigo",
                new EnderecoRequest("Rua A", null, null, "Maringa", "PR"), 10, 0, null));

        AbrigoResponse resposta = mapper.paraResponse(abrigo);

        assertNull(resposta.endereco().numero());
        assertNull(resposta.endereco().bairro());
    }
}
