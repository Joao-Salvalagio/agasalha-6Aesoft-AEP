
package br.com.cesumar.agasalha.model;

import br.com.cesumar.agasalha.exception.DadosInvalidosException;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("doadores")
@Getter
@Setter
public class Doador {

    @Id
    private String id;
    private String nome;
    private String contato;

    public Doador() {
    }

    public Doador(String nome, String contato) {
        this.nome = nome;
        this.contato = contato;
    }

    public void validar() {
        exigir(nome != null && !nome.isBlank(), "nome e obrigatorio");
        exigir(contato != null && !contato.isBlank(), "contato e obrigatorio");
    }

    private static void exigir(boolean condicao, String mensagem) {
        if (!condicao) {
            throw new DadosInvalidosException(mensagem);
        }
    }
}
