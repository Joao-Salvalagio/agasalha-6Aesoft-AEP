package br.com.cesumar.agasalha.model;

import br.com.cesumar.agasalha.exception.DadosInvalidosException;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Demanda {

    private TipoPeca tipoPeca;
    private Tamanho tamanho;
    private Genero genero;
    private int quantidadeNecessaria;
    private int quantidadeAtendida;

    public Demanda() {
    }

    public Demanda(TipoPeca tipoPeca, Tamanho tamanho, Genero genero,
                   int quantidadeNecessaria, int quantidadeAtendida) {
        this.tipoPeca = tipoPeca;
        this.tamanho = tamanho;
        this.genero = genero;
        this.quantidadeNecessaria = quantidadeNecessaria;
        this.quantidadeAtendida = quantidadeAtendida;
    }

    public int deficit() {
        return quantidadeNecessaria - quantidadeAtendida;
    }

    public boolean emAberto() {
        return deficit() > 0;
    }

    public void validar() {
        exigir(tipoPeca != null, "tipoPeca da demanda e obrigatorio");
        exigir(tamanho != null, "tamanho da demanda e obrigatorio");
        exigir(genero != null, "genero da demanda e obrigatorio");
        exigir(quantidadeNecessaria > 0, "quantidadeNecessaria deve ser maior que zero");
        exigir(quantidadeAtendida >= 0, "quantidadeAtendida nao pode ser negativa");
        exigir(quantidadeAtendida <= quantidadeNecessaria,
                "quantidadeAtendida nao pode exceder quantidadeNecessaria");
    }

    private static void exigir(boolean condicao, String mensagem) {
        if (!condicao) {
            throw new DadosInvalidosException(mensagem);
        }
    }
}
