package br.com.cesumar.agasalha.service;

import br.com.cesumar.agasalha.model.Abrigo;
import br.com.cesumar.agasalha.model.Demanda;
import br.com.cesumar.agasalha.model.Endereco;
import br.com.cesumar.agasalha.model.EstadoConservacao;
import br.com.cesumar.agasalha.model.Genero;
import br.com.cesumar.agasalha.model.ItemAgasalho;
import br.com.cesumar.agasalha.model.Tamanho;
import br.com.cesumar.agasalha.model.TipoPeca;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MatchingServiceTest {

    private final MatchingService service = new MatchingService();

    private static ItemAgasalho item(Tamanho tamanho, Genero genero) {
        return new ItemAgasalho(TipoPeca.CASACO, tamanho, genero,
                EstadoConservacao.USADO_BOM, "Maria", "maria@email.com");
    }

    private static Demanda demanda(Tamanho tamanho, Genero genero, int necessaria, int atendida) {
        return new Demanda(TipoPeca.CASACO, tamanho, genero, necessaria, atendida);
    }

    private static Abrigo abrigo(String id, int capacidade, int estoque, Demanda... demandas) {
        Abrigo abrigo = new Abrigo("Abrigo " + id, new Endereco("Rua A", "1", "Centro", "Maringa", "PR"),
                capacidade, estoque);
        abrigo.setId(id);
        abrigo.getDemandas().addAll(List.of(demandas));
        return abrigo;
    }

    @ParameterizedTest
    @CsvSource({
            "M, M, true",
            "M, G, false",
            "PP, GG, false",
            "GG, GG, true"
    })
    void sugerir_tamanhoDoItemEDaDemanda_casaSomenteQuandoIgual(Tamanho tamanhoItem,
                                                                Tamanho tamanhoDemanda,
                                                                boolean casa) {
        Abrigo abrigo = abrigo("a1", 10, 0, demanda(tamanhoDemanda, Genero.UNISSEX, 5, 0));

        Optional<SugestaoMatch> resultado = service.sugerir(item(tamanhoItem, Genero.UNISSEX), List.of(abrigo));

        assertEquals(casa, resultado.isPresent());
    }

    @ParameterizedTest
    @CsvSource({
            "MASCULINO, MASCULINO, true",
            "FEMININO, FEMININO, true",
            "UNISSEX, UNISSEX, true",
            "MASCULINO, FEMININO, false",
            "FEMININO, MASCULINO, false",
            "UNISSEX, MASCULINO, true",
            "UNISSEX, FEMININO, true",
            "MASCULINO, UNISSEX, true",
            "FEMININO, UNISSEX, true"
    })
    void sugerir_generoDoItemEDaDemanda_seguePlanilhaDeCompatibilidade(Genero generoItem,
                                                                       Genero generoDemanda,
                                                                       boolean casa) {
        Abrigo abrigo = abrigo("a1", 10, 0, demanda(Tamanho.M, generoDemanda, 5, 0));

        Optional<SugestaoMatch> resultado = service.sugerir(item(Tamanho.M, generoItem), List.of(abrigo));

        assertEquals(casa, resultado.isPresent());
    }

    @ParameterizedTest
    @CsvSource({
            "10, 0, true",
            "10, 8, true",
            "10, 9, true",
            "10, 10, false",
            "10, 11, false",
            "1, 0, true",
            "1, 1, false"
    })
    void sugerir_estoqueDoAbrigo_bloqueiaQuandoEstoqueMaisUmExcedeCapacidade(int capacidade,
                                                                             int estoque,
                                                                             boolean casa) {
        Abrigo abrigo = abrigo("a1", capacidade, estoque, demanda(Tamanho.M, Genero.UNISSEX, 5, 0));

        Optional<SugestaoMatch> resultado = service.sugerir(item(Tamanho.M, Genero.UNISSEX), List.of(abrigo));

        assertEquals(casa, resultado.isPresent());
    }

    @ParameterizedTest
    @CsvSource({
            "5, 0, true",
            "5, 4, true",
            "5, 5, false",
            "1, 0, true",
            "1, 1, false"
    })
    void sugerir_demandaEmAberto_casaSomenteQuandoAtendidaMenorQueNecessaria(int necessaria,
                                                                             int atendida,
                                                                             boolean casa) {
        Abrigo abrigo = abrigo("a1", 10, 0, demanda(Tamanho.M, Genero.UNISSEX, necessaria, atendida));

        Optional<SugestaoMatch> resultado = service.sugerir(item(Tamanho.M, Genero.UNISSEX), List.of(abrigo));

        assertEquals(casa, resultado.isPresent());
    }

    @Test
    void sugerir_doisAbrigosCompativeis_escolheOMaiorDeficit() {
        Abrigo menor = abrigo("a1", 10, 0, demanda(Tamanho.M, Genero.UNISSEX, 5, 3));
        Abrigo maior = abrigo("a2", 10, 0, demanda(Tamanho.M, Genero.UNISSEX, 9, 2));

        Optional<SugestaoMatch> resultado = service.sugerir(item(Tamanho.M, Genero.UNISSEX), List.of(menor, maior));

        assertSame(maior, resultado.orElseThrow().abrigo());
        assertEquals(7, resultado.orElseThrow().demanda().deficit());
    }

    @Test
    void sugerir_doisAbrigosCompativeis_ordemDaListaNaoAlteraEscolha() {
        Abrigo menor = abrigo("a1", 10, 0, demanda(Tamanho.M, Genero.UNISSEX, 5, 3));
        Abrigo maior = abrigo("a2", 10, 0, demanda(Tamanho.M, Genero.UNISSEX, 9, 2));

        Optional<SugestaoMatch> resultado = service.sugerir(item(Tamanho.M, Genero.UNISSEX), List.of(maior, menor));

        assertSame(maior, resultado.orElseThrow().abrigo());
    }

    @Test
    void sugerir_empateDeDeficit_escolheMenorIdDeAbrigo() {
        Abrigo b = abrigo("b", 10, 0, demanda(Tamanho.M, Genero.UNISSEX, 5, 0));
        Abrigo a = abrigo("a", 10, 0, demanda(Tamanho.M, Genero.UNISSEX, 5, 0));

        Optional<SugestaoMatch> resultado = service.sugerir(item(Tamanho.M, Genero.UNISSEX), List.of(b, a));

        assertSame(a, resultado.orElseThrow().abrigo());
    }

    @Test
    void sugerir_abrigoMaiorDeficitSemEspaco_escolheOutroCompativel() {
        Abrigo cheio = abrigo("a1", 10, 10, demanda(Tamanho.M, Genero.UNISSEX, 20, 0));
        Abrigo comEspaco = abrigo("a2", 10, 0, demanda(Tamanho.M, Genero.UNISSEX, 2, 0));

        Optional<SugestaoMatch> resultado = service.sugerir(item(Tamanho.M, Genero.UNISSEX), List.of(cheio, comEspaco));

        assertSame(comEspaco, resultado.orElseThrow().abrigo());
    }

    @Test
    void sugerir_abrigoMaiorDeficitComDemandaAtendida_escolheOutroCompativel() {
        Abrigo atendido = abrigo("a1", 10, 0, demanda(Tamanho.M, Genero.UNISSEX, 5, 5));
        Abrigo aberto = abrigo("a2", 10, 0, demanda(Tamanho.M, Genero.UNISSEX, 2, 0));

        Optional<SugestaoMatch> resultado = service.sugerir(item(Tamanho.M, Genero.UNISSEX), List.of(atendido, aberto));

        assertSame(aberto, resultado.orElseThrow().abrigo());
    }

    @Test
    void sugerir_abrigoComVariasDemandas_escolheDemandaCompativelDeMaiorDeficit() {
        Demanda incompativel = demanda(Tamanho.G, Genero.UNISSEX, 50, 0);
        Demanda pequena = demanda(Tamanho.M, Genero.UNISSEX, 3, 0);
        Demanda grande = demanda(Tamanho.M, Genero.UNISSEX, 8, 0);
        Abrigo abrigo = abrigo("a1", 10, 0, incompativel, pequena, grande);

        Optional<SugestaoMatch> resultado = service.sugerir(item(Tamanho.M, Genero.UNISSEX), List.of(abrigo));

        assertSame(grande, resultado.orElseThrow().demanda());
    }

    @Test
    void sugerir_abrigoSemDemandas_retornaVazio() {
        Abrigo abrigo = abrigo("a1", 10, 0);

        Optional<SugestaoMatch> resultado = service.sugerir(item(Tamanho.M, Genero.UNISSEX), List.of(abrigo));

        assertTrue(resultado.isEmpty());
    }

    @Test
    void sugerir_listaDeAbrigosVazia_retornaVazio() {
        Optional<SugestaoMatch> resultado = service.sugerir(item(Tamanho.M, Genero.UNISSEX), List.of());

        assertTrue(resultado.isEmpty());
    }
}
