package br.com.cesumar.agasalha.model;

import br.com.cesumar.agasalha.exception.DadosInvalidosException;
import br.com.cesumar.agasalha.exception.DemandaNaoEncontradaException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AbrigoTest {

    private static final Endereco ENDERECO = new Endereco("Rua A", "1", "Centro", "Maringa", "PR");

    private Abrigo valido() {
        return new Abrigo("Abrigo Central", ENDERECO, 100, 10);
    }

    private Demanda demanda(int necessaria, int atendida) {
        return new Demanda(TipoPeca.CASACO, Tamanho.M, Genero.UNISSEX, necessaria, atendida);
    }

    @ParameterizedTest
    @CsvSource({
            "10, 0, true",
            "10, 9, true",
            "10, 10, false",
            "10, 11, false",
            "1, 0, true",
            "1, 1, false"
    })
    void temEspaco_estoqueMaisUmContraCapacidade_respeitaLimite(int capacidade, int estoque, boolean esperado) {
        Abrigo abrigo = new Abrigo("A", ENDERECO, capacidade, estoque);

        assertEquals(esperado, abrigo.temEspaco());
    }

    @Test
    void validar_abrigoValido_naoLanca() {
        assertDoesNotThrow(() -> valido().validar());
    }

    @Test
    void validar_estoqueAtualZero_naoLanca() {
        Abrigo abrigo = new Abrigo("A", ENDERECO, 1, 0);

        assertDoesNotThrow(abrigo::validar);
    }

    @Test
    void validar_nomeNulo_lancaDadosInvalidos() {
        Abrigo abrigo = valido();
        abrigo.setNome(null);

        assertThrows(DadosInvalidosException.class, abrigo::validar);
    }

    @Test
    void validar_nomeEmBranco_lancaDadosInvalidos() {
        Abrigo abrigo = valido();
        abrigo.setNome("  ");

        assertThrows(DadosInvalidosException.class, abrigo::validar);
    }

    @Test
    void validar_enderecoNulo_lancaDadosInvalidos() {
        Abrigo abrigo = valido();
        abrigo.setEndereco(null);

        assertThrows(DadosInvalidosException.class, abrigo::validar);
    }

    @ParameterizedTest
    @CsvSource({"0", "-1"})
    void validar_capacidadeNaoPositiva_lancaDadosInvalidos(int capacidade) {
        Abrigo abrigo = valido();
        abrigo.setCapacidadeEstoque(capacidade);

        assertThrows(DadosInvalidosException.class, abrigo::validar);
    }

    @Test
    void validar_estoqueAtualNegativo_lancaDadosInvalidos() {
        Abrigo abrigo = valido();
        abrigo.setEstoqueAtual(-1);

        assertThrows(DadosInvalidosException.class, abrigo::validar);
    }

    @Test
    void validar_demandasNulas_lancaDadosInvalidos() {
        Abrigo abrigo = valido();
        abrigo.setDemandas(null);

        assertThrows(DadosInvalidosException.class, abrigo::validar);
    }

    @Test
    void validar_demandaComAtendidaMaiorQueNecessaria_lancaDadosInvalidos() {
        Abrigo abrigo = valido();
        abrigo.getDemandas().add(demanda(5, 6));

        assertThrows(DadosInvalidosException.class, abrigo::validar);
    }

    @Test
    void adicionarDemanda_demandaValida_incluiNaLista() {
        Abrigo abrigo = valido();
        Demanda nova = demanda(5, 0);

        abrigo.adicionarDemanda(nova);

        assertEquals(1, abrigo.getDemandas().size());
        assertSame(nova, abrigo.getDemandas().get(0));
    }

    @Test
    void adicionarDemanda_demandaInvalida_lancaEMantemListaVazia() {
        Abrigo abrigo = valido();

        assertThrows(DadosInvalidosException.class, () -> abrigo.adicionarDemanda(demanda(5, 6)));

        assertTrue(abrigo.getDemandas().isEmpty());
    }

    @Test
    void atualizarDemanda_posicaoExistente_substituiDemanda() {
        Abrigo abrigo = valido();
        abrigo.adicionarDemanda(demanda(5, 0));
        Demanda nova = demanda(8, 2);

        abrigo.atualizarDemanda(0, nova);

        assertSame(nova, abrigo.getDemandas().get(0));
        assertEquals(1, abrigo.getDemandas().size());
    }

    @Test
    void atualizarDemanda_demandaInvalida_lancaEMantemOriginal() {
        Abrigo abrigo = valido();
        Demanda original = demanda(5, 0);
        abrigo.adicionarDemanda(original);

        assertThrows(DadosInvalidosException.class, () -> abrigo.atualizarDemanda(0, demanda(5, 9)));

        assertSame(original, abrigo.getDemandas().get(0));
    }

    @ParameterizedTest
    @CsvSource({"-1", "1", "5"})
    void atualizarDemanda_posicaoInexistente_lancaDemandaNaoEncontrada(int posicao) {
        Abrigo abrigo = valido();
        abrigo.adicionarDemanda(demanda(5, 0));

        assertThrows(DemandaNaoEncontradaException.class,
                () -> abrigo.atualizarDemanda(posicao, demanda(5, 0)));
    }

    @Test
    void removerDemanda_posicaoExistente_removeDaLista() {
        Abrigo abrigo = valido();
        Demanda primeira = demanda(5, 0);
        Demanda segunda = demanda(7, 1);
        abrigo.adicionarDemanda(primeira);
        abrigo.adicionarDemanda(segunda);

        abrigo.removerDemanda(0);

        assertEquals(1, abrigo.getDemandas().size());
        assertSame(segunda, abrigo.getDemandas().get(0));
    }

    @ParameterizedTest
    @CsvSource({"-1", "0", "3"})
    void removerDemanda_posicaoInexistente_lancaDemandaNaoEncontrada(int posicao) {
        Abrigo abrigo = valido();

        assertThrows(DemandaNaoEncontradaException.class, () -> abrigo.removerDemanda(posicao));
    }

    @Test
    void removerDemanda_ultimaDemanda_deixaListaVazia() {
        Abrigo abrigo = valido();
        abrigo.adicionarDemanda(demanda(5, 0));

        abrigo.removerDemanda(0);

        assertFalse(abrigo.getDemandas().iterator().hasNext());
    }

    @Test
    void receberPeca_comEspacoEDemandaAberta_incrementaEstoqueEAtendida() {
        Abrigo abrigo = new Abrigo("A", ENDERECO, 10, 3);
        Demanda demanda = demanda(5, 2);

        abrigo.receberPeca(demanda);

        assertEquals(4, abrigo.getEstoqueAtual());
        assertEquals(3, demanda.getQuantidadeAtendida());
        assertEquals(2, demanda.deficit());
    }

    @Test
    void receberPeca_ultimaVagaDoEstoque_aceita() {
        Abrigo abrigo = new Abrigo("A", ENDERECO, 10, 9);

        assertDoesNotThrow(() -> abrigo.receberPeca(demanda(5, 0)));

        assertEquals(10, abrigo.getEstoqueAtual());
    }

    @Test
    void receberPeca_semEspaco_lancaENaoAlteraNada() {
        Abrigo abrigo = new Abrigo("A", ENDERECO, 10, 10);
        Demanda demanda = demanda(5, 0);

        assertThrows(DadosInvalidosException.class, () -> abrigo.receberPeca(demanda));

        assertEquals(10, abrigo.getEstoqueAtual());
        assertEquals(0, demanda.getQuantidadeAtendida());
    }

    @Test
    void receberPeca_demandaJaAtendida_lancaENaoAlteraNada() {
        Abrigo abrigo = new Abrigo("A", ENDERECO, 10, 3);
        Demanda demanda = demanda(5, 5);

        assertThrows(DadosInvalidosException.class, () -> abrigo.receberPeca(demanda));

        assertEquals(3, abrigo.getEstoqueAtual());
        assertEquals(5, demanda.getQuantidadeAtendida());
    }
}
