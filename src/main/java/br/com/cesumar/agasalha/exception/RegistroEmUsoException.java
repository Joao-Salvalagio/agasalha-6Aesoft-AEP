
package br.com.cesumar.agasalha.exception;

public class RegistroEmUsoException extends RuntimeException {

    public RegistroEmUsoException(String recurso, String id) {
        super(recurso + " possui vinculos e nao pode ser removido: " + id);
    }
}
