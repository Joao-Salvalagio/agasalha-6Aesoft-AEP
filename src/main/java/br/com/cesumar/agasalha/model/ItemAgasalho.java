package br.com.cesumar.agasalha.model;

import br.com.cesumar.agasalha.exception.DadosInvalidosException;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document("itens")
@Getter
@Setter
public class ItemAgasalho {

    @Id
    private String id;
    private TipoPeca tipoPeca;
    private Tamanho tamanho;
    private Genero genero;
    private EstadoConservacao estadoConservacao;
    private String nomeDoador;
    private String contatoDoador;
    private StatusItem status;
    private Instant dataCadastro;

    public ItemAgasalho() {
    }

    public ItemAgasalho(TipoPeca tipoPeca, Tamanho tamanho, Genero genero,
                        EstadoConservacao estadoConservacao, String nomeDoador,
                        String contatoDoador) {
        this.tipoPeca = tipoPeca;
        this.tamanho = tamanho;
        this.genero = genero;
        this.estadoConservacao = estadoConservacao;
        this.nomeDoador = nomeDoador;
        this.contatoDoador = contatoDoador;
        this.status = StatusItem.DISPONIVEL;
        this.dataCadastro = Instant.now();
    }

    public void validar() {
        exigir(tipoPeca != null, "tipoPeca e obrigatorio");
        exigir(tamanho != null, "tamanho e obrigatorio");
        exigir(genero != null, "genero e obrigatorio");
        exigir(estadoConservacao != null, "estadoConservacao e obrigatorio");
        exigir(nomeDoador != null && !nomeDoador.isBlank(), "nomeDoador e obrigatorio");
        exigir(contatoDoador != null && !contatoDoador.isBlank(), "contatoDoador e obrigatorio");
        exigir(status != null, "status e obrigatorio");
        exigir(dataCadastro != null, "dataCadastro e obrigatorio");
    }

    private static void exigir(boolean condicao, String mensagem) {
        if (!condicao) {
            throw new DadosInvalidosException(mensagem);
        }
    }
}
