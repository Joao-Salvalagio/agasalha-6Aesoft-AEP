package br.com.cesumar.agasalha.mapper;

import br.com.cesumar.agasalha.controller.dto.ItemCreateRequest;
import br.com.cesumar.agasalha.controller.dto.ItemResponse;
import br.com.cesumar.agasalha.controller.dto.ItemSummaryResponse;
import br.com.cesumar.agasalha.controller.dto.ItemUpdateRequest;
import br.com.cesumar.agasalha.model.ItemAgasalho;
import org.springframework.stereotype.Component;

@Component
public class ItemMapper {

    public ItemAgasalho paraModel(ItemCreateRequest req) {
        return new ItemAgasalho(req.tipoPeca(), req.tamanho(), req.genero(),
                req.estadoConservacao(), req.nomeDoador(), req.contatoDoador());
    }

    public void aplicar(ItemUpdateRequest req, ItemAgasalho alvo) {
        alvo.setTipoPeca(req.tipoPeca());
        alvo.setTamanho(req.tamanho());
        alvo.setGenero(req.genero());
        alvo.setEstadoConservacao(req.estadoConservacao());
        alvo.setNomeDoador(req.nomeDoador());
        alvo.setContatoDoador(req.contatoDoador());
    }

    public ItemResponse paraResponse(ItemAgasalho item) {
        return new ItemResponse(item.getId(), item.getTipoPeca(), item.getTamanho(),
                item.getGenero(), item.getEstadoConservacao(), item.getNomeDoador(),
                item.getContatoDoador(), item.getStatus(), item.getDataCadastro());
    }

    public ItemSummaryResponse paraSummary(ItemAgasalho item) {
        return new ItemSummaryResponse(item.getId(), item.getTipoPeca(), item.getTamanho(),
                item.getGenero(), item.getStatus());
    }
}
