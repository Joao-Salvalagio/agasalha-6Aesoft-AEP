
package br.com.cesumar.agasalha.mapper;

import br.com.cesumar.agasalha.controller.dto.DoacaoCreateRequest;
import br.com.cesumar.agasalha.controller.dto.DoacaoResponse;
import br.com.cesumar.agasalha.model.Doacao;
import org.springframework.stereotype.Component;

@Component
public class DoacaoMapper {

    public Doacao paraModel(DoacaoCreateRequest req) {
        return new Doacao(req.doadorId(), req.abrigoId());
    }

    public DoacaoResponse paraResponse(Doacao doacao) {
        return new DoacaoResponse(doacao.getId(), doacao.getDoadorId(), doacao.getAbrigoId(),
                doacao.getDataDoacao());
    }
}
