
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
import br.com.cesumar.agasalha.model.ItemAgasalho;
import br.com.cesumar.agasalha.repository.AbrigoRepository;
import br.com.cesumar.agasalha.repository.DoacaoRepository;
import br.com.cesumar.agasalha.repository.DoadorRepository;
import br.com.cesumar.agasalha.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DoacaoService {

    private final DoacaoRepository repository;
    private final DoadorRepository doadorRepository;
    private final AbrigoRepository abrigoRepository;
    private final ItemRepository itemRepository;
    private final DoacaoMapper mapper;
    private final ItemMapper itemMapper;

    public DoacaoResponse criar(DoacaoCreateRequest req) {
        Doacao nova = mapper.paraModel(req);
        nova.validar();
        if (!doadorRepository.existsById(nova.getDoadorId())) {
            throw new DoadorNaoEncontradoException(nova.getDoadorId());
        }
        if (nova.getAbrigoId() != null && !abrigoRepository.existsById(nova.getAbrigoId())) {
            throw new AbrigoNaoEncontradoException(nova.getAbrigoId());
        }
        return mapper.paraResponse(repository.save(nova));
    }

    public DoacaoResponse buscarPorId(String id) {
        return mapper.paraResponse(carregar(id));
    }

    public ItemResponse criarItem(String doacaoId, ItemDoacaoRequest req) {
        carregar(doacaoId);
        ItemAgasalho novo = itemMapper.paraModel(req, doacaoId);
        novo.validar();
        return itemMapper.paraResponse(itemRepository.save(novo));
    }

    private Doacao carregar(String id) {
        return repository.findById(id).orElseThrow(() -> new DoacaoNaoEncontradaException(id));
    }
}
