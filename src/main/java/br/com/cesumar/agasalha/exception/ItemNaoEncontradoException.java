package br.com.cesumar.agasalha.exception;

public class ItemNaoEncontradoException extends RuntimeException {

    public ItemNaoEncontradoException(String id) {
        super("Item nao encontrado: " + id);
    }
}
