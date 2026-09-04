package br.com.cesumar.agasalha.service;

import br.com.cesumar.agasalha.controller.dto.ItemResponse;
import br.com.cesumar.agasalha.exception.ItemNaoEncontradoException;
import br.com.cesumar.agasalha.mapper.ItemMapper;
import br.com.cesumar.agasalha.model.AcaoStatus;
import br.com.cesumar.agasalha.model.ItemAgasalho;
import br.com.cesumar.agasalha.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ItemStatusService {

    private final ItemRepository repository;
    private final StatusTransitionService statusTransitionService;
    private final ItemMapper mapper;

    public ItemResponse reservar(String id) {
        return aplicar(id, AcaoStatus.RESERVAR);
    }

    public ItemResponse entregar(String id) {
        return aplicar(id, AcaoStatus.ENTREGAR);
    }

    private ItemResponse aplicar(String id, AcaoStatus acao) {
        ItemAgasalho item = repository.findById(id)
                .orElseThrow(() -> new ItemNaoEncontradoException(id));
        item.setStatus(statusTransitionService.aplicar(item.getStatus(), acao));
        return mapper.paraResponse(repository.save(item));
    }
}
