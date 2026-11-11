
package br.com.cesumar.agasalha.exception;

public class DoadorNaoEncontradoException extends RuntimeException {

    public DoadorNaoEncontradoException(String id) {
        super("Doador nao encontrado: " + id);
    }
}
