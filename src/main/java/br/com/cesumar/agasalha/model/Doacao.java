
package br.com.cesumar.agasalha.model;

import br.com.cesumar.agasalha.exception.DadosInvalidosException;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document("doacoes")
@Getter
@Setter
public class Doacao {

    @Id
    private String id;
    private String doadorId;
    private String abrigoId;
    private Instant dataDoacao;

    public Doacao() {
    }

    public Doacao(String doadorId, String abrigoId) {
        this.doadorId = doadorId;
        this.abrigoId = abrigoId;
        this.dataDoacao = Instant.now();
    }

    public void validar() {
        exigir(doadorId != null && !doadorId.isBlank(), "doadorId e obrigatorio");
        exigir(abrigoId == null || !abrigoId.isBlank(), "abrigoId nao pode ser vazio");
        exigir(dataDoacao != null, "dataDoacao e obrigatorio");
    }

    private static void exigir(boolean condicao, String mensagem) {
        if (!condicao) {
            throw new DadosInvalidosException(mensagem);
        }
    }
}
