
package br.com.cesumar.agasalha.mapper;

import br.com.cesumar.agasalha.controller.dto.DoadorCreateRequest;
import br.com.cesumar.agasalha.controller.dto.DoadorResponse;
import br.com.cesumar.agasalha.controller.dto.DoadorUpdateRequest;
import br.com.cesumar.agasalha.model.Doador;
import org.springframework.stereotype.Component;

@Component
public class DoadorMapper {

    public Doador paraModel(DoadorCreateRequest req) {
        return new Doador(req.nome(), req.contato());
    }

    public void aplicar(DoadorUpdateRequest req, Doador alvo) {
        alvo.setNome(req.nome());
        alvo.setContato(req.contato());
    }

    public DoadorResponse paraResponse(Doador doador) {
        return new DoadorResponse(doador.getId(), doador.getNome(), doador.getContato());
    }
}
