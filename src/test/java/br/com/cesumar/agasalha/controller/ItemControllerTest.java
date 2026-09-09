package br.com.cesumar.agasalha.controller;

import br.com.cesumar.agasalha.controller.dto.ItemCreateRequest;
import br.com.cesumar.agasalha.controller.dto.ItemResponse;
import br.com.cesumar.agasalha.controller.dto.ItemSummaryResponse;
import br.com.cesumar.agasalha.controller.dto.ItemUpdateRequest;
import br.com.cesumar.agasalha.exception.ItemNaoEncontradoException;
import br.com.cesumar.agasalha.exception.TransicaoInvalidaException;
import br.com.cesumar.agasalha.model.AcaoStatus;
import br.com.cesumar.agasalha.model.EstadoConservacao;
import br.com.cesumar.agasalha.model.Genero;
import br.com.cesumar.agasalha.model.StatusItem;
import br.com.cesumar.agasalha.model.Tamanho;
import br.com.cesumar.agasalha.model.TipoPeca;
import br.com.cesumar.agasalha.repository.FiltroItem;
import br.com.cesumar.agasalha.service.ItemService;
import br.com.cesumar.agasalha.service.ItemStatusService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ItemService itemService;

    @MockitoBean
    private ItemStatusService itemStatusService;

    private ItemCreateRequest criarRequest() {
        return new ItemCreateRequest(TipoPeca.CASACO, Tamanho.M, Genero.UNISSEX,
                EstadoConservacao.USADO_BOM, "Ana", "ana@exemplo.com");
    }

    private ItemUpdateRequest atualizarRequest() {
        return new ItemUpdateRequest(TipoPeca.COBERTOR, Tamanho.G, Genero.MASCULINO,
                EstadoConservacao.NOVO, "Bia", "bia@exemplo.com");
    }

    private ItemResponse response(String id, StatusItem status) {
        return new ItemResponse(id, TipoPeca.CASACO, Tamanho.M, Genero.UNISSEX,
                EstadoConservacao.USADO_BOM, "Ana", "ana@exemplo.com", status, Instant.parse("2026-06-01T10:00:00Z"));
    }

    @Test
    void criar_valido_retorna201ComLocationEBody() throws Exception {
        when(itemService.criar(any(ItemCreateRequest.class))).thenReturn(response("id1", StatusItem.DISPONIVEL));

        mockMvc.perform(post("/api/itens")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(criarRequest())))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/itens/id1"))
                .andExpect(jsonPath("$.id").value("id1"))
                .andExpect(jsonPath("$.status").value("DISPONIVEL"));
    }

    @Test
    void criar_camposObrigatoriosAusentes_retorna400ComDetalhes() throws Exception {
        String corpoInvalido = "{\"tipoPeca\":null,\"tamanho\":\"M\",\"genero\":\"UNISSEX\","
                + "\"estadoConservacao\":\"NOVO\",\"nomeDoador\":\"   \",\"contatoDoador\":\"ana@exemplo.com\"}";

        mockMvc.perform(post("/api/itens")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpoInvalido))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detalhes").isNotEmpty());
    }

    @Test
    void listar_semFiltro_retorna200ComListaEMontaFiltroVazio() throws Exception {
        when(itemService.listar(any(FiltroItem.class)))
                .thenReturn(List.of(new ItemSummaryResponse("id1", TipoPeca.CASACO, Tamanho.M, Genero.UNISSEX, StatusItem.DISPONIVEL)));

        mockMvc.perform(get("/api/itens"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value("id1"));

        verify(itemService).listar(eq(FiltroItem.vazio()));
    }

    @Test
    void listar_comFiltros_montaFiltroItemCorreto() throws Exception {
        when(itemService.listar(any(FiltroItem.class))).thenReturn(List.of());

        mockMvc.perform(get("/api/itens")
                        .param("tamanho", "M")
                        .param("tipoPeca", "CASACO")
                        .param("genero", "UNISSEX")
                        .param("status", "DISPONIVEL"))
                .andExpect(status().isOk());

        verify(itemService).listar(eq(new FiltroItem(Tamanho.M, TipoPeca.CASACO, Genero.UNISSEX, StatusItem.DISPONIVEL)));
    }

    @Test
    void listar_parametroEnumInvalido_retorna400() throws Exception {
        mockMvc.perform(get("/api/itens").param("tamanho", "XPTO"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void buscarPorId_existente_retorna200ComBody() throws Exception {
        when(itemService.buscarPorId("id1")).thenReturn(response("id1", StatusItem.DISPONIVEL));

        mockMvc.perform(get("/api/itens/id1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("id1"))
                .andExpect(jsonPath("$.nomeDoador").value("Ana"));
    }

    @Test
    void buscarPorId_inexistente_retorna404() throws Exception {
        when(itemService.buscarPorId("x")).thenThrow(new ItemNaoEncontradoException("x"));

        mockMvc.perform(get("/api/itens/x"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void atualizar_valido_retorna200ComBody() throws Exception {
        when(itemService.atualizar(eq("id1"), any(ItemUpdateRequest.class)))
                .thenReturn(response("id1", StatusItem.RESERVADO));

        mockMvc.perform(put("/api/itens/id1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(atualizarRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("RESERVADO"));
    }

    @Test
    void atualizar_camposObrigatoriosAusentes_retorna400() throws Exception {
        String corpoInvalido = "{\"tipoPeca\":\"CASACO\",\"tamanho\":null,\"genero\":\"UNISSEX\","
                + "\"estadoConservacao\":\"NOVO\",\"nomeDoador\":\"Bia\",\"contatoDoador\":\"bia@exemplo.com\"}";

        mockMvc.perform(put("/api/itens/id1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpoInvalido))
                .andExpect(status().isBadRequest());
    }

    @Test
    void atualizar_inexistente_retorna404() throws Exception {
        when(itemService.atualizar(eq("x"), any(ItemUpdateRequest.class)))
                .thenThrow(new ItemNaoEncontradoException("x"));

        mockMvc.perform(put("/api/itens/x")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(atualizarRequest())))
                .andExpect(status().isNotFound());
    }

    @Test
    void remover_existente_retorna204SemCorpo() throws Exception {
        mockMvc.perform(delete("/api/itens/id1"))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(itemService).remover("id1");
    }

    @Test
    void remover_inexistente_retorna404() throws Exception {
        doThrow(new ItemNaoEncontradoException("x")).when(itemService).remover("x");

        mockMvc.perform(delete("/api/itens/x"))
                .andExpect(status().isNotFound());
    }

    @Test
    void reservar_valido_retorna200ComBody() throws Exception {
        when(itemStatusService.reservar("id1")).thenReturn(response("id1", StatusItem.RESERVADO));

        mockMvc.perform(post("/api/itens/id1/reserva"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("RESERVADO"));
    }

    @Test
    void reservar_itemInexistente_retorna404() throws Exception {
        when(itemStatusService.reservar("x")).thenThrow(new ItemNaoEncontradoException("x"));

        mockMvc.perform(post("/api/itens/x/reserva"))
                .andExpect(status().isNotFound());
    }

    @Test
    void reservar_transicaoInvalida_retorna409() throws Exception {
        when(itemStatusService.reservar("id1"))
                .thenThrow(new TransicaoInvalidaException(StatusItem.ENTREGUE, AcaoStatus.RESERVAR));

        mockMvc.perform(post("/api/itens/id1/reserva"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void entregar_valido_retorna200ComBody() throws Exception {
        when(itemStatusService.entregar("id1")).thenReturn(response("id1", StatusItem.ENTREGUE));

        mockMvc.perform(post("/api/itens/id1/entrega"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ENTREGUE"));
    }

    @Test
    void entregar_transicaoInvalida_retorna409() throws Exception {
        when(itemStatusService.entregar("id1"))
                .thenThrow(new TransicaoInvalidaException(StatusItem.DISPONIVEL, AcaoStatus.ENTREGAR));

        mockMvc.perform(post("/api/itens/id1/entrega"))
                .andExpect(status().isConflict());
    }
}