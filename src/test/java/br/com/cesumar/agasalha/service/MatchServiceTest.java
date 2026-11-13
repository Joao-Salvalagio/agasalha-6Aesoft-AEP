package br.com.cesumar.agasalha.service;

import br.com.cesumar.agasalha.controller.dto.ItemResponse;
import br.com.cesumar.agasalha.controller.dto.MatchResponse;
import br.com.cesumar.agasalha.exception.ItemJaNoAbrigoException;
import br.com.cesumar.agasalha.exception.ItemNaoEncontradoException;
import br.com.cesumar.agasalha.exception.MatchNaoEncontradoException;
import br.com.cesumar.agasalha.mapper.ItemMapper;
import br.com.cesumar.agasalha.mapper.MatchMapper;
import br.com.cesumar.agasalha.model.Abrigo;
import br.com.cesumar.agasalha.model.Demanda;
import br.com.cesumar.agasalha.model.Endereco;
import br.com.cesumar.agasalha.model.EstadoConservacao;
import br.com.cesumar.agasalha.model.Genero;
import br.com.cesumar.agasalha.model.ItemAgasalho;
import br.com.cesumar.agasalha.model.StatusItem;
import br.com.cesumar.agasalha.model.Tamanho;
import br.com.cesumar.agasalha.model.TipoPeca;
import br.com.cesumar.agasalha.repository.AbrigoRepository;
import br.com.cesumar.agasalha.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MatchServiceTest {

    private ItemRepository itemRepository;
    private AbrigoRepository abrigoRepository;
    private MatchService service;

    @BeforeEach
    void setUp() {
        itemRepository = mock(ItemRepository.class);
        abrigoRepository = mock(AbrigoRepository.class);
        service = new MatchService(itemRepository, abrigoRepository, new MatchingService(), new MatchMapper(),
                new ItemMapper(), new StatusTransitionService());
    }

    private ItemAgasalho item() {
        return new ItemAgasalho(TipoPeca.CASACO, Tamanho.M, Genero.UNISSEX,
                EstadoConservacao.USADO_BOM, "Ana", "ana@exemplo.com");
    }

    private Abrigo abrigo(String id, Demanda... demandas) {
        Abrigo abrigo = new Abrigo("Abrigo " + id, new Endereco("Rua A", "1", "Centro", "Maringa", "PR"), 10, 0);
        abrigo.setId(id);
        abrigo.getDemandas().addAll(List.of(demandas));
        return abrigo;
    }

    @Test
    void sugerir_itemComAbrigoCompativel_devolveResponseDoMelhorAbrigo() {
        when(itemRepository.findById("i1")).thenReturn(Optional.of(item()));
        when(abrigoRepository.findAll()).thenReturn(List.of(
                abrigo("a1", new Demanda(TipoPeca.CASACO, Tamanho.M, Genero.UNISSEX, 4, 3)),
                abrigo("a2", new Demanda(TipoPeca.CASACO, Tamanho.M, Genero.UNISSEX, 9, 2))));

        MatchResponse resposta = service.sugerir("i1");

        assertEquals("i1", resposta.itemId());
        assertEquals("a2", resposta.abrigoId());
        assertEquals("Abrigo a2", resposta.abrigoNome());
        assertEquals(7, resposta.deficit());
    }

    @Test
    void sugerir_itemInexistente_lancaItemNaoEncontradoSemConsultarAbrigos() {
        when(itemRepository.findById("i1")).thenReturn(Optional.empty());

        assertThrows(ItemNaoEncontradoException.class, () -> service.sugerir("i1"));

        verify(abrigoRepository, never()).findAll();
    }

    @Test
    void sugerir_nenhumAbrigoCompativel_lancaMatchNaoEncontrado() {
        when(itemRepository.findById("i1")).thenReturn(Optional.of(item()));
        when(abrigoRepository.findAll()).thenReturn(List.of(
                abrigo("a1", new Demanda(TipoPeca.CASACO, Tamanho.G, Genero.UNISSEX, 5, 0))));

        assertThrows(MatchNaoEncontradoException.class, () -> service.sugerir("i1"));
    }

    @Test
    void sugerir_semAbrigosCadastrados_lancaMatchNaoEncontrado() {
        when(itemRepository.findById("i1")).thenReturn(Optional.of(item()));
        when(abrigoRepository.findAll()).thenReturn(List.of());

        assertThrows(MatchNaoEncontradoException.class, () -> service.sugerir("i1"));
    }

    private ItemAgasalho itemComId(StatusItem status) {
        ItemAgasalho item = item();
        item.setId("i1");
        item.setStatus(status);
        return item;
    }

    private void prepararConfirmacao(ItemAgasalho item, Abrigo abrigo) {
        when(itemRepository.findById("i1")).thenReturn(Optional.of(item));
        when(abrigoRepository.findAll()).thenReturn(List.of(abrigo));
        when(abrigoRepository.save(any(Abrigo.class))).thenAnswer(i -> i.getArgument(0));
        when(itemRepository.save(any(ItemAgasalho.class))).thenAnswer(i -> i.getArgument(0));
    }

    @Test
    void confirmar_itemDisponivel_alocaNoAbrigoAtualizaNumerosEReserva() {
        Demanda demanda = new Demanda(TipoPeca.CASACO, Tamanho.M, Genero.UNISSEX, 5, 1);
        Abrigo abrigo = abrigo("a1", demanda);
        prepararConfirmacao(itemComId(StatusItem.DISPONIVEL), abrigo);

        ItemResponse resposta = service.confirmar("i1");

        assertEquals("a1", resposta.abrigoId());
        assertEquals(StatusItem.RESERVADO, resposta.status());
        assertEquals(1, abrigo.getEstoqueAtual());
        assertEquals(2, demanda.getQuantidadeAtendida());
        verify(abrigoRepository).save(abrigo);
    }

    @Test
    void confirmar_itemReservado_alocaEMantemReservado() {
        Abrigo abrigo = abrigo("a1", new Demanda(TipoPeca.CASACO, Tamanho.M, Genero.UNISSEX, 5, 0));
        prepararConfirmacao(itemComId(StatusItem.RESERVADO), abrigo);

        ItemResponse resposta = service.confirmar("i1");

        assertEquals("a1", resposta.abrigoId());
        assertEquals(StatusItem.RESERVADO, resposta.status());
    }

    @Test
    void confirmar_itemInexistente_lancaItemNaoEncontradoSemConsultarAbrigos() {
        when(itemRepository.findById("i1")).thenReturn(Optional.empty());

        assertThrows(ItemNaoEncontradoException.class, () -> service.confirmar("i1"));

        verify(abrigoRepository, never()).findAll();
    }

    @Test
    void confirmar_itemJaNoAbrigo_lancaConflitoSemGravarNada() {
        ItemAgasalho item = itemComId(StatusItem.RESERVADO);
        item.setAbrigoId("a9");
        when(itemRepository.findById("i1")).thenReturn(Optional.of(item));

        assertThrows(ItemJaNoAbrigoException.class, () -> service.confirmar("i1"));

        verify(abrigoRepository, never()).save(any());
        verify(itemRepository, never()).save(any());
    }

    @Test
    void confirmar_semAbrigoCompativel_lancaMatchNaoEncontradoSemGravarNada() {
        when(itemRepository.findById("i1")).thenReturn(Optional.of(itemComId(StatusItem.DISPONIVEL)));
        when(abrigoRepository.findAll()).thenReturn(List.of(
                abrigo("a1", new Demanda(TipoPeca.CASACO, Tamanho.G, Genero.UNISSEX, 5, 0))));

        assertThrows(MatchNaoEncontradoException.class, () -> service.confirmar("i1"));

        verify(abrigoRepository, never()).save(any());
        verify(itemRepository, never()).save(any());
    }

    @Test
    void confirmar_itemEntregueSemAbrigo_alocaEMantemEntregue() {
        Abrigo abrigo = abrigo("a1", new Demanda(TipoPeca.CASACO, Tamanho.M, Genero.UNISSEX, 5, 0));
        prepararConfirmacao(itemComId(StatusItem.ENTREGUE), abrigo);

        ItemResponse resposta = service.confirmar("i1");

        assertEquals("a1", resposta.abrigoId());
        assertEquals(StatusItem.ENTREGUE, resposta.status());
    }
}
