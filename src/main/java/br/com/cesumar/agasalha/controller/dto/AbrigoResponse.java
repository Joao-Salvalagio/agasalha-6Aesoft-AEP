
package br.com.cesumar.agasalha.controller.dto;

import java.util.List;

public record AbrigoResponse(
        String id,
        String nome,
        EnderecoResponse endereco,
        int capacidadeEstoque,
        int estoqueAtual,
        List<DemandaResponse> demandas) {
}
