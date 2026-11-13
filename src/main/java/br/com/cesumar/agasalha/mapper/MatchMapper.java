
package br.com.cesumar.agasalha.mapper;

import br.com.cesumar.agasalha.controller.dto.MatchResponse;
import br.com.cesumar.agasalha.service.SugestaoMatch;
import org.springframework.stereotype.Component;

@Component
public class MatchMapper {

    public MatchResponse paraResponse(String itemId, SugestaoMatch sugestao) {
        return new MatchResponse(itemId, sugestao.abrigo().getId(), sugestao.abrigo().getNome(),
                sugestao.demanda().getTipoPeca(), sugestao.demanda().getTamanho(),
                sugestao.demanda().getGenero(), sugestao.demanda().deficit());
    }
}
