package br.com.cesumar.agasalha.service;

import br.com.cesumar.agasalha.controller.dto.ItemCreateRequest;
import br.com.cesumar.agasalha.controller.dto.ItemResponse;
import br.com.cesumar.agasalha.controller.dto.ItemUpdateRequest;
import br.com.cesumar.agasalha.exception.DadosInvalidosException;
import br.com.cesumar.agasalha.exception.ItemNaoEncontradoException;
import br.com.cesumar.agasalha.mapper.ItemMapper;
import br.com.cesumar.agasalha.model.EstadoConservacao;
import br.com.cesumar.agasalha.model.Genero;
import br.com.cesumar.agasalha.model.ItemAgasalho;
import br.com.cesumar.agasalha.model.StatusItem;
import br.com.cesumar.agasalha.model.Tamanho;
import br.com.cesumar.agasalha.model.TipoPeca;
import br.com.cesumar.agasalha.repository.FiltroItem;
import br.com.cesumar.agasalha.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class ItemServiceTest {

    private ItemRepository repository;
    private MongoTemplate mongoTemplate;
    private ItemService service;

    @BeforeEach
    void setUp() {
        repository = mock(ItemRepository.class);
        mongoTemplate = mock(MongoTemplate.class);
        service = new ItemService(repository, mongoTemplate, new ItemMapper());
    }

    private ItemCreateRequest criarRequest() {
        return new ItemCreateRequest(TipoPeca.CASACO, Tamanho.M, Genero.UNISSEX,
                EstadoConservacao.USADO_BOM, "Ana", "ana@exemplo.com");
    }

    private ItemUpdateRequest atualizarRequest() {
        return new ItemUpdateRequest(TipoPeca.COBERTOR, Tamanho.G, Genero.MASCULINO,
                EstadoConservacao.NOVO, "Bia", "bia@exemplo.com");
    }

    private ItemAgasalho item(String id) {
        ItemAgasalho it = new ItemAgasalho(TipoPeca.CASACO, Tamanho.M, Genero.UNISSEX,
                EstadoConservacao.USADO_BOM, "Ana", "ana@exemplo.com");
        it.setId(id);
        return it;
    }

    @Test
    void criar_valido_salvaEDevolveResponseDisponivel() {
        when(repository.save(any(ItemAgasalho.class))).thenAnswer(i -> {
            ItemAgasalho it = i.getArgument(0);
            it.setId("novo");
            return it;
        });

        ItemResponse r = service.criar(criarRequest());

        assertEquals("novo", r.id());
        assertEquals(StatusItem.DISPONIVEL, r.status());
        verify(repository).save(any(ItemAgasalho.class));
    }

    @Test
    void criar_contatoEmBranco_lancaENaoSalva() {
        ItemCreateRequest invalido = new ItemCreateRequest(TipoPeca.CASACO, Tamanho.M,
                Genero.UNISSEX, EstadoConservacao.USADO_BOM, "Ana", "   ");

        assertThrows(DadosInvalidosException.class, () -> service.criar(invalido));
        verify(repository, never()).save(any());
    }

    @Test
    void buscarPorId_existente_devolveResponse() {
        when(repository.findById("id1")).thenReturn(Optional.of(item("id1")));
        assertEquals("id1", service.buscarPorId("id1").id());
    }

    @Test
    void buscarPorId_inexistente_lanca404() {
        when(repository.findById("x")).thenReturn(Optional.empty());
        assertThrows(ItemNaoEncontradoException.class, () -> service.buscarPorId("x"));
    }

    @Test
    void listar_semCriterio_usaFindAll() {
        when(repository.findAll()).thenReturn(List.of(item("a"), item("b")));

        assertEquals(2, service.listar(FiltroItem.vazio()).size());
        verify(repository).findAll();
        verifyNoInteractions(mongoTemplate);
    }

    @Test
    void listar_filtroNulo_usaFindAll() {
        when(repository.findAll()).thenReturn(List.of());
        service.listar(null);
        verify(repository).findAll();
    }

    @Test
    void listar_comCriterio_consultaMongoTemplateComQueryCorreta() {
        when(mongoTemplate.find(any(Query.class), eq(ItemAgasalho.class)))
                .thenReturn(List.of(item("b")));

        List<?> resultado = service.listar(new FiltroItem(Tamanho.M, TipoPeca.CASACO, null, null));

        assertEquals(1, resultado.size());
        ArgumentCaptor<Query> captor = ArgumentCaptor.forClass(Query.class);
        verify(mongoTemplate).find(captor.capture(), eq(ItemAgasalho.class));
        org.bson.Document criterios = captor.getValue().getQueryObject();
        assertTrue(criterios.containsKey("tamanho"));
        assertTrue(criterios.containsKey("tipoPeca"));
        assertFalse(criterios.containsKey("genero"));
        assertFalse(criterios.containsKey("status"));
        assertEquals(Tamanho.M, criterios.get("tamanho"));
        verify(repository, never()).findAll();
    }

    @Test
    void atualizar_existente_alteraDescritivosSemMexerNoStatus() {
        ItemAgasalho existente = item("id1");
        existente.setStatus(StatusItem.RESERVADO);
        when(repository.findById("id1")).thenReturn(Optional.of(existente));
        when(repository.save(any(ItemAgasalho.class))).thenAnswer(i -> i.getArgument(0));

        ItemResponse r = service.atualizar("id1", atualizarRequest());

        assertEquals(TipoPeca.COBERTOR, r.tipoPeca());
        assertEquals("Bia", r.nomeDoador());
        assertEquals(StatusItem.RESERVADO, r.status());
    }

    @Test
    void atualizar_inexistente_lanca404() {
        when(repository.findById("x")).thenReturn(Optional.empty());
        assertThrows(ItemNaoEncontradoException.class,
                () -> service.atualizar("x", atualizarRequest()));
        verify(repository, never()).save(any());
    }

    @Test
    void remover_existente_deleta() {
        when(repository.existsById("id1")).thenReturn(true);
        service.remover("id1");
        verify(repository).deleteById("id1");
    }

    @Test
    void remover_inexistente_lanca404ENaoDeleta() {
        when(repository.existsById("x")).thenReturn(false);
        assertThrows(ItemNaoEncontradoException.class, () -> service.remover("x"));
        verify(repository, never()).deleteById(anyString());
    }
}
