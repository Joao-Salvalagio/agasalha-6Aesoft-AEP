package br.com.cesumar.agasalha.service;

import br.com.cesumar.agasalha.controller.dto.ItemResponse;
import br.com.cesumar.agasalha.exception.ItemNaoEncontradoException;
import br.com.cesumar.agasalha.exception.TransicaoInvalidaException;
import br.com.cesumar.agasalha.mapper.ItemMapper;
import br.com.cesumar.agasalha.model.EstadoConservacao;
import br.com.cesumar.agasalha.model.Genero;
import br.com.cesumar.agasalha.model.ItemAgasalho;
import br.com.cesumar.agasalha.model.StatusItem;
import br.com.cesumar.agasalha.model.Tamanho;
import br.com.cesumar.agasalha.model.TipoPeca;
import br.com.cesumar.agasalha.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ItemStatusServiceTest {

    private ItemRepository repository;
    private ItemStatusService service;

    @BeforeEach
    void setUp() {
        repository = mock(ItemRepository.class);
        service = new ItemStatusService(repository, new StatusTransitionService(), new ItemMapper());
    }

    private ItemAgasalho item(String id, StatusItem status) {
        ItemAgasalho item = new ItemAgasalho(TipoPeca.CASACO, Tamanho.M, Genero.UNISSEX,
                EstadoConservacao.USADO_BOM, "Ana", "ana@exemplo.com");
        item.setId(id);
        item.setStatus(status);
        return item;
    }

    @Test
    void reservar_itemDisponivel_salvaReservadoEDevolveResponse() {
        ItemAgasalho item = item("id1", StatusItem.DISPONIVEL);
        when(repository.findById("id1")).thenReturn(Optional.of(item));
        when(repository.save(any(ItemAgasalho.class))).thenAnswer(i -> i.getArgument(0));

        ItemResponse resultado = service.reservar("id1");

        assertEquals("id1", resultado.id());
        assertEquals(StatusItem.RESERVADO, resultado.status());
        verify(repository).save(item);
    }

    @Test
    void entregar_itemReservado_salvaEntregueEDevolveResponse() {
        ItemAgasalho item = item("id2", StatusItem.RESERVADO);
        when(repository.findById("id2")).thenReturn(Optional.of(item));
        when(repository.save(any(ItemAgasalho.class))).thenAnswer(i -> i.getArgument(0));

        ItemResponse resultado = service.entregar("id2");

        assertEquals("id2", resultado.id());
        assertEquals(StatusItem.ENTREGUE, resultado.status());
        verify(repository).save(item);
    }

    @Test
    void reservar_itemInexistente_lancaItemNaoEncontrado() {
        when(repository.findById("x")).thenReturn(Optional.empty());

        assertThrows(ItemNaoEncontradoException.class, () -> service.reservar("x"));
        verify(repository, never()).save(any());
    }

    @Test
    void entregar_itemInexistente_lancaItemNaoEncontrado() {
        when(repository.findById("x")).thenReturn(Optional.empty());

        assertThrows(ItemNaoEncontradoException.class, () -> service.entregar("x"));
        verify(repository, never()).save(any());
    }

    @Test
    void reservar_itemReservado_lancaTransicaoInvalidaENaoSalva() {
        ItemAgasalho item = item("id3", StatusItem.RESERVADO);
        when(repository.findById("id3")).thenReturn(Optional.of(item));

        assertThrows(TransicaoInvalidaException.class, () -> service.reservar("id3"));
        verify(repository, never()).save(any());
    }

    @Test
    void entregar_itemDisponivel_lancaTransicaoInvalidaENaoSalva() {
        ItemAgasalho item = item("id4", StatusItem.DISPONIVEL);
        when(repository.findById("id4")).thenReturn(Optional.of(item));

        assertThrows(TransicaoInvalidaException.class, () -> service.entregar("id4"));
        verify(repository, never()).save(any());
    }
}
