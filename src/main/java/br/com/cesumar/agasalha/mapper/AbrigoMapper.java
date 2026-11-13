
package br.com.cesumar.agasalha.mapper;

import br.com.cesumar.agasalha.controller.dto.AbrigoCreateRequest;
import br.com.cesumar.agasalha.controller.dto.AbrigoResponse;
import br.com.cesumar.agasalha.controller.dto.AbrigoUpdateRequest;
import br.com.cesumar.agasalha.controller.dto.DemandaCreateRequest;
import br.com.cesumar.agasalha.controller.dto.DemandaResponse;
import br.com.cesumar.agasalha.controller.dto.DemandaUpdateRequest;
import br.com.cesumar.agasalha.controller.dto.EnderecoRequest;
import br.com.cesumar.agasalha.controller.dto.EnderecoResponse;
import br.com.cesumar.agasalha.model.Abrigo;
import br.com.cesumar.agasalha.model.Demanda;
import br.com.cesumar.agasalha.model.Endereco;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Component
public class AbrigoMapper {

    public Abrigo paraModel(AbrigoCreateRequest req) {
        Abrigo abrigo = new Abrigo(req.nome(), paraEndereco(req.endereco()),
                req.capacidadeEstoque(), req.estoqueAtual());
        List<Demanda> demandas = new ArrayList<>();
        req.demandas().forEach(demanda -> demandas.add(paraDemanda(demanda)));
        abrigo.setDemandas(demandas);
        return abrigo;
    }

    public void aplicar(AbrigoUpdateRequest req, Abrigo alvo) {
        alvo.setNome(req.nome());
        alvo.setEndereco(paraEndereco(req.endereco()));
        alvo.setCapacidadeEstoque(req.capacidadeEstoque());
        alvo.setEstoqueAtual(req.estoqueAtual());
    }

    public Demanda paraDemanda(DemandaCreateRequest req) {
        return new Demanda(req.tipoPeca(), req.tamanho(), req.genero(),
                req.quantidadeNecessaria(), req.quantidadeAtendida());
    }

    public Demanda paraDemanda(DemandaUpdateRequest req) {
        return new Demanda(req.tipoPeca(), req.tamanho(), req.genero(),
                req.quantidadeNecessaria(), req.quantidadeAtendida());
    }

    public AbrigoResponse paraResponse(Abrigo abrigo) {
        List<Demanda> demandas = abrigo.getDemandas();
        List<DemandaResponse> respostas = IntStream.range(0, demandas.size())
                .mapToObj(posicao -> paraDemandaResponse(posicao, demandas.get(posicao)))
                .toList();
        return new AbrigoResponse(abrigo.getId(), abrigo.getNome(), paraEnderecoResponse(abrigo.getEndereco()),
                abrigo.getCapacidadeEstoque(), abrigo.getEstoqueAtual(), respostas);
    }

    public DemandaResponse paraDemandaResponse(int posicao, Demanda demanda) {
        return new DemandaResponse(posicao, demanda.getTipoPeca(), demanda.getTamanho(), demanda.getGenero(),
                demanda.getQuantidadeNecessaria(), demanda.getQuantidadeAtendida(), demanda.deficit());
    }

    private Endereco paraEndereco(EnderecoRequest req) {
        return new Endereco(req.logradouro(), req.numero(), req.bairro(), req.cidade(), req.estado());
    }

    private EnderecoResponse paraEnderecoResponse(Endereco endereco) {
        return new EnderecoResponse(endereco.logradouro(), endereco.numero(), endereco.bairro(),
                endereco.cidade(), endereco.estado());
    }
}
