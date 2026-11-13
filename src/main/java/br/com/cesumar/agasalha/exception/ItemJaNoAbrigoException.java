package br.com.cesumar.agasalha.exception;

public class ItemJaNoAbrigoException extends RuntimeException {

    public ItemJaNoAbrigoException(String itemId) {
        super("Item ja foi adicionado a um abrigo: " + itemId);
    }
}
