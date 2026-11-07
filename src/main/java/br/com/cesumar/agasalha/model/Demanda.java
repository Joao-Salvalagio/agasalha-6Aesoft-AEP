package br.com.cesumar.agasalha.model;

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
}
