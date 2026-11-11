
package br.com.cesumar.agasalha.exception;

public class MatchNaoEncontradoException extends RuntimeException {

    public MatchNaoEncontradoException(String itemId) {
        super("Nenhum abrigo compativel para o item: " + itemId);
    }
}
