package br.com.cesumar.agasalha.model;

import br.com.cesumar.agasalha.exception.DadosInvalidosException;
import br.com.cesumar.agasalha.exception.DemandaNaoEncontradaException;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document("abrigos")
@Getter
@Setter
public class Abrigo {

    @Id
    private String id;
    private String nome;
    private Endereco endereco;
    private int capacidadeEstoque;
    private int estoqueAtual;
    private List<Demanda> demandas = new ArrayList<>();

    public Abrigo() {
    }

    public Abrigo(String nome, Endereco endereco, int capacidadeEstoque, int estoqueAtual) {
        this.nome = nome;
        this.endereco = endereco;
        this.capacidadeEstoque = capacidadeEstoque;
        this.estoqueAtual = estoqueAtual;
    }

    public boolean temEspaco() {
        return estoqueAtual + 1 <= capacidadeEstoque;
    }

    public void adicionarDemanda(Demanda demanda) {
        demanda.validar();
        demandas.add(demanda);
    }

    public void atualizarDemanda(int posicao, Demanda nova) {
        exigirPosicao(posicao);
        nova.validar();
        demandas.set(posicao, nova);
    }

    public void removerDemanda(int posicao) {
        exigirPosicao(posicao);
        demandas.remove(posicao);
    }

    public void validar() {
        exigir(nome != null && !nome.isBlank(), "nome e obrigatorio");
        exigir(endereco != null, "endereco e obrigatorio");
        exigir(capacidadeEstoque > 0, "capacidadeEstoque deve ser maior que zero");
        exigir(estoqueAtual >= 0, "estoqueAtual nao pode ser negativo");
        exigir(demandas != null, "demandas e obrigatorio");
        demandas.forEach(Demanda::validar);
    }

    private void exigirPosicao(int posicao) {
        if (posicao < 0 || posicao >= demandas.size()) {
            throw new DemandaNaoEncontradaException(posicao);
        }
    }

    private static void exigir(boolean condicao, String mensagem) {
        if (!condicao) {
            throw new DadosInvalidosException(mensagem);
        }
    }
}
