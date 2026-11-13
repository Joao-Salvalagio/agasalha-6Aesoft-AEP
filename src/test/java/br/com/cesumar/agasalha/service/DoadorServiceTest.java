package br.com.cesumar.agasalha.service;

import br.com.cesumar.agasalha.controller.dto.DoadorCreateRequest;
import br.com.cesumar.agasalha.controller.dto.DoadorResponse;
import br.com.cesumar.agasalha.controller.dto.DoadorUpdateRequest;
import br.com.cesumar.agasalha.exception.DadosInvalidosException;
import br.com.cesumar.agasalha.exception.DoadorNaoEncontradoException;
import br.com.cesumar.agasalha.exception.RegistroEmUsoException;
import br.com.cesumar.agasalha.mapper.DoadorMapper;
import br.com.cesumar.agasalha.model.Doador;
import br.com.cesumar.agasalha.repository.DoacaoRepository;
import br.com.cesumar.agasalha.repository.DoadorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DoadorServiceTest {

    private DoadorRepository repository;
    private DoacaoRepository doacaoRepository;
    private DoadorService service;

    @BeforeEach
    void setUp() {
        repository = mock(DoadorRepository.class);
        doacaoRepository = mock(DoacaoRepository.class);
        service = new DoadorService(repository, doacaoRepository, new DoadorMapper());
    }

    private Doador doador(String id) {
        Doador doador = new Doador("Ana", "ana@exemplo.com");
        doador.setId(id);
        return doador;
    }

    @Test
    void criar_valido_salvaEDevolveResponse() {
        when(repository.save(any(Doador.class))).thenAnswer(i -> {
            Doador doador = i.getArgument(0);
            doador.setId("novo");
            return doador;
        });

        DoadorResponse resposta = service.criar(new DoadorCreateRequest("Ana", "ana@exemplo.com"));

        assertEquals("novo", resposta.id());
        assertEquals("Ana", resposta.nome());
    }

    @Test
    void criar_nomeEmBranco_lancaDadosInvalidosENaoSalva() {
        assertThrows(DadosInvalidosException.class,
                () -> service.criar(new DoadorCreateRequest(" ", "ana@exemplo.com")));
        verify(repository, never()).save(any());
    }

    @Test
    void buscarPorId_existente_devolveResponse() {
        when(repository.findById("d1")).thenReturn(Optional.of(doador("d1")));

        assertEquals("d1", service.buscarPorId("d1").id());
    }

    @Test
    void buscarPorId_inexistente_lancaDoadorNaoEncontrado() {
        when(repository.findById("x")).thenReturn(Optional.empty());

        assertThrows(DoadorNaoEncontradoException.class, () -> service.buscarPorId("x"));
    }

    @Test
    void listar_devolveTodosOsDoadores() {
        when(repository.findAll()).thenReturn(List.of(doador("d1"), doador("d2")));

        assertEquals(2, service.listar().size());
    }

    @Test
    void atualizar_valido_alteraNomeEContato() {
        when(repository.findById("d1")).thenReturn(Optional.of(doador("d1")));
        when(repository.save(any(Doador.class))).thenAnswer(i -> i.getArgument(0));

        DoadorResponse resposta = service.atualizar("d1", new DoadorUpdateRequest("Bia", "bia@exemplo.com"));

        assertEquals("d1", resposta.id());
        assertEquals("Bia", resposta.nome());
        assertEquals("bia@exemplo.com", resposta.contato());
    }

    @Test
    void atualizar_inexistente_lancaDoadorNaoEncontrado() {
        when(repository.findById("x")).thenReturn(Optional.empty());

        assertThrows(DoadorNaoEncontradoException.class,
                () -> service.atualizar("x", new DoadorUpdateRequest("Bia", "bia@exemplo.com")));
    }

    @Test
    void atualizar_contatoEmBranco_lancaDadosInvalidosENaoSalva() {
        when(repository.findById("d1")).thenReturn(Optional.of(doador("d1")));

        assertThrows(DadosInvalidosException.class,
                () -> service.atualizar("d1", new DoadorUpdateRequest("Bia", " ")));
        verify(repository, never()).save(any());
    }

    @Test
    void remover_semDoacoes_removeDoRepositorio() {
        when(repository.existsById("d1")).thenReturn(true);

        service.remover("d1");

        verify(repository).deleteById("d1");
    }

    @Test
    void remover_inexistente_lancaDoadorNaoEncontrado() {
        when(repository.existsById("x")).thenReturn(false);

        assertThrows(DoadorNaoEncontradoException.class, () -> service.remover("x"));
        verify(repository, never()).deleteById(any());
    }

    @Test
    void remover_comDoacaoVinculada_lancaRegistroEmUso() {
        when(repository.existsById("d1")).thenReturn(true);
        when(doacaoRepository.existsByDoadorId("d1")).thenReturn(true);

        assertThrows(RegistroEmUsoException.class, () -> service.remover("d1"));
        verify(repository, never()).deleteById(any());
    }
}
