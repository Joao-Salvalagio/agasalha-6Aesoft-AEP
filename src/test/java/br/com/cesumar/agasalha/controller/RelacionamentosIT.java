package br.com.cesumar.agasalha.controller;

import br.com.cesumar.agasalha.model.Abrigo;
import br.com.cesumar.agasalha.model.Demanda;
import br.com.cesumar.agasalha.model.Doador;
import br.com.cesumar.agasalha.model.Endereco;
import br.com.cesumar.agasalha.model.EstadoConservacao;
import br.com.cesumar.agasalha.model.Genero;
import br.com.cesumar.agasalha.model.ItemAgasalho;
import br.com.cesumar.agasalha.model.StatusItem;
import br.com.cesumar.agasalha.model.Tamanho;
import br.com.cesumar.agasalha.model.TipoPeca;
import br.com.cesumar.agasalha.repository.AbrigoRepository;
import br.com.cesumar.agasalha.repository.DoacaoRepository;
import br.com.cesumar.agasalha.repository.DoadorRepository;
import br.com.cesumar.agasalha.repository.ItemRepository;
import br.com.cesumar.agasalha.support.MongoIT;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RelacionamentosIT extends MongoIT {

    private static final String ITEM_JSON = "{\"tipoPeca\":\"CASACO\",\"tamanho\":\"M\",\"genero\":\"UNISSEX\","
            + "\"estadoConservacao\":\"USADO_BOM\"}";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AbrigoRepository abrigoRepository;

    @Autowired
    private DoadorRepository doadorRepository;

    @Autowired
    private DoacaoRepository doacaoRepository;

    @Autowired
    private ItemRepository itemRepository;

    @BeforeEach
    void limparColecoes() {
        abrigoRepository.deleteAll();
        doadorRepository.deleteAll();
        doacaoRepository.deleteAll();
        itemRepository.deleteAll();
    }

    private Doador salvarDoador() {
        return doadorRepository.save(new Doador("Ana Souza", "ana@exemplo.com"));
    }

    private Abrigo salvarAbrigo(Demanda... demandas) {
        Abrigo abrigo = new Abrigo("Abrigo Central", new Endereco("Rua A", "120", "Centro", "Maringa", "PR"), 100, 0);
        for (Demanda demanda : demandas) {
            abrigo.adicionarDemanda(demanda);
        }
        return abrigoRepository.save(abrigo);
    }

    private String idDe(String corpo) throws Exception {
        return objectMapper.readTree(corpo).get("id").asText();
    }

    private String criarDoacao(String doadorId, String abrigoId) throws Exception {
        String corpo = mockMvc.perform(post("/api/doacoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"doadorId\":\"" + doadorId + "\",\"abrigoId\":\"" + abrigoId + "\"}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return idDe(corpo);
    }

    private String criarItemNaDoacao(String doacaoId) throws Exception {
        String corpo = mockMvc.perform(post("/api/doacoes/" + doacaoId + "/itens")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ITEM_JSON))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return idDe(corpo);
    }

    @Test
    void criarDoacao_comDoadorEAbrigoExistentes_guardaApenasOsIdentificadores() throws Exception {
        Doador doador = salvarDoador();
        Abrigo abrigo = salvarAbrigo();

        String doacaoId = criarDoacao(doador.getId(), abrigo.getId());

        var persistida = doacaoRepository.findById(doacaoId).orElseThrow();
        assertEquals(doador.getId(), persistida.getDoadorId());
        assertEquals(abrigo.getId(), persistida.getAbrigoId());
    }

    @Test
    void criarDoacao_doadorInexistente_retorna404SemPersistir() throws Exception {
        Abrigo abrigo = salvarAbrigo();

        mockMvc.perform(post("/api/doacoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"doadorId\":\"inexistente\",\"abrigoId\":\"" + abrigo.getId() + "\"}"))
                .andExpect(status().isNotFound());

        assertEquals(0, doacaoRepository.count());
    }

    @Test
    void criarDoacao_abrigoInexistente_retorna404SemPersistir() throws Exception {
        Doador doador = salvarDoador();

        mockMvc.perform(post("/api/doacoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"doadorId\":\"" + doador.getId() + "\",\"abrigoId\":\"inexistente\"}"))
                .andExpect(status().isNotFound());

        assertEquals(0, doacaoRepository.count());
    }

    @Test
    void criarDoacao_semAbrigo_persisteEItemRecebeSugestaoDoMatch() throws Exception {
        Doador doador = salvarDoador();
        Abrigo abrigo = salvarAbrigo(new Demanda(TipoPeca.CASACO, Tamanho.M, Genero.UNISSEX, 30, 0));

        String corpo = mockMvc.perform(post("/api/doacoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"doadorId\":\"" + doador.getId() + "\"}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        String doacaoId = idDe(corpo);
        assertNull(doacaoRepository.findById(doacaoId).orElseThrow().getAbrigoId());

        String itemId = criarItemNaDoacao(doacaoId);

        mockMvc.perform(post("/api/matches")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"itemId\":\"" + itemId + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.abrigoId").value(abrigo.getId()));
    }

    @Test
    void criarItemNaDoacao_doacaoInexistente_retorna404() throws Exception {
        mockMvc.perform(post("/api/doacoes/inexistente/itens")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ITEM_JSON))
                .andExpect(status().isNotFound());

        assertEquals(0, itemRepository.count());
    }

    @Test
    void navegarItemParaDoacaoParaDoador_percorreAsReferenciasEntreColecoes() throws Exception {
        Doador doador = salvarDoador();
        Abrigo abrigo = salvarAbrigo();
        String doacaoId = criarDoacao(doador.getId(), abrigo.getId());
        String itemId = criarItemNaDoacao(doacaoId);

        String itemCorpo = mockMvc.perform(get("/api/itens/" + itemId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.doacaoId").value(doacaoId))
                .andReturn().getResponse().getContentAsString();
        assertNull(objectMapper.readTree(itemCorpo).path("nomeDoador").textValue());

        String doacaoCorpo = mockMvc.perform(get("/api/doacoes/"
                        + objectMapper.readTree(itemCorpo).get("doacaoId").asText()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.abrigoId").value(abrigo.getId()))
                .andReturn().getResponse().getContentAsString();

        mockMvc.perform(get("/api/doadores/" + objectMapper.readTree(doacaoCorpo).get("doadorId").asText()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Ana Souza"));
    }

    @Test
    void itemDaDoacao_participaDoMatchingComAbrigoCompativel() throws Exception {
        Doador doador = salvarDoador();
        Abrigo abrigo = salvarAbrigo(new Demanda(TipoPeca.CASACO, Tamanho.M, Genero.UNISSEX, 30, 0));
        String itemId = criarItemNaDoacao(criarDoacao(doador.getId(), abrigo.getId()));

        mockMvc.perform(post("/api/matches")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"itemId\":\"" + itemId + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.abrigoId").value(abrigo.getId()))
                .andExpect(jsonPath("$.deficit").value(30));
    }

    @Test
    void enviarAoAbrigoEMudarEstado_mantemReferenciasEAtualizaSoOAbrigo() throws Exception {
        Doador doador = salvarDoador();
        Abrigo abrigo = salvarAbrigo(new Demanda(TipoPeca.CASACO, Tamanho.M, Genero.UNISSEX, 30, 0));
        String doacaoId = criarDoacao(doador.getId(), abrigo.getId());
        String itemId = criarItemNaDoacao(doacaoId);

        mockMvc.perform(post("/api/itens/" + itemId + "/reserva"))
                .andExpect(status().isConflict());
        mockMvc.perform(post("/api/matches/confirmacao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"itemId\":\"" + itemId + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DISPONIVEL"));
        mockMvc.perform(post("/api/itens/" + itemId + "/reserva"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("RESERVADO"))
                .andExpect(jsonPath("$.doacaoId").value(doacaoId));
        mockMvc.perform(post("/api/itens/" + itemId + "/entrega"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ENTREGUE"))
                .andExpect(jsonPath("$.doacaoId").value(doacaoId));

        ItemAgasalho item = itemRepository.findById(itemId).orElseThrow();
        assertEquals(StatusItem.ENTREGUE, item.getStatus());
        assertEquals(doacaoId, item.getDoacaoId());
        Abrigo inalterado = abrigoRepository.findById(abrigo.getId()).orElseThrow();
        assertEquals(1, inalterado.getEstoqueAtual());
        assertEquals(29, inalterado.getDemandas().get(0).deficit());
        assertEquals(1, doadorRepository.count());
    }

    @Test
    void removerDoador_comDoacao_retorna409EMantemDoador() throws Exception {
        Doador doador = salvarDoador();
        Abrigo abrigo = salvarAbrigo();
        criarDoacao(doador.getId(), abrigo.getId());

        mockMvc.perform(delete("/api/doadores/" + doador.getId()))
                .andExpect(status().isConflict());

        assertEquals(1, doadorRepository.count());
    }

    @Test
    void removerDoador_semDoacoes_retorna204() throws Exception {
        Doador doador = salvarDoador();

        mockMvc.perform(delete("/api/doadores/" + doador.getId()))
                .andExpect(status().isNoContent());

        assertEquals(0, doadorRepository.count());
    }

    @Test
    void removerAbrigo_comDoacao_retorna409EMantemAbrigo() throws Exception {
        Doador doador = salvarDoador();
        Abrigo abrigo = salvarAbrigo();
        criarDoacao(doador.getId(), abrigo.getId());

        mockMvc.perform(delete("/api/abrigos/" + abrigo.getId()))
                .andExpect(status().isConflict());

        assertEquals(1, abrigoRepository.count());
    }

    @Test
    void removerAbrigo_comItemReferenciandoAbrigo_retorna409() throws Exception {
        Abrigo abrigo = salvarAbrigo();
        ItemAgasalho item = new ItemAgasalho(TipoPeca.CASACO, Tamanho.M, Genero.UNISSEX,
                EstadoConservacao.USADO_BOM, "Ana", "ana@exemplo.com");
        item.setAbrigoId(abrigo.getId());
        itemRepository.save(item);

        mockMvc.perform(delete("/api/abrigos/" + abrigo.getId()))
                .andExpect(status().isConflict());
    }
}
