package br.com.cesumar.agasalha.service;

import br.com.cesumar.agasalha.controller.dto.AbrigoCreateRequest;
import br.com.cesumar.agasalha.controller.dto.AbrigoResponse;
import br.com.cesumar.agasalha.controller.dto.AbrigoUpdateRequest;
import br.com.cesumar.agasalha.controller.dto.DemandaCreateRequest;
import br.com.cesumar.agasalha.controller.dto.DemandaResponse;
import br.com.cesumar.agasalha.controller.dto.DemandaUpdateRequest;
import br.com.cesumar.agasalha.controller.dto.EnderecoRequest;
import br.com.cesumar.agasalha.exception.AbrigoNaoEncontradoException;
import br.com.cesumar.agasalha.exception.DadosInvalidosException;
import br.com.cesumar.agasalha.exception.DemandaNaoEncontradaException;
import br.com.cesumar.agasalha.exception.RegistroEmUsoException;
import br.com.cesumar.agasalha.mapper.AbrigoMapper;
import br.com.cesumar.agasalha.model.Abrigo;
import br.com.cesumar.agasalha.model.Demanda;
import br.com.cesumar.agasalha.model.Endereco;
import br.com.cesumar.agasalha.model.Genero;
import br.com.cesumar.agasalha.model.Tamanho;
import br.com.cesumar.agasalha.model.TipoPeca;
import br.com.cesumar.agasalha.repository.AbrigoRepository;
import br.com.cesumar.agasalha.repository.DoacaoRepository;
import br.com.cesumar.agasalha.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AbrigoServiceTest {

    private AbrigoRepository repository;
    private DoacaoRepository doacaoRepository;
    private ItemRepository itemRepository;
    private AbrigoService service;

    @BeforeEach
    void setUp() {
        repository = mock(AbrigoRepository.class);
        doacaoRepository = mock(DoacaoRepository.class);
        itemRepository = mock(ItemRepository.class);
        service = new AbrigoService(repository, doacaoRepository, itemRepository, new AbrigoMapper());
    }

    private EnderecoRequest endereco() {
        return new EnderecoRequest("Rua A", "120", "Centro", "Maringa", "PR");
    }

    private DemandaCreateRequest demandaCriar(int necessaria, Integer atendida) {
        return new DemandaCreateRequest(TipoPeca.CASACO, Tamanho.M, Genero.UNISSEX, necessaria, atendida);
    }

    private DemandaUpdateRequest demandaAtualizar(int necessaria, int atendida) {
        return new DemandaUpdateRequest(TipoPeca.COBERTOR, Tamanho.G, Genero.MASCULINO, necessaria, atendida);
    }

    private Abrigo abrigo(String id) {
        Abrigo abrigo = new Abrigo("Abrigo Central", new Endereco("Rua A", "120", "Centro", "Maringa", "PR"), 100, 10);
        abrigo.setId(id);
        return abrigo;
    }

    private Abrigo abrigoComDemanda(String id) {
        Abrigo abrigo = abrigo(id);
        abrigo.adicionarDemanda(new Demanda(TipoPeca.CASACO, Tamanho.M, Genero.UNISSEX, 30, 10));
        return abrigo;
    }

    @Test
    void criar_valido_salvaComDemandasAninhadas() {
        when(repository.save(any(Abrigo.class))).thenAnswer(i -> {
            Abrigo abrigo = i.getArgument(0);
            abrigo.setId("novo");
            return abrigo;
        });

        AbrigoResponse resposta = service.criar(new AbrigoCreateRequest("Abrigo Central", endereco(), 100, null,
                List.of(demandaCriar(30, null))));

        assertEquals("novo", resposta.id());
        assertEquals(0, resposta.estoqueAtual());
        assertEquals(1, resposta.demandas().size());
        assertEquals(30, resposta.demandas().get(0).deficit());
    }

    @Test
    void criar_semDemandas_salvaListaVazia() {
        when(repository.save(any(Abrigo.class))).thenAnswer(i -> i.getArgument(0));

        AbrigoResponse resposta = service.criar(new AbrigoCreateRequest("Abrigo Central", endereco(), 100, 0, null));

        assertTrue(resposta.demandas().isEmpty());
    }

    @Test
    void criar_demandaAtendidaAcimaDaNecessaria_lancaDadosInvalidosENaoSalva() {
        AbrigoCreateRequest requisicao = new AbrigoCreateRequest("Abrigo Central", endereco(), 100, 0,
                List.of(demandaCriar(2, 3)));

        assertThrows(DadosInvalidosException.class, () -> service.criar(requisicao));
        verify(repository, never()).save(any());
    }

    @Test
    void buscarPorId_existente_devolveResponse() {
        when(repository.findById("a1")).thenReturn(Optional.of(abrigo("a1")));

        assertEquals("a1", service.buscarPorId("a1").id());
    }

    @Test
    void buscarPorId_inexistente_lancaAbrigoNaoEncontrado() {
        when(repository.findById("x")).thenReturn(Optional.empty());

        assertThrows(AbrigoNaoEncontradoException.class, () -> service.buscarPorId("x"));
    }

    @Test
    void listar_devolveTodosOsAbrigos() {
        when(repository.findAll()).thenReturn(List.of(abrigo("a1"), abrigo("a2")));

        assertEquals(2, service.listar().size());
    }

    @Test
    void atualizar_valido_alteraDadosEPreservaDemandas() {
        when(repository.findById("a1")).thenReturn(Optional.of(abrigoComDemanda("a1")));
        when(repository.save(any(Abrigo.class))).thenAnswer(i -> i.getArgument(0));

        AbrigoResponse resposta = service.atualizar("a1", new AbrigoUpdateRequest("Novo Nome", endereco(), 200, 40));

        assertEquals("Novo Nome", resposta.nome());
        assertEquals(200, resposta.capacidadeEstoque());
        assertEquals(40, resposta.estoqueAtual());
        assertEquals(1, resposta.demandas().size());
    }

    @Test
    void atualizar_inexistente_lancaAbrigoNaoEncontrado() {
        when(repository.findById("x")).thenReturn(Optional.empty());

        assertThrows(AbrigoNaoEncontradoException.class,
                () -> service.atualizar("x", new AbrigoUpdateRequest("Nome", endereco(), 10, 0)));
    }

    @Test
    void atualizar_demandaPreexistenteInvalida_lancaDadosInvalidosENaoSalva() {
        Abrigo existente = abrigoComDemanda("a1");
        existente.getDemandas().get(0).setQuantidadeAtendida(99);
        when(repository.findById("a1")).thenReturn(Optional.of(existente));

        assertThrows(DadosInvalidosException.class,
                () -> service.atualizar("a1", new AbrigoUpdateRequest("Nome", endereco(), 10, 0)));
        verify(repository, never()).save(any());
    }

    @Test
    void atualizar_estoqueNegativo_lancaDadosInvalidosENaoSalva() {
        when(repository.findById("a1")).thenReturn(Optional.of(abrigo("a1")));

        assertThrows(DadosInvalidosException.class,
                () -> service.atualizar("a1", new AbrigoUpdateRequest("Nome", endereco(), 10, -1)));
        verify(repository, never()).save(any());
    }

    @Test
    void remover_semVinculos_removeDoRepositorio() {
        when(repository.existsById("a1")).thenReturn(true);

        service.remover("a1");

        verify(repository).deleteById("a1");
    }

    @Test
    void remover_inexistente_lancaAbrigoNaoEncontrado() {
        when(repository.existsById("x")).thenReturn(false);

        assertThrows(AbrigoNaoEncontradoException.class, () -> service.remover("x"));
        verify(repository, never()).deleteById(any());
    }

    @Test
    void remover_comDoacaoVinculada_lancaRegistroEmUso() {
        when(repository.existsById("a1")).thenReturn(true);
        when(doacaoRepository.existsByAbrigoId("a1")).thenReturn(true);

        assertThrows(RegistroEmUsoException.class, () -> service.remover("a1"));
        verify(repository, never()).deleteById(any());
    }

    @Test
    void remover_comItemCasadoAoAbrigo_lancaRegistroEmUso() {
        when(repository.existsById("a1")).thenReturn(true);
        when(itemRepository.existsByAbrigoId("a1")).thenReturn(true);

        assertThrows(RegistroEmUsoException.class, () -> service.remover("a1"));
        verify(repository, never()).deleteById(any());
    }

    @Test
    void listarDemandas_devolveDemandasComPosicaoEDeficit() {
        when(repository.findById("a1")).thenReturn(Optional.of(abrigoComDemanda("a1")));

        List<DemandaResponse> demandas = service.listarDemandas("a1");

        assertEquals(1, demandas.size());
        assertEquals(0, demandas.get(0).posicao());
        assertEquals(20, demandas.get(0).deficit());
    }

    @Test
    void listarDemandas_abrigoInexistente_lancaAbrigoNaoEncontrado() {
        when(repository.findById("x")).thenReturn(Optional.empty());

        assertThrows(AbrigoNaoEncontradoException.class, () -> service.listarDemandas("x"));
    }

    @Test
    void adicionarDemanda_valida_salvaEDevolveAPosicaoDaNova() {
        Abrigo abrigo = abrigoComDemanda("a1");
        when(repository.findById("a1")).thenReturn(Optional.of(abrigo));

        DemandaResponse resposta = service.adicionarDemanda("a1", demandaCriar(8, 2));

        assertEquals(1, resposta.posicao());
        assertEquals(6, resposta.deficit());
        assertEquals(2, abrigo.getDemandas().size());
        verify(repository).save(abrigo);
    }

    @Test
    void adicionarDemanda_abrigoInexistente_lancaAbrigoNaoEncontrado() {
        when(repository.findById("x")).thenReturn(Optional.empty());

        assertThrows(AbrigoNaoEncontradoException.class, () -> service.adicionarDemanda("x", demandaCriar(5, 0)));
    }

    @Test
    void adicionarDemanda_atendidaAcimaDaNecessaria_lancaDadosInvalidosENaoSalva() {
        when(repository.findById("a1")).thenReturn(Optional.of(abrigo("a1")));

        assertThrows(DadosInvalidosException.class, () -> service.adicionarDemanda("a1", demandaCriar(2, 3)));
        verify(repository, never()).save(any());
    }

    @Test
    void atualizarDemanda_valida_substituiNaPosicaoESalva() {
        Abrigo abrigo = abrigoComDemanda("a1");
        when(repository.findById("a1")).thenReturn(Optional.of(abrigo));

        DemandaResponse resposta = service.atualizarDemanda("a1", 0, demandaAtualizar(8, 3));

        assertEquals(0, resposta.posicao());
        assertEquals(TipoPeca.COBERTOR, resposta.tipoPeca());
        assertEquals(5, resposta.deficit());
        assertEquals(TipoPeca.COBERTOR, abrigo.getDemandas().get(0).getTipoPeca());
        verify(repository).save(abrigo);
    }

    @Test
    void atualizarDemanda_posicaoInexistente_lancaDemandaNaoEncontradaENaoSalva() {
        when(repository.findById("a1")).thenReturn(Optional.of(abrigoComDemanda("a1")));

        assertThrows(DemandaNaoEncontradaException.class, () -> service.atualizarDemanda("a1", 5, demandaAtualizar(8, 3)));
        verify(repository, never()).save(any());
    }

    @Test
    void atualizarDemanda_atendidaAcimaDaNecessaria_lancaDadosInvalidosENaoSalva() {
        when(repository.findById("a1")).thenReturn(Optional.of(abrigoComDemanda("a1")));

        assertThrows(DadosInvalidosException.class, () -> service.atualizarDemanda("a1", 0, demandaAtualizar(2, 3)));
        verify(repository, never()).save(any());
    }

    @Test
    void atualizarDemanda_abrigoInexistente_lancaAbrigoNaoEncontrado() {
        when(repository.findById("x")).thenReturn(Optional.empty());

        assertThrows(AbrigoNaoEncontradoException.class, () -> service.atualizarDemanda("x", 0, demandaAtualizar(8, 3)));
    }

    @Test
    void removerDemanda_posicaoExistente_removeESalva() {
        Abrigo abrigo = abrigoComDemanda("a1");
        when(repository.findById("a1")).thenReturn(Optional.of(abrigo));

        service.removerDemanda("a1", 0);

        assertTrue(abrigo.getDemandas().isEmpty());
        verify(repository).save(abrigo);
    }

    @Test
    void removerDemanda_posicaoInexistente_lancaDemandaNaoEncontradaENaoSalva() {
        when(repository.findById("a1")).thenReturn(Optional.of(abrigo("a1")));

        assertThrows(DemandaNaoEncontradaException.class, () -> service.removerDemanda("a1", 0));
        verify(repository, never()).save(any());
    }

    @Test
    void removerDemanda_abrigoInexistente_lancaAbrigoNaoEncontrado() {
        when(repository.findById("x")).thenReturn(Optional.empty());

        assertThrows(AbrigoNaoEncontradoException.class, () -> service.removerDemanda("x", 0));
    }
}
