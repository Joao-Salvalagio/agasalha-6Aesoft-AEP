
package br.com.cesumar.agasalha.service;

import br.com.cesumar.agasalha.controller.dto.ItemResponse;
import br.com.cesumar.agasalha.controller.dto.MatchResponse;
import br.com.cesumar.agasalha.exception.ItemJaNoAbrigoException;
import br.com.cesumar.agasalha.exception.ItemNaoEncontradoException;
import br.com.cesumar.agasalha.exception.MatchNaoEncontradoException;
import br.com.cesumar.agasalha.mapper.ItemMapper;
import br.com.cesumar.agasalha.mapper.MatchMapper;
import br.com.cesumar.agasalha.model.ItemAgasalho;
import br.com.cesumar.agasalha.repository.AbrigoRepository;
import br.com.cesumar.agasalha.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MatchService {

    private final ItemRepository itemRepository;
    private final AbrigoRepository abrigoRepository;
    private final MatchingService matchingService;
    private final MatchMapper mapper;
    private final ItemMapper itemMapper;

    public MatchResponse sugerir(String itemId) {
        ItemAgasalho item = carregarItem(itemId);
        return mapper.paraResponse(itemId, buscarSugestao(item));
    }

    public ItemResponse confirmar(String itemId) {
        ItemAgasalho item = carregarItem(itemId);
        if (item.getAbrigoId() != null) {
            throw new ItemJaNoAbrigoException(itemId);
        }
        SugestaoMatch sugestao = buscarSugestao(item);

        sugestao.abrigo().receberPeca(sugestao.demanda());
        abrigoRepository.save(sugestao.abrigo());
        item.alocarEm(sugestao.abrigo().getId());
        return itemMapper.paraResponse(itemRepository.save(item));
    }

    private ItemAgasalho carregarItem(String itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNaoEncontradoException(itemId));
    }

    private SugestaoMatch buscarSugestao(ItemAgasalho item) {
        return matchingService.sugerir(item, abrigoRepository.findAll())
                .orElseThrow(() -> new MatchNaoEncontradoException(item.getId()));
    }
}
