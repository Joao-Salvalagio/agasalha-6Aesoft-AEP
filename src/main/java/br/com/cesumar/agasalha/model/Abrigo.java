package br.com.cesumar.agasalha.model;

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
    private int capacidadeEstoque;
    private int estoqueAtual;
    private List<Demanda> demandas = new ArrayList<>();

    public Abrigo() {
    }

    public Abrigo(String id, String nome, int capacidadeEstoque, int estoqueAtual,
                  List<Demanda> demandas) {
        this.id = id;
        this.nome = nome;
        this.capacidadeEstoque = capacidadeEstoque;
        this.estoqueAtual = estoqueAtual;
        this.demandas = demandas;
    }

    public boolean temEspaco() {
        return estoqueAtual + 1 <= capacidadeEstoque;
    }
}
