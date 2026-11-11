package br.com.cesumar.agasalha.controller;

import br.com.cesumar.agasalha.model.Abrigo;
import br.com.cesumar.agasalha.model.Demanda;
import br.com.cesumar.agasalha.model.Endereco;
import br.com.cesumar.agasalha.model.Genero;
import br.com.cesumar.agasalha.model.Tamanho;
import br.com.cesumar.agasalha.model.TipoPeca;
import br.com.cesumar.agasalha.repository.AbrigoRepository;
import br.com.cesumar.agasalha.support.MongoIT;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AbrigoApiIT extends MongoIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AbrigoRepository repository;

    @BeforeEach
    void limparColecao() {
        repository.deleteAll();
    }

    private static final String ENDERECO_JSON =
            "{\"logradouro\":\"Rua A\",\"numero\":\"120\",\"bairro\":\"Centro\",\"cidade\":\"Maringa\",\"estado\":\"PR\"}";

    private String abrigoJson() {
        return "{\"nome\":\"Abrigo Central\",\"endereco\":" + ENDERECO_JSON + ",\"capacidadeEstoque\":100,"
                + "\"estoqueAtual\":0,\"demandas\":[{\"tipoPeca\":\"CASACO\",\"tamanho\":\"M\","
                + "\"genero\":\"UNISSEX\",\"quantidadeNecessaria\":30}]}";
    }

    private String demandaJson(int necessaria, int atendida) {
        return "{\"tipoPeca\":\"COBERTOR\",\"tamanho\":\"G\",\"genero\":\"MASCULINO\","
                + "\"quantidadeNecessaria\":" + necessaria + ",\"quantidadeAtendida\":" + atendida + "}";
    }

    private Abrigo salvarAbrigoComDemanda() {
        Abrigo abrigo = new Abrigo("Abrigo Central", new Endereco("Rua A", "120", "Centro", "Maringa", "PR"), 100, 0);
        abrigo.adicionarDemanda(new Demanda(TipoPeca.CASACO, Tamanho.M, Genero.UNISSEX, 30, 0));
        return repository.save(abrigo);
    }

    @Test
    void criar_comEnderecoEDemandasAninhadas_persisteSubdocumentos() throws Exception {
        mockMvc.perform(post("/api/abrigos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(abrigoJson()))
                .andExpect(status().isCreated());

        Abrigo persistido = repository.findAll().get(0);
        assertEquals("Maringa", persistido.getEndereco().cidade());
        assertEquals(1, persistido.getDemandas().size());
        assertEquals(30, persistido.getDemandas().get(0).deficit());
    }

    @Test
    void criar_capacidadeZero_retorna400SemPersistir() throws Exception {
        mockMvc.perform(post("/api/abrigos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(abrigoJson().replace("\"capacidadeEstoque\":100", "\"capacidadeEstoque\":0")))
                .andExpect(status().isBadRequest());

        assertEquals(0, repository.count());
    }

    @Test
    void criar_demandaAtendidaAcimaDaNecessaria_retorna400SemPersistir() throws Exception {
        mockMvc.perform(post("/api/abrigos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(abrigoJson().replace("\"quantidadeNecessaria\":30",
                                "\"quantidadeNecessaria\":2,\"quantidadeAtendida\":3")))
                .andExpect(status().isBadRequest());

        assertEquals(0, repository.count());
    }

    @Test
    void buscarPorId_existente_retornaAbrigoComDemandas() throws Exception {
        Abrigo abrigo = salvarAbrigoComDemanda();

        mockMvc.perform(get("/api/abrigos/" + abrigo.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Abrigo Central"))
                .andExpect(jsonPath("$.endereco.bairro").value("Centro"))
                .andExpect(jsonPath("$.demandas[0].posicao").value(0))
                .andExpect(jsonPath("$.demandas[0].deficit").value(30));
    }

    @Test
    void buscarPorId_inexistente_retorna404() throws Exception {
        mockMvc.perform(get("/api/abrigos/inexistente"))
                .andExpect(status().isNotFound());
    }

    @Test
    void listar_retornaTodosOsAbrigos() throws Exception {
        salvarAbrigoComDemanda();
        salvarAbrigoComDemanda();

        mockMvc.perform(get("/api/abrigos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void atualizar_valido_alteraDadosEPreservaDemandas() throws Exception {
        Abrigo abrigo = salvarAbrigoComDemanda();
        String corpo = "{\"nome\":\"Novo Nome\",\"endereco\":{\"logradouro\":\"Rua B\",\"cidade\":\"Londrina\","
                + "\"estado\":\"PR\"},\"capacidadeEstoque\":200,\"estoqueAtual\":4}";

        mockMvc.perform(put("/api/abrigos/" + abrigo.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpo))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Novo Nome"));

        Abrigo persistido = repository.findById(abrigo.getId()).orElseThrow();
        assertEquals("Londrina", persistido.getEndereco().cidade());
        assertEquals(200, persistido.getCapacidadeEstoque());
        assertEquals(1, persistido.getDemandas().size());
    }

    @Test
    void remover_semVinculos_retorna204ERemoveDoBanco() throws Exception {
        Abrigo abrigo = salvarAbrigoComDemanda();

        mockMvc.perform(delete("/api/abrigos/" + abrigo.getId()))
                .andExpect(status().isNoContent());

        assertFalse(repository.existsById(abrigo.getId()));
    }

    @Test
    void adicionarDemanda_valida_persisteNaListaAninhada() throws Exception {
        Abrigo abrigo = salvarAbrigoComDemanda();

        mockMvc.perform(post("/api/abrigos/" + abrigo.getId() + "/demandas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(demandaJson(8, 2)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.posicao").value(1))
                .andExpect(jsonPath("$.deficit").value(6));

        assertEquals(2, repository.findById(abrigo.getId()).orElseThrow().getDemandas().size());
    }

    @Test
    void adicionarDemanda_atendidaAcimaDaNecessaria_retorna400() throws Exception {
        Abrigo abrigo = salvarAbrigoComDemanda();

        mockMvc.perform(post("/api/abrigos/" + abrigo.getId() + "/demandas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(demandaJson(2, 3)))
                .andExpect(status().isBadRequest());

        assertEquals(1, repository.findById(abrigo.getId()).orElseThrow().getDemandas().size());
    }

    @Test
    void atualizarDemanda_posicaoExistente_substituiOSubdocumento() throws Exception {
        Abrigo abrigo = salvarAbrigoComDemanda();

        mockMvc.perform(put("/api/abrigos/" + abrigo.getId() + "/demandas/0")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(demandaJson(9, 4)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deficit").value(5));

        Demanda persistida = repository.findById(abrigo.getId()).orElseThrow().getDemandas().get(0);
        assertEquals(TipoPeca.COBERTOR, persistida.getTipoPeca());
        assertEquals(4, persistida.getQuantidadeAtendida());
    }

    @Test
    void atualizarDemanda_posicaoInexistente_retorna404() throws Exception {
        Abrigo abrigo = salvarAbrigoComDemanda();

        mockMvc.perform(put("/api/abrigos/" + abrigo.getId() + "/demandas/7")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(demandaJson(9, 4)))
                .andExpect(status().isNotFound());
    }

    @Test
    void removerDemanda_posicaoExistente_retiraDaListaAninhada() throws Exception {
        Abrigo abrigo = salvarAbrigoComDemanda();

        mockMvc.perform(delete("/api/abrigos/" + abrigo.getId() + "/demandas/0"))
                .andExpect(status().isNoContent());

        assertTrue(repository.findById(abrigo.getId()).orElseThrow().getDemandas().isEmpty());
    }

    @Test
    void listarDemandas_retornaDemandasDoAbrigo() throws Exception {
        Abrigo abrigo = salvarAbrigoComDemanda();

        mockMvc.perform(get("/api/abrigos/" + abrigo.getId() + "/demandas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }
}
