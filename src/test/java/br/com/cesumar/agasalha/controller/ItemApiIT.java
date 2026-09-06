package br.com.cesumar.agasalha.controller;

import br.com.cesumar.agasalha.controller.dto.ItemCreateRequest;
import br.com.cesumar.agasalha.controller.dto.ItemUpdateRequest;
import br.com.cesumar.agasalha.model.EstadoConservacao;
import br.com.cesumar.agasalha.model.Genero;
import br.com.cesumar.agasalha.model.ItemAgasalho;
import br.com.cesumar.agasalha.model.StatusItem;
import br.com.cesumar.agasalha.model.Tamanho;
import br.com.cesumar.agasalha.model.TipoPeca;
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

import java.util.List;

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
class ItemApiIT extends MongoIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ItemRepository repository;

    @BeforeEach
    void limparColecao() {
        repository.deleteAll();
    }

    private ItemCreateRequest criarRequest() {
        return new ItemCreateRequest(TipoPeca.CASACO, Tamanho.M, Genero.UNISSEX,
                EstadoConservacao.USADO_BOM, "Ana", "ana@exemplo.com");
    }

    private ItemUpdateRequest atualizarRequest() {
        return new ItemUpdateRequest(TipoPeca.COBERTOR, Tamanho.G, Genero.MASCULINO,
                EstadoConservacao.NOVO, "Bia", "bia@exemplo.com");
    }

    private ItemAgasalho salvarItem(Tamanho tamanho, StatusItem status) {
        ItemAgasalho item = new ItemAgasalho(TipoPeca.CASACO, tamanho, Genero.UNISSEX,
                EstadoConservacao.USADO_BOM, "Ana", "ana@exemplo.com");
        item.setStatus(status);
        return repository.save(item);
    }

    @Test
    void criar_valido_persisteERetorna201() throws Exception {
        mockMvc.perform(post("/api/itens")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(criarRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("DISPONIVEL"));

        assertEquals(1, repository.count());
    }

    @Test
    void criar_payloadInvalido_retorna400SemPersistir() throws Exception {
        String corpoInvalido = "{\"tipoPeca\":null,\"tamanho\":\"M\",\"genero\":\"UNISSEX\","
                + "\"estadoConservacao\":\"NOVO\",\"nomeDoador\":\"Ana\",\"contatoDoador\":\"ana@exemplo.com\"}";

        mockMvc.perform(post("/api/itens")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpoInvalido))
                .andExpect(status().isBadRequest());

        assertEquals(0, repository.count());
    }

    @Test
    void buscarPorId_existente_retorna200ComItem() throws Exception {
        ItemAgasalho item = salvarItem(Tamanho.M, StatusItem.DISPONIVEL);

        mockMvc.perform(get("/api/itens/" + item.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(item.getId()))
                .andExpect(jsonPath("$.nomeDoador").value("Ana"));
    }

    @Test
    void buscarPorId_inexistente_retorna404() throws Exception {
        mockMvc.perform(get("/api/itens/inexistente"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void listar_semFiltro_retornaTodosOsItens() throws Exception {
        salvarItem(Tamanho.M, StatusItem.DISPONIVEL);
        salvarItem(Tamanho.G, StatusItem.DISPONIVEL);

        mockMvc.perform(get("/api/itens"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void listar_comFiltro_retornaApenasCorrespondentes() throws Exception {
        salvarItem(Tamanho.M, StatusItem.DISPONIVEL);
        salvarItem(Tamanho.G, StatusItem.DISPONIVEL);

        mockMvc.perform(get("/api/itens").param("tamanho", "M"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].tamanho").value("M"));
    }

    @Test
    void atualizar_valido_persisteAlteracaoERetorna200() throws Exception {
        ItemAgasalho item = salvarItem(Tamanho.M, StatusItem.DISPONIVEL);

        mockMvc.perform(put("/api/itens/" + item.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(atualizarRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nomeDoador").value("Bia"));

        ItemAgasalho persistido = repository.findById(item.getId()).orElseThrow();
        assertEquals("Bia", persistido.getNomeDoador());
        assertEquals(Tamanho.G, persistido.getTamanho());
    }

    @Test
    void atualizar_payloadInvalido_retorna400() throws Exception {
        ItemAgasalho item = salvarItem(Tamanho.M, StatusItem.DISPONIVEL);
        String corpoInvalido = "{\"tipoPeca\":\"CASACO\",\"tamanho\":null,\"genero\":\"UNISSEX\","
                + "\"estadoConservacao\":\"NOVO\",\"nomeDoador\":\"Bia\",\"contatoDoador\":\"bia@exemplo.com\"}";

        mockMvc.perform(put("/api/itens/" + item.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpoInvalido))
                .andExpect(status().isBadRequest());
    }

    @Test
    void atualizar_inexistente_retorna404() throws Exception {
        mockMvc.perform(put("/api/itens/inexistente")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(atualizarRequest())))
                .andExpect(status().isNotFound());
    }

    @Test
    void remover_existente_retorna204ERemoveDoBanco() throws Exception {
        ItemAgasalho item = salvarItem(Tamanho.M, StatusItem.DISPONIVEL);

        mockMvc.perform(delete("/api/itens/" + item.getId()))
                .andExpect(status().isNoContent());

        assertFalse(repository.existsById(item.getId()));
    }

    @Test
    void remover_inexistente_retorna404() throws Exception {
        mockMvc.perform(delete("/api/itens/inexistente"))
                .andExpect(status().isNotFound());
    }

    @Test
    void reservar_itemDisponivel_retorna200EAtualizaStatus() throws Exception {
        ItemAgasalho item = salvarItem(Tamanho.M, StatusItem.DISPONIVEL);

        mockMvc.perform(post("/api/itens/" + item.getId() + "/reserva"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("RESERVADO"));

        assertEquals(StatusItem.RESERVADO, repository.findById(item.getId()).orElseThrow().getStatus());
    }

    @Test
    void reservar_itemInexistente_retorna404() throws Exception {
        mockMvc.perform(post("/api/itens/inexistente/reserva"))
                .andExpect(status().isNotFound());
    }

    @Test
    void reservar_itemJaReservado_retorna409SemAlterarStatus() throws Exception {
        ItemAgasalho item = salvarItem(Tamanho.M, StatusItem.RESERVADO);

        mockMvc.perform(post("/api/itens/" + item.getId() + "/reserva"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));

        assertEquals(StatusItem.RESERVADO, repository.findById(item.getId()).orElseThrow().getStatus());
    }

    @Test
    void entregar_itemReservado_retorna200EAtualizaStatus() throws Exception {
        ItemAgasalho item = salvarItem(Tamanho.M, StatusItem.RESERVADO);

        mockMvc.perform(post("/api/itens/" + item.getId() + "/entrega"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ENTREGUE"));

        assertEquals(StatusItem.ENTREGUE, repository.findById(item.getId()).orElseThrow().getStatus());
    }

    @Test
    void entregar_itemDisponivel_retorna409SemAlterarStatus() throws Exception {
        ItemAgasalho item = salvarItem(Tamanho.M, StatusItem.DISPONIVEL);

        mockMvc.perform(post("/api/itens/" + item.getId() + "/entrega"))
                .andExpect(status().isConflict());

        assertEquals(StatusItem.DISPONIVEL, repository.findById(item.getId()).orElseThrow().getStatus());
    }

    @Test
    void entregar_itemInexistente_retorna404() throws Exception {
        mockMvc.perform(post("/api/itens/inexistente/entrega"))
                .andExpect(status().isNotFound());
    }
}