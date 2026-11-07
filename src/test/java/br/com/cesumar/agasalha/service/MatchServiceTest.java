package br.com.cesumar.agasalha.service;

import br.com.cesumar.agasalha.controller.dto.MatchResponse;
import br.com.cesumar.agasalha.exception.ItemNaoEncontradoException;
import br.com.cesumar.agasalha.exception.MatchNaoEncontradoException;
import br.com.cesumar.agasalha.mapper.MatchMapper;
import br.com.cesumar.agasalha.model.Abrigo;
import br.com.cesumar.agasalha.model.Demanda;
import br.com.cesumar.agasalha.model.EstadoConservacao;
import br.com.cesumar.agasalha.model.Genero;
import br.com.cesumar.agasalha.model.ItemAgasalho;
import br.com.cesumar.agasalha.model.Tamanho;
import br.com.cesumar.agasalha.model.TipoPeca;
import br.com.cesumar.agasalha.repository.AbrigoRepository;
import br.com.cesumar.agasalha.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
        service = new MatchService(itemRepository, abrigoRepository, new MatchingService(), new MatchMapper());
    }

    private ItemAgasalho item() {
        return new ItemAgasalho(TipoPeca.CASACO, Tamanho.M, Genero.UNISSEX,
                EstadoConservacao.USADO_BOM, "Ana", "ana@exemplo.com");
    }

    private Abrigo abrigo(String id, Demanda... demandas) {
        return new Abrigo(id, "Abrigo " + id, 10, 0, List.of(demandas));
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
}
