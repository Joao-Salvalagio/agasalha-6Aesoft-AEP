
package br.com.cesumar.agasalha.mapper;

import br.com.cesumar.agasalha.controller.dto.ItemCreateRequest;
import br.com.cesumar.agasalha.controller.dto.ItemDoacaoRequest;
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

    public ItemAgasalho paraModel(ItemDoacaoRequest req, String doacaoId) {
        ItemAgasalho item = new ItemAgasalho(req.tipoPeca(), req.tamanho(), req.genero(),
                req.estadoConservacao(), null, null);
        item.setDoacaoId(doacaoId);
        return item;
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
                item.getContatoDoador(), item.getStatus(), item.getDataCadastro(),
                item.getDoacaoId(), item.getAbrigoId());
    }

    public ItemSummaryResponse paraSummary(ItemAgasalho item) {
        return new ItemSummaryResponse(item.getId(), item.getTipoPeca(), item.getTamanho(),
                item.getGenero(), item.getStatus());
    }
}
