package br.com.cesumar.agasalha.service;

import br.com.cesumar.agasalha.controller.dto.DoacaoCreateRequest;
import br.com.cesumar.agasalha.controller.dto.DoacaoResponse;
import br.com.cesumar.agasalha.controller.dto.ItemDoacaoRequest;
import br.com.cesumar.agasalha.controller.dto.ItemResponse;
import br.com.cesumar.agasalha.exception.AbrigoNaoEncontradoException;
import br.com.cesumar.agasalha.exception.DoacaoNaoEncontradaException;
import br.com.cesumar.agasalha.exception.DoadorNaoEncontradoException;
import br.com.cesumar.agasalha.mapper.DoacaoMapper;
import br.com.cesumar.agasalha.mapper.ItemMapper;
import br.com.cesumar.agasalha.model.Doacao;
import br.com.cesumar.agasalha.model.EstadoConservacao;
import br.com.cesumar.agasalha.model.Genero;
import br.com.cesumar.agasalha.model.ItemAgasalho;
import br.com.cesumar.agasalha.model.StatusItem;
import br.com.cesumar.agasalha.model.Tamanho;
import br.com.cesumar.agasalha.model.TipoPeca;
import br.com.cesumar.agasalha.repository.AbrigoRepository;
import br.com.cesumar.agasalha.repository.DoacaoRepository;
import br.com.cesumar.agasalha.repository.DoadorRepository;
import br.com.cesumar.agasalha.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DoacaoServiceTest {

    private DoacaoRepository repository;
    private DoadorRepository doadorRepository;
    private AbrigoRepository abrigoRepository;
    private ItemRepository itemRepository;
    private DoacaoService service;

    @BeforeEach
    void setUp() {
        repository = mock(DoacaoRepository.class);
        doadorRepository = mock(DoadorRepository.class);
        abrigoRepository = mock(AbrigoRepository.class);
        itemRepository = mock(ItemRepository.class);
        service = new DoacaoService(repository, doadorRepository, abrigoRepository, itemRepository,
                new DoacaoMapper(), new ItemMapper());
    }

    private Doacao doacao(String id) {
        Doacao doacao = new Doacao("d1", "a1");
        doacao.setId(id);
        return doacao;
    }

    private ItemDoacaoRequest itemRequest() {
        return new ItemDoacaoRequest(TipoPeca.CASACO, Tamanho.M, Genero.UNISSEX, EstadoConservacao.USADO_BOM);
    }

    @Test
    void criar_doadorEAbrigoExistentes_salvaComReferencias() {
        when(doadorRepository.existsById("d1")).thenReturn(true);
        when(abrigoRepository.existsById("a1")).thenReturn(true);
        when(repository.save(any(Doacao.class))).thenAnswer(i -> {
            Doacao doacao = i.getArgument(0);
            doacao.setId("novo");
            return doacao;
        });

        DoacaoResponse resposta = service.criar(new DoacaoCreateRequest("d1", "a1"));

        assertEquals("novo", resposta.id());
        assertEquals("d1", resposta.doadorId());
        assertEquals("a1", resposta.abrigoId());
    }

    @Test
    void criar_doadorInexistente_lancaDoadorNaoEncontradoENaoSalva() {
        when(doadorRepository.existsById("d1")).thenReturn(false);

        assertThrows(DoadorNaoEncontradoException.class, () -> service.criar(new DoacaoCreateRequest("d1", "a1")));
        verify(repository, never()).save(any());
    }

    @Test
    void criar_abrigoInexistente_lancaAbrigoNaoEncontradoENaoSalva() {
        when(doadorRepository.existsById("d1")).thenReturn(true);
        when(abrigoRepository.existsById("a1")).thenReturn(false);

        assertThrows(AbrigoNaoEncontradoException.class, () -> service.criar(new DoacaoCreateRequest("d1", "a1")));
        verify(repository, never()).save(any());
    }

    @Test
    void buscarPorId_existente_devolveResponse() {
        when(repository.findById("o1")).thenReturn(Optional.of(doacao("o1")));

        assertEquals("o1", service.buscarPorId("o1").id());
    }

    @Test
    void buscarPorId_inexistente_lancaDoacaoNaoEncontrada() {
        when(repository.findById("x")).thenReturn(Optional.empty());

        assertThrows(DoacaoNaoEncontradaException.class, () -> service.buscarPorId("x"));
    }

    @Test
    void criarItem_doacaoExistente_salvaItemVinculadoSemDadosDoDoador() {
        when(repository.findById("o1")).thenReturn(Optional.of(doacao("o1")));
        when(itemRepository.save(any(ItemAgasalho.class))).thenAnswer(i -> {
            ItemAgasalho item = i.getArgument(0);
            item.setId("item1");
            return item;
        });

        ItemResponse resposta = service.criarItem("o1", itemRequest());

        assertEquals("item1", resposta.id());
        assertEquals("o1", resposta.doacaoId());
        assertEquals(StatusItem.DISPONIVEL, resposta.status());
        assertNull(resposta.nomeDoador());
        assertNull(resposta.contatoDoador());
    }

    @Test
    void criarItem_doacaoInexistente_lancaDoacaoNaoEncontradaENaoSalva() {
        when(repository.findById("x")).thenReturn(Optional.empty());

        assertThrows(DoacaoNaoEncontradaException.class, () -> service.criarItem("x", itemRequest()));
        verify(itemRepository, never()).save(any());
    }
}
