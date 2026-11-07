package br.com.cesumar.agasalha.exception;

public class DemandaNaoEncontradaException extends RuntimeException {

    public DemandaNaoEncontradaException(int posicao) {
        super("Demanda nao encontrada na posicao: " + posicao);
    }
}
