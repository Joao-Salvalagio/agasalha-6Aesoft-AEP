
package br.com.cesumar.agasalha.service;

import br.com.cesumar.agasalha.controller.dto.MatchResponse;
import br.com.cesumar.agasalha.exception.ItemNaoEncontradoException;
import br.com.cesumar.agasalha.exception.MatchNaoEncontradoException;
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

    public MatchResponse sugerir(String itemId) {
        ItemAgasalho item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNaoEncontradoException(itemId));
        SugestaoMatch sugestao = matchingService.sugerir(item, abrigoRepository.findAll())
                .orElseThrow(() -> new MatchNaoEncontradoException(itemId));
        return mapper.paraResponse(itemId, sugestao);
    }
}
