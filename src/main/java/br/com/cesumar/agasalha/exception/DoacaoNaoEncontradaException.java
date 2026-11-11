
package br.com.cesumar.agasalha.exception;

public class DoacaoNaoEncontradaException extends RuntimeException {

    public DoacaoNaoEncontradaException(String id) {
        super("Doacao nao encontrada: " + id);
    }
}
