package br.com.cesumar.agasalha.service;

import br.com.cesumar.agasalha.exception.TransicaoInvalidaException;
import br.com.cesumar.agasalha.model.AcaoStatus;
import br.com.cesumar.agasalha.model.StatusItem;
import org.springframework.stereotype.Service;

@Service
public class StatusTransitionService {

    public StatusItem aplicar(StatusItem atual, AcaoStatus acao) {
        if (atual == StatusItem.DISPONIVEL && acao == AcaoStatus.RESERVAR) {
            return StatusItem.RESERVADO;
        }
        if (atual == StatusItem.RESERVADO && acao == AcaoStatus.ENTREGAR) {
            return StatusItem.ENTREGUE;
        }
        throw new TransicaoInvalidaException(atual, acao);
    }
}
