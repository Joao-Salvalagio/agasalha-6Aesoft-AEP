package br.com.cesumar.agasalha.controller;

import br.com.cesumar.agasalha.controller.dto.DoadorCreateRequest;
import br.com.cesumar.agasalha.controller.dto.DoadorResponse;
import br.com.cesumar.agasalha.controller.dto.DoadorUpdateRequest;
import br.com.cesumar.agasalha.exception.DoadorNaoEncontradoException;
import br.com.cesumar.agasalha.exception.RegistroEmUsoException;
import br.com.cesumar.agasalha.service.DoadorService;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DoadorController.class)
class DoadorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private DoadorService service;

    private DoadorResponse response(String id) {
        return new DoadorResponse(id, "Ana", "ana@exemplo.com");
    }

    @Test
    void criar_valido_retorna201ComLocationEBody() throws Exception {
        when(service.criar(any(DoadorCreateRequest.class))).thenReturn(response("d1"));

        mockMvc.perform(post("/api/doadores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new DoadorCreateRequest("Ana", "ana@exemplo.com"))))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/doadores/d1"))
                .andExpect(jsonPath("$.nome").value("Ana"));
    }

    @Test
    void criar_camposEmBranco_retorna400ComDetalhes() throws Exception {
        mockMvc.perform(post("/api/doadores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\" \",\"contato\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detalhes").isNotEmpty());
    }

    @Test
    void listar_retorna200ComLista() throws Exception {
        when(service.listar()).thenReturn(List.of(response("d1"), response("d2")));

        mockMvc.perform(get("/api/doadores"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void buscarPorId_existente_retorna200() throws Exception {
        when(service.buscarPorId("d1")).thenReturn(response("d1"));

        mockMvc.perform(get("/api/doadores/d1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contato").value("ana@exemplo.com"));
    }

    @Test
    void buscarPorId_inexistente_retorna404() throws Exception {
        when(service.buscarPorId("x")).thenThrow(new DoadorNaoEncontradoException("x"));

        mockMvc.perform(get("/api/doadores/x"))
                .andExpect(status().isNotFound());
    }

    @Test
    void atualizar_valido_retorna200() throws Exception {
        when(service.atualizar(eq("d1"), any(DoadorUpdateRequest.class))).thenReturn(response("d1"));

        mockMvc.perform(put("/api/doadores/d1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new DoadorUpdateRequest("Bia", "bia@exemplo.com"))))
                .andExpect(status().isOk());
    }

    @Test
    void atualizar_contatoAusente_retorna400() throws Exception {
        mockMvc.perform(put("/api/doadores/d1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Bia\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void atualizar_inexistente_retorna404() throws Exception {
        when(service.atualizar(eq("x"), any(DoadorUpdateRequest.class)))
                .thenThrow(new DoadorNaoEncontradoException("x"));

        mockMvc.perform(put("/api/doadores/x")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new DoadorUpdateRequest("Bia", "bia@exemplo.com"))))
                .andExpect(status().isNotFound());
    }

    @Test
    void remover_existente_retorna204() throws Exception {
        mockMvc.perform(delete("/api/doadores/d1"))
                .andExpect(status().isNoContent());

        verify(service).remover("d1");
    }

    @Test
    void remover_inexistente_retorna404() throws Exception {
        doThrow(new DoadorNaoEncontradoException("x")).when(service).remover("x");

        mockMvc.perform(delete("/api/doadores/x"))
                .andExpect(status().isNotFound());
    }

    @Test
    void remover_comDoacoes_retorna409() throws Exception {
        doThrow(new RegistroEmUsoException("Doador", "d1")).when(service).remover("d1");

        mockMvc.perform(delete("/api/doadores/d1"))
                .andExpect(status().isConflict());
    }
}
