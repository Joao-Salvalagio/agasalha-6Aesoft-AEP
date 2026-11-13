package br.com.cesumar.agasalha.controller;

import br.com.cesumar.agasalha.controller.dto.DoacaoCreateRequest;
import br.com.cesumar.agasalha.controller.dto.DoacaoResponse;
import br.com.cesumar.agasalha.controller.dto.ItemDoacaoRequest;
import br.com.cesumar.agasalha.controller.dto.ItemResponse;
import br.com.cesumar.agasalha.exception.AbrigoNaoEncontradoException;
import br.com.cesumar.agasalha.exception.DoacaoNaoEncontradaException;
import br.com.cesumar.agasalha.exception.DoadorNaoEncontradoException;
import br.com.cesumar.agasalha.model.EstadoConservacao;
import br.com.cesumar.agasalha.model.Genero;
import br.com.cesumar.agasalha.model.StatusItem;
import br.com.cesumar.agasalha.model.Tamanho;
import br.com.cesumar.agasalha.model.TipoPeca;
import br.com.cesumar.agasalha.service.DoacaoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DoacaoController.class)
class DoacaoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private DoacaoService service;

    private DoacaoResponse response(String id) {
        return new DoacaoResponse(id, "d1", "a1", Instant.parse("2026-06-01T10:00:00Z"));
    }

    private ItemDoacaoRequest itemRequest() {
        return new ItemDoacaoRequest(TipoPeca.CASACO, Tamanho.M, Genero.UNISSEX, EstadoConservacao.USADO_BOM);
    }

    private ItemResponse itemResponse(String id) {
        return new ItemResponse(id, TipoPeca.CASACO, Tamanho.M, Genero.UNISSEX, EstadoConservacao.USADO_BOM,
                null, null, StatusItem.DISPONIVEL, Instant.parse("2026-06-01T10:00:00Z"), "o1", null);
    }

    @Test
    void criar_valida_retorna201ComLocationEBody() throws Exception {
        when(service.criar(any(DoacaoCreateRequest.class))).thenReturn(response("o1"));

        mockMvc.perform(post("/api/doacoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new DoacaoCreateRequest("d1", "a1"))))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/doacoes/o1"))
                .andExpect(jsonPath("$.doadorId").value("d1"))
                .andExpect(jsonPath("$.abrigoId").value("a1"));
    }

    @Test
    void criar_semAbrigo_retorna201() throws Exception {
        when(service.criar(any(DoacaoCreateRequest.class)))
                .thenReturn(new DoacaoResponse("o2", "d1", null, Instant.parse("2026-06-01T10:00:00Z")));

        mockMvc.perform(post("/api/doacoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"doadorId\":\"d1\"}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/doacoes/o2"))
                .andExpect(jsonPath("$.abrigoId").doesNotExist());
    }

    @Test
    void criar_referenciasEmBranco_retorna400() throws Exception {
        mockMvc.perform(post("/api/doacoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"doadorId\":\"\",\"abrigoId\":null}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detalhes").isNotEmpty());
    }

    @Test
    void criar_doadorInexistente_retorna404() throws Exception {
        when(service.criar(any(DoacaoCreateRequest.class))).thenThrow(new DoadorNaoEncontradoException("d1"));

        mockMvc.perform(post("/api/doacoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new DoacaoCreateRequest("d1", "a1"))))
                .andExpect(status().isNotFound());
    }

    @Test
    void criar_abrigoInexistente_retorna404() throws Exception {
        when(service.criar(any(DoacaoCreateRequest.class))).thenThrow(new AbrigoNaoEncontradoException("a1"));

        mockMvc.perform(post("/api/doacoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new DoacaoCreateRequest("d1", "a1"))))
                .andExpect(status().isNotFound());
    }

    @Test
    void buscarPorId_existente_retorna200() throws Exception {
        when(service.buscarPorId("o1")).thenReturn(response("o1"));

        mockMvc.perform(get("/api/doacoes/o1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("o1"));
    }

    @Test
    void buscarPorId_inexistente_retorna404() throws Exception {
        when(service.buscarPorId("x")).thenThrow(new DoacaoNaoEncontradaException("x"));

        mockMvc.perform(get("/api/doacoes/x"))
                .andExpect(status().isNotFound());
    }

    @Test
    void criarItem_valido_retorna201ComLocationDoItem() throws Exception {
        when(service.criarItem(eq("o1"), any(ItemDoacaoRequest.class))).thenReturn(itemResponse("item1"));

        mockMvc.perform(post("/api/doacoes/o1/itens")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequest())))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/itens/item1"))
                .andExpect(jsonPath("$.doacaoId").value("o1"));
    }

    @Test
    void criarItem_camposObrigatoriosAusentes_retorna400() throws Exception {
        mockMvc.perform(post("/api/doacoes/o1/itens")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tipoPeca\":null,\"tamanho\":\"M\",\"genero\":\"UNISSEX\","
                                + "\"estadoConservacao\":\"NOVO\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void criarItem_doacaoInexistente_retorna404() throws Exception {
        when(service.criarItem(eq("x"), any(ItemDoacaoRequest.class)))
                .thenThrow(new DoacaoNaoEncontradaException("x"));

        mockMvc.perform(post("/api/doacoes/x/itens")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequest())))
                .andExpect(status().isNotFound());
    }
}
