package br.com.cesumar.agasalha.controller;

import br.com.cesumar.agasalha.controller.dto.AbrigoCreateRequest;
import br.com.cesumar.agasalha.controller.dto.AbrigoResponse;
import br.com.cesumar.agasalha.controller.dto.AbrigoUpdateRequest;
import br.com.cesumar.agasalha.controller.dto.DemandaCreateRequest;
import br.com.cesumar.agasalha.controller.dto.DemandaResponse;
import br.com.cesumar.agasalha.controller.dto.DemandaUpdateRequest;
import br.com.cesumar.agasalha.controller.dto.EnderecoRequest;
import br.com.cesumar.agasalha.controller.dto.EnderecoResponse;
import br.com.cesumar.agasalha.exception.AbrigoNaoEncontradoException;
import br.com.cesumar.agasalha.exception.DadosInvalidosException;
import br.com.cesumar.agasalha.exception.DemandaNaoEncontradaException;
import br.com.cesumar.agasalha.exception.RegistroEmUsoException;
import br.com.cesumar.agasalha.model.Genero;
import br.com.cesumar.agasalha.model.Tamanho;
import br.com.cesumar.agasalha.model.TipoPeca;
import br.com.cesumar.agasalha.service.AbrigoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

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

@WebMvcTest(AbrigoController.class)
class AbrigoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AbrigoService service;

    private EnderecoRequest endereco() {
        return new EnderecoRequest("Rua A", "120", "Centro", "Maringa", "PR");
    }

    private AbrigoCreateRequest criarRequest() {
        return new AbrigoCreateRequest("Abrigo Central", endereco(), 100, 0,
                List.of(new DemandaCreateRequest(TipoPeca.CASACO, Tamanho.M, Genero.UNISSEX, 30, 0)));
    }

    private AbrigoUpdateRequest atualizarRequest() {
        return new AbrigoUpdateRequest("Novo Nome", endereco(), 200, 3);
    }

    private DemandaCreateRequest demandaCriarRequest() {
        return new DemandaCreateRequest(TipoPeca.CASACO, Tamanho.M, Genero.UNISSEX, 30, 0);
    }

    private DemandaUpdateRequest demandaAtualizarRequest() {
        return new DemandaUpdateRequest(TipoPeca.COBERTOR, Tamanho.G, Genero.MASCULINO, 8, 2);
    }

    private DemandaResponse demandaResponse(int posicao) {
        return new DemandaResponse(posicao, TipoPeca.CASACO, Tamanho.M, Genero.UNISSEX, 30, 0, 30);
    }

    private AbrigoResponse response(String id) {
        return new AbrigoResponse(id, "Abrigo Central",
                new EnderecoResponse("Rua A", "120", "Centro", "Maringa", "PR"),
                100, 0, List.of(demandaResponse(0)));
    }

    @Test
    void criar_valido_retorna201ComLocationEBody() throws Exception {
        when(service.criar(any(AbrigoCreateRequest.class))).thenReturn(response("a1"));

        mockMvc.perform(post("/api/abrigos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(criarRequest())))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/abrigos/a1"))
                .andExpect(jsonPath("$.id").value("a1"))
                .andExpect(jsonPath("$.endereco.cidade").value("Maringa"))
                .andExpect(jsonPath("$.demandas[0].deficit").value(30));
    }

    @Test
    void criar_nomeEmBrancoEEnderecoAusente_retorna400ComDetalhes() throws Exception {
        mockMvc.perform(post("/api/abrigos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"  \",\"capacidadeEstoque\":100}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detalhes").isNotEmpty());
    }

    @Test
    void criar_enderecoAninhadoIncompleto_retorna400() throws Exception {
        mockMvc.perform(post("/api/abrigos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Abrigo\",\"endereco\":{\"logradouro\":\"\",\"cidade\":\"Maringa\","
                                + "\"estado\":\"PR\"},\"capacidadeEstoque\":100}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void criar_capacidadeZero_retorna400() throws Exception {
        mockMvc.perform(post("/api/abrigos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Abrigo\",\"endereco\":{\"logradouro\":\"Rua A\",\"cidade\":\"Maringa\","
                                + "\"estado\":\"PR\"},\"capacidadeEstoque\":0}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void criar_demandaAninhadaComNecessariaZero_retorna400() throws Exception {
        mockMvc.perform(post("/api/abrigos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Abrigo\",\"endereco\":{\"logradouro\":\"Rua A\",\"cidade\":\"Maringa\","
                                + "\"estado\":\"PR\"},\"capacidadeEstoque\":100,\"demandas\":[{\"tipoPeca\":\"CASACO\","
                                + "\"tamanho\":\"M\",\"genero\":\"UNISSEX\",\"quantidadeNecessaria\":0}]}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void criar_regraDeDominioViolada_retorna400ComMensagemDoDominio() throws Exception {
        when(service.criar(any(AbrigoCreateRequest.class)))
                .thenThrow(new DadosInvalidosException("quantidadeAtendida nao pode exceder quantidadeNecessaria"));

        mockMvc.perform(post("/api/abrigos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(criarRequest())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro").value("quantidadeAtendida nao pode exceder quantidadeNecessaria"));
    }

    @Test
    void listar_retorna200ComLista() throws Exception {
        when(service.listar()).thenReturn(List.of(response("a1"), response("a2")));

        mockMvc.perform(get("/api/abrigos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void buscarPorId_existente_retorna200() throws Exception {
        when(service.buscarPorId("a1")).thenReturn(response("a1"));

        mockMvc.perform(get("/api/abrigos/a1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Abrigo Central"));
    }

    @Test
    void buscarPorId_inexistente_retorna404() throws Exception {
        when(service.buscarPorId("x")).thenThrow(new AbrigoNaoEncontradoException("x"));

        mockMvc.perform(get("/api/abrigos/x"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void atualizar_valido_retorna200() throws Exception {
        when(service.atualizar(eq("a1"), any(AbrigoUpdateRequest.class))).thenReturn(response("a1"));

        mockMvc.perform(put("/api/abrigos/a1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(atualizarRequest())))
                .andExpect(status().isOk());
    }

    @Test
    void atualizar_estoqueAusente_retorna400() throws Exception {
        mockMvc.perform(put("/api/abrigos/a1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Abrigo\",\"endereco\":{\"logradouro\":\"Rua A\",\"cidade\":\"Maringa\","
                                + "\"estado\":\"PR\"},\"capacidadeEstoque\":100}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void atualizar_inexistente_retorna404() throws Exception {
        when(service.atualizar(eq("x"), any(AbrigoUpdateRequest.class)))
                .thenThrow(new AbrigoNaoEncontradoException("x"));

        mockMvc.perform(put("/api/abrigos/x")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(atualizarRequest())))
                .andExpect(status().isNotFound());
    }

    @Test
    void remover_existente_retorna204SemCorpo() throws Exception {
        mockMvc.perform(delete("/api/abrigos/a1"))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(service).remover("a1");
    }

    @Test
    void remover_inexistente_retorna404() throws Exception {
        doThrow(new AbrigoNaoEncontradoException("x")).when(service).remover("x");

        mockMvc.perform(delete("/api/abrigos/x"))
                .andExpect(status().isNotFound());
    }

    @Test
    void remover_comVinculos_retorna409() throws Exception {
        doThrow(new RegistroEmUsoException("Abrigo", "a1")).when(service).remover("a1");

        mockMvc.perform(delete("/api/abrigos/a1"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void listarDemandas_retorna200ComLista() throws Exception {
        when(service.listarDemandas("a1")).thenReturn(List.of(demandaResponse(0)));

        mockMvc.perform(get("/api/abrigos/a1/demandas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].posicao").value(0));
    }

    @Test
    void listarDemandas_abrigoInexistente_retorna404() throws Exception {
        when(service.listarDemandas("x")).thenThrow(new AbrigoNaoEncontradoException("x"));

        mockMvc.perform(get("/api/abrigos/x/demandas"))
                .andExpect(status().isNotFound());
    }

    @Test
    void adicionarDemanda_valida_retorna201ComLocationDaPosicao() throws Exception {
        when(service.adicionarDemanda(eq("a1"), any(DemandaCreateRequest.class))).thenReturn(demandaResponse(1));

        mockMvc.perform(post("/api/abrigos/a1/demandas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(demandaCriarRequest())))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/abrigos/a1/demandas/1"))
                .andExpect(jsonPath("$.deficit").value(30));
    }

    @Test
    void adicionarDemanda_quantidadeNecessariaZero_retorna400() throws Exception {
        mockMvc.perform(post("/api/abrigos/a1/demandas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tipoPeca\":\"CASACO\",\"tamanho\":\"M\",\"genero\":\"UNISSEX\","
                                + "\"quantidadeNecessaria\":0}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void adicionarDemanda_abrigoInexistente_retorna404() throws Exception {
        when(service.adicionarDemanda(eq("x"), any(DemandaCreateRequest.class)))
                .thenThrow(new AbrigoNaoEncontradoException("x"));

        mockMvc.perform(post("/api/abrigos/x/demandas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(demandaCriarRequest())))
                .andExpect(status().isNotFound());
    }

    @Test
    void atualizarDemanda_valida_retorna200() throws Exception {
        when(service.atualizarDemanda(eq("a1"), eq(0), any(DemandaUpdateRequest.class)))
                .thenReturn(demandaResponse(0));

        mockMvc.perform(put("/api/abrigos/a1/demandas/0")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(demandaAtualizarRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.posicao").value(0));
    }

    @Test
    void atualizarDemanda_atendidaAusente_retorna400() throws Exception {
        mockMvc.perform(put("/api/abrigos/a1/demandas/0")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tipoPeca\":\"CASACO\",\"tamanho\":\"M\",\"genero\":\"UNISSEX\","
                                + "\"quantidadeNecessaria\":5}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void atualizarDemanda_posicaoNaoNumerica_retorna400() throws Exception {
        mockMvc.perform(put("/api/abrigos/a1/demandas/abc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(demandaAtualizarRequest())))
                .andExpect(status().isBadRequest());
    }

    @Test
    void atualizarDemanda_posicaoInexistente_retorna404() throws Exception {
        when(service.atualizarDemanda(eq("a1"), eq(9), any(DemandaUpdateRequest.class)))
                .thenThrow(new DemandaNaoEncontradaException(9));

        mockMvc.perform(put("/api/abrigos/a1/demandas/9")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(demandaAtualizarRequest())))
                .andExpect(status().isNotFound());
    }

    @Test
    void removerDemanda_existente_retorna204() throws Exception {
        mockMvc.perform(delete("/api/abrigos/a1/demandas/0"))
                .andExpect(status().isNoContent());

        verify(service).removerDemanda("a1", 0);
    }

    @Test
    void removerDemanda_posicaoInexistente_retorna404() throws Exception {
        doThrow(new DemandaNaoEncontradaException(9)).when(service).removerDemanda("a1", 9);

        mockMvc.perform(delete("/api/abrigos/a1/demandas/9"))
                .andExpect(status().isNotFound());
    }
}
