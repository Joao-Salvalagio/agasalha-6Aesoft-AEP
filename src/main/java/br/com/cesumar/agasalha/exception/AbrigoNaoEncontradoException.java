
package br.com.cesumar.agasalha.exception;

public class AbrigoNaoEncontradoException extends RuntimeException {

    public AbrigoNaoEncontradoException(String id) {
        super("Abrigo nao encontrado: " + id);
    }
}
