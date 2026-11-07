package br.com.cesumar.agasalha.controller;

import br.com.cesumar.agasalha.controller.dto.MatchRequest;
import br.com.cesumar.agasalha.controller.dto.MatchResponse;
import br.com.cesumar.agasalha.exception.ItemNaoEncontradoException;
import br.com.cesumar.agasalha.exception.MatchNaoEncontradoException;
import br.com.cesumar.agasalha.model.Genero;
import br.com.cesumar.agasalha.model.Tamanho;
import br.com.cesumar.agasalha.model.TipoPeca;
import br.com.cesumar.agasalha.service.MatchService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MatchController.class)
class MatchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MatchService matchService;

    @Test
    void sugerir_itemComMatch_retorna200ComSugestao() throws Exception {
        when(matchService.sugerir("i1")).thenReturn(new MatchResponse(
                "i1", "a1", "Abrigo Central", TipoPeca.CASACO, Tamanho.M, Genero.UNISSEX, 7));

        mockMvc.perform(post("/api/matches")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new MatchRequest("i1"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.itemId").value("i1"))
                .andExpect(jsonPath("$.abrigoId").value("a1"))
                .andExpect(jsonPath("$.abrigoNome").value("Abrigo Central"))
                .andExpect(jsonPath("$.tipoPeca").value("CASACO"))
                .andExpect(jsonPath("$.tamanho").value("M"))
                .andExpect(jsonPath("$.genero").value("UNISSEX"))
                .andExpect(jsonPath("$.deficit").value(7));
    }

    @Test
    void sugerir_itemIdEmBranco_retorna400SemChamarService() throws Exception {
        mockMvc.perform(post("/api/matches")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"itemId\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        verify(matchService, never()).sugerir(org.mockito.ArgumentMatchers.anyString());
    }

    @Test
    void sugerir_corpoSemItemId_retorna400() throws Exception {
        mockMvc.perform(post("/api/matches")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void sugerir_itemInexistente_retorna404() throws Exception {
        when(matchService.sugerir("i1")).thenThrow(new ItemNaoEncontradoException("i1"));

        mockMvc.perform(post("/api/matches")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"itemId\":\"i1\"}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void sugerir_semAbrigoCompativel_retorna404() throws Exception {
        when(matchService.sugerir("i1")).thenThrow(new MatchNaoEncontradoException("i1"));

        mockMvc.perform(post("/api/matches")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"itemId\":\"i1\"}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.erro").value("Nenhum abrigo compativel para o item: i1"));
    }
}
