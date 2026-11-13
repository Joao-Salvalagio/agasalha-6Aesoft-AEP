
package br.com.cesumar.agasalha.service;

import br.com.cesumar.agasalha.controller.dto.AbrigoCreateRequest;
import br.com.cesumar.agasalha.controller.dto.AbrigoResponse;
import br.com.cesumar.agasalha.controller.dto.AbrigoUpdateRequest;
import br.com.cesumar.agasalha.controller.dto.DemandaCreateRequest;
import br.com.cesumar.agasalha.controller.dto.DemandaResponse;
import br.com.cesumar.agasalha.controller.dto.DemandaUpdateRequest;
import br.com.cesumar.agasalha.exception.AbrigoNaoEncontradoException;
import br.com.cesumar.agasalha.exception.RegistroEmUsoException;
import br.com.cesumar.agasalha.mapper.AbrigoMapper;
import br.com.cesumar.agasalha.model.Abrigo;
import br.com.cesumar.agasalha.repository.AbrigoRepository;
import br.com.cesumar.agasalha.repository.DoacaoRepository;
import br.com.cesumar.agasalha.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AbrigoService {

    private final AbrigoRepository repository;
    private final DoacaoRepository doacaoRepository;
    private final ItemRepository itemRepository;
    private final AbrigoMapper mapper;

    public AbrigoResponse criar(AbrigoCreateRequest req) {
        Abrigo novo = mapper.paraModel(req);
        novo.validar();
        return mapper.paraResponse(repository.save(novo));
    }

    public AbrigoResponse buscarPorId(String id) {
        return mapper.paraResponse(carregar(id));
    }

    public List<AbrigoResponse> listar() {
        return repository.findAll().stream().map(mapper::paraResponse).toList();
    }

    public AbrigoResponse atualizar(String id, AbrigoUpdateRequest req) {
        Abrigo existente = carregar(id);
        mapper.aplicar(req, existente);
        existente.validar();
        return mapper.paraResponse(repository.save(existente));
    }

    public void remover(String id) {
        if (!repository.existsById(id)) {
            throw new AbrigoNaoEncontradoException(id);
        }
        if (doacaoRepository.existsByAbrigoId(id) || itemRepository.existsByAbrigoId(id)) {
            throw new RegistroEmUsoException("Abrigo", id);
        }
        repository.deleteById(id);
    }

    public List<DemandaResponse> listarDemandas(String abrigoId) {
        return mapper.paraResponse(carregar(abrigoId)).demandas();
    }

    public DemandaResponse adicionarDemanda(String abrigoId, DemandaCreateRequest req) {
        Abrigo abrigo = carregar(abrigoId);
        abrigo.adicionarDemanda(mapper.paraDemanda(req));
        repository.save(abrigo);
        int posicao = abrigo.getDemandas().size() - 1;
        return mapper.paraDemandaResponse(posicao, abrigo.getDemandas().get(posicao));
    }

    public DemandaResponse atualizarDemanda(String abrigoId, int posicao, DemandaUpdateRequest req) {
        Abrigo abrigo = carregar(abrigoId);
        abrigo.atualizarDemanda(posicao, mapper.paraDemanda(req));
        repository.save(abrigo);
        return mapper.paraDemandaResponse(posicao, abrigo.getDemandas().get(posicao));
    }

    public void removerDemanda(String abrigoId, int posicao) {
        Abrigo abrigo = carregar(abrigoId);
        abrigo.removerDemanda(posicao);
        repository.save(abrigo);
    }

    private Abrigo carregar(String id) {
        return repository.findById(id).orElseThrow(() -> new AbrigoNaoEncontradoException(id));
    }
}
