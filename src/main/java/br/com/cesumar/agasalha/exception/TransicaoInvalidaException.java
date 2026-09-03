package br.com.cesumar.agasalha.exception;

import br.com.cesumar.agasalha.model.AcaoStatus;
import br.com.cesumar.agasalha.model.StatusItem;

public class TransicaoInvalidaException extends RuntimeException {

    public TransicaoInvalidaException(StatusItem atual, AcaoStatus acao) {
        super("Nao e possivel " + acao + " item com status " + atual);
    }
}
