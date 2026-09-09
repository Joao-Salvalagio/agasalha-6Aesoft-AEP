package br.com.cesumar.agasalha.service;

import br.com.cesumar.agasalha.controller.dto.ItemCreateRequest;
import br.com.cesumar.agasalha.controller.dto.ItemResponse;
import br.com.cesumar.agasalha.controller.dto.ItemSummaryResponse;
import br.com.cesumar.agasalha.controller.dto.ItemUpdateRequest;
import br.com.cesumar.agasalha.exception.ItemNaoEncontradoException;
import br.com.cesumar.agasalha.mapper.ItemMapper;
import br.com.cesumar.agasalha.model.ItemAgasalho;
import br.com.cesumar.agasalha.repository.FiltroItem;
import br.com.cesumar.agasalha.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository repository;
    private final MongoTemplate mongoTemplate;
    private final ItemMapper mapper;

    public ItemResponse criar(ItemCreateRequest req) {
        ItemAgasalho novo = mapper.paraModel(req);
        novo.validar();
        return mapper.paraResponse(repository.save(novo));
    }

    public ItemResponse buscarPorId(String id) {
        return mapper.paraResponse(carregar(id));
    }

    public List<ItemSummaryResponse> listar(FiltroItem filtro) {
        List<ItemAgasalho> itens = (filtro == null || !filtro.temAlgumCriterio())
                ? repository.findAll()
                : mongoTemplate.find(montarQuery(filtro), ItemAgasalho.class);
        return itens.stream().map(mapper::paraSummary).toList();
    }

    public ItemResponse atualizar(String id, ItemUpdateRequest req) {
        ItemAgasalho existente = carregar(id);
        mapper.aplicar(req, existente);
        existente.validar();
        return mapper.paraResponse(repository.save(existente));
    }

    public void remover(String id) {
        if (!repository.existsById(id)) {
            throw new ItemNaoEncontradoException(id);
        }
        repository.deleteById(id);
    }

    private ItemAgasalho carregar(String id) {
        return repository.findById(id).orElseThrow(() -> new ItemNaoEncontradoException(id));
    }

    private static Query montarQuery(FiltroItem filtro) {
        Query query = new Query();
        if (filtro.tamanho() != null) {
            query.addCriteria(Criteria.where("tamanho").is(filtro.tamanho()));
        }
        if (filtro.tipoPeca() != null) {
            query.addCriteria(Criteria.where("tipoPeca").is(filtro.tipoPeca()));
        }
        if (filtro.genero() != null) {
            query.addCriteria(Criteria.where("genero").is(filtro.genero()));
        }
        if (filtro.status() != null) {
            query.addCriteria(Criteria.where("status").is(filtro.status()));
        }
        return query;
    }
}
