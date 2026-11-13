package br.com.cesumar.agasalha.exception;

public class ItemSemAbrigoException extends RuntimeException {

    public ItemSemAbrigoException(String itemId) {
        super("Item precisa ser enviado a um abrigo antes de mudar de status: " + itemId);
    }
}
