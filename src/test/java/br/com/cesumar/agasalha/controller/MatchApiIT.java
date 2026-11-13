package br.com.cesumar.agasalha.controller;

import br.com.cesumar.agasalha.model.Abrigo;
import br.com.cesumar.agasalha.model.Demanda;
import br.com.cesumar.agasalha.model.Endereco;
import br.com.cesumar.agasalha.model.EstadoConservacao;
import br.com.cesumar.agasalha.model.Genero;
import br.com.cesumar.agasalha.model.ItemAgasalho;
import br.com.cesumar.agasalha.model.Tamanho;
import br.com.cesumar.agasalha.model.TipoPeca;
import br.com.cesumar.agasalha.repository.AbrigoRepository;
import br.com.cesumar.agasalha.repository.ItemRepository;
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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class MatchApiIT extends MongoIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private AbrigoRepository abrigoRepository;

    @BeforeEach
    void limparColecoes() {
        itemRepository.deleteAll();
        abrigoRepository.deleteAll();
    }

    private ItemAgasalho salvarItem(Tamanho tamanho, Genero genero) {
        return itemRepository.save(new ItemAgasalho(TipoPeca.CASACO, tamanho, genero,
                EstadoConservacao.USADO_BOM, "Ana", "ana@exemplo.com"));
    }

    private Abrigo salvarAbrigo(String nome, int capacidade, int estoque, Demanda... demandas) {
        Abrigo abrigo = new Abrigo(nome, new Endereco("Rua A", "1", "Centro", "Maringa", "PR"),
                capacidade, estoque);
        for (Demanda demanda : demandas) {
            abrigo.adicionarDemanda(demanda);
        }
        return abrigoRepository.save(abrigo);
    }

    private Demanda demanda(Tamanho tamanho, Genero genero, int necessaria, int atendida) {
        return new Demanda(TipoPeca.CASACO, tamanho, genero, necessaria, atendida);
    }

    private String corpo(String itemId) {
        return "{\"itemId\":\"" + itemId + "\"}";
    }

    @Test
    void sugerir_doisAbrigosCompativeis_devolveODeMaiorDeficit() throws Exception {
        ItemAgasalho item = salvarItem(Tamanho.M, Genero.UNISSEX);
        salvarAbrigo("Menor", 100, 0, demanda(Tamanho.M, Genero.UNISSEX, 5, 3));
        Abrigo maior = salvarAbrigo("Maior", 100, 0, demanda(Tamanho.M, Genero.UNISSEX, 20, 5));

        mockMvc.perform(post("/api/matches")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpo(item.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.itemId").value(item.getId()))
                .andExpect(jsonPath("$.abrigoId").value(maior.getId()))
                .andExpect(jsonPath("$.abrigoNome").value("Maior"))
                .andExpect(jsonPath("$.tamanho").value("M"))
                .andExpect(jsonPath("$.deficit").value(15));
    }

    @Test
    void sugerir_abrigoDeMaiorDeficitCheio_devolveOutroComEspaco() throws Exception {
        ItemAgasalho item = salvarItem(Tamanho.M, Genero.UNISSEX);
        salvarAbrigo("Cheio", 10, 10, demanda(Tamanho.M, Genero.UNISSEX, 50, 0));
        Abrigo comEspaco = salvarAbrigo("ComEspaco", 10, 0, demanda(Tamanho.M, Genero.UNISSEX, 3, 0));

        mockMvc.perform(post("/api/matches")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpo(item.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.abrigoId").value(comEspaco.getId()));
    }

    @Test
    void sugerir_demandaAtendidaEOutraAberta_devolveAAberta() throws Exception {
        ItemAgasalho item = salvarItem(Tamanho.M, Genero.UNISSEX);
        salvarAbrigo("Atendido", 100, 0, demanda(Tamanho.M, Genero.UNISSEX, 5, 5));
        Abrigo aberto = salvarAbrigo("Aberto", 100, 0, demanda(Tamanho.M, Genero.UNISSEX, 2, 0));

        mockMvc.perform(post("/api/matches")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpo(item.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.abrigoId").value(aberto.getId()));
    }

    @Test
    void sugerir_generoIncompativel_retorna404() throws Exception {
        ItemAgasalho item = salvarItem(Tamanho.M, Genero.MASCULINO);
        salvarAbrigo("Feminino", 100, 0, demanda(Tamanho.M, Genero.FEMININO, 10, 0));

        mockMvc.perform(post("/api/matches")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpo(item.getId())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void sugerir_semAbrigosCadastrados_retorna404() throws Exception {
        ItemAgasalho item = salvarItem(Tamanho.M, Genero.UNISSEX);

        mockMvc.perform(post("/api/matches")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpo(item.getId())))
                .andExpect(status().isNotFound());
    }

    @Test
    void sugerir_itemInexistente_retorna404() throws Exception {
        salvarAbrigo("Qualquer", 100, 0, demanda(Tamanho.M, Genero.UNISSEX, 10, 0));

        mockMvc.perform(post("/api/matches")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpo("inexistente")))
                .andExpect(status().isNotFound());
    }

    @Test
    void sugerir_itemIdEmBranco_retorna400() throws Exception {
        mockMvc.perform(post("/api/matches")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpo("")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void sugerir_depoisDeSugerir_naoAlteraAbrigoNemDemanda() throws Exception {
        ItemAgasalho item = salvarItem(Tamanho.M, Genero.UNISSEX);
        Abrigo abrigo = salvarAbrigo("Unico", 100, 7, demanda(Tamanho.M, Genero.UNISSEX, 10, 4));

        mockMvc.perform(post("/api/matches")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpo(item.getId())))
                .andExpect(status().isOk());

        Abrigo recarregado = abrigoRepository.findById(abrigo.getId()).orElseThrow();
        assertEquals(7, recarregado.getEstoqueAtual());
        assertEquals(4, recarregado.getDemandas().get(0).getQuantidadeAtendida());
    }

    @Test
    void confirmar_itemComAbrigoCompativel_alocaAtualizaNumerosEReserva() throws Exception {
        ItemAgasalho item = salvarItem(Tamanho.M, Genero.UNISSEX);
        Abrigo abrigo = salvarAbrigo("Central", 10, 2, demanda(Tamanho.M, Genero.UNISSEX, 5, 1));

        mockMvc.perform(post("/api/matches/confirmacao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpo(item.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.abrigoId").value(abrigo.getId()))
                .andExpect(jsonPath("$.status").value("RESERVADO"));

        mockMvc.perform(get("/api/abrigos/" + abrigo.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estoqueAtual").value(3))
                .andExpect(jsonPath("$.demandas[0].quantidadeAtendida").value(2))
                .andExpect(jsonPath("$.demandas[0].deficit").value(3));
        mockMvc.perform(get("/api/itens/" + item.getId()))
                .andExpect(jsonPath("$.abrigoId").value(abrigo.getId()))
                .andExpect(jsonPath("$.status").value("RESERVADO"));
    }

    @Test
    void confirmar_segundaVezNoMesmoItem_retorna409SemMudarOAbrigo() throws Exception {
        ItemAgasalho item = salvarItem(Tamanho.M, Genero.UNISSEX);
        Abrigo abrigo = salvarAbrigo("Central", 10, 0, demanda(Tamanho.M, Genero.UNISSEX, 5, 0));
        mockMvc.perform(post("/api/matches/confirmacao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpo(item.getId())))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/matches/confirmacao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpo(item.getId())))
                .andExpect(status().isConflict());

        assertEquals(1, abrigoRepository.findById(abrigo.getId()).orElseThrow().getEstoqueAtual());
    }

    @Test
    void confirmar_abrigoSemEspaco_retorna404ENaoGravaNada() throws Exception {
        ItemAgasalho item = salvarItem(Tamanho.M, Genero.UNISSEX);
        Abrigo abrigo = salvarAbrigo("Cheio", 5, 5, demanda(Tamanho.M, Genero.UNISSEX, 5, 0));

        mockMvc.perform(post("/api/matches/confirmacao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpo(item.getId())))
                .andExpect(status().isNotFound());

        assertEquals(5, abrigoRepository.findById(abrigo.getId()).orElseThrow().getEstoqueAtual());
        assertNull(itemRepository.findById(item.getId()).orElseThrow().getAbrigoId());
    }

    @Test
    void confirmar_itemInexistente_retorna404() throws Exception {
        mockMvc.perform(post("/api/matches/confirmacao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpo("inexistente")))
                .andExpect(status().isNotFound());
    }

    @Test
    void enviarAoAbrigo_reservaELiberaAEntrega() throws Exception {
        ItemAgasalho item = salvarItem(Tamanho.M, Genero.UNISSEX);
        salvarAbrigo("Central", 10, 0, demanda(Tamanho.M, Genero.UNISSEX, 5, 0));

        mockMvc.perform(post("/api/itens/" + item.getId() + "/reserva"))
                .andExpect(status().isConflict());

        mockMvc.perform(post("/api/matches/confirmacao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpo(item.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("RESERVADO"));

        mockMvc.perform(post("/api/itens/" + item.getId() + "/entrega"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ENTREGUE"));
    }

    @Test
    void sugerir_pecaDeTipoDiferenteDaDemanda_retorna404() throws Exception {
        ItemAgasalho blusa = itemRepository.save(new ItemAgasalho(TipoPeca.BLUSA, Tamanho.M, Genero.UNISSEX,
                EstadoConservacao.USADO_BOM, "Ana", "ana@exemplo.com"));
        Abrigo abrigo = new Abrigo("Central", new Endereco("Rua A", "1", "Centro", "Maringa", "PR"), 10, 0);
        abrigo.adicionarDemanda(new Demanda(TipoPeca.MEIA, Tamanho.M, Genero.UNISSEX, 40, 15));
        abrigoRepository.save(abrigo);

        mockMvc.perform(post("/api/matches")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpo(blusa.getId())))
                .andExpect(status().isNotFound());
    }
}
