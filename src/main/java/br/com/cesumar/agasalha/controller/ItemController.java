package br.com.cesumar.agasalha.controller;

import br.com.cesumar.agasalha.controller.dto.ItemCreateRequest;
import br.com.cesumar.agasalha.controller.dto.ItemResponse;
import br.com.cesumar.agasalha.controller.dto.ItemSummaryResponse;
import br.com.cesumar.agasalha.controller.dto.ItemUpdateRequest;
import br.com.cesumar.agasalha.model.Genero;
import br.com.cesumar.agasalha.model.StatusItem;
import br.com.cesumar.agasalha.model.Tamanho;
import br.com.cesumar.agasalha.model.TipoPeca;
import br.com.cesumar.agasalha.repository.FiltroItem;
import br.com.cesumar.agasalha.service.ItemService;
import br.com.cesumar.agasalha.service.ItemStatusService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/itens")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;
    private final ItemStatusService itemStatusService;

    @PostMapping
    public ResponseEntity<ItemResponse> criar(@Valid @RequestBody ItemCreateRequest requisicao) {
        ItemResponse criado = itemService.criar(requisicao);
        return ResponseEntity.created(URI.create("/api/itens/" + criado.id())).body(criado);
    }

    @GetMapping
    public ResponseEntity<List<ItemSummaryResponse>> listar(
            @RequestParam(required = false) Tamanho tamanho,
            @RequestParam(required = false) TipoPeca tipoPeca,
            @RequestParam(required = false) Genero genero,
            @RequestParam(required = false) StatusItem status) {
        FiltroItem filtro = new FiltroItem(tamanho, tipoPeca, genero, status);
        return ResponseEntity.ok(itemService.listar(filtro));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemResponse> buscarPorId(@PathVariable String id) {
        return ResponseEntity.ok(itemService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ItemResponse> atualizar(@PathVariable String id,
                                                  @Valid @RequestBody ItemUpdateRequest requisicao) {
        return ResponseEntity.ok(itemService.atualizar(id, requisicao));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable String id) {
        itemService.remover(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/reserva")
    public ResponseEntity<ItemResponse> reservar(@PathVariable String id) {
        return ResponseEntity.ok(itemStatusService.reservar(id));
    }

    @PostMapping("/{id}/entrega")
    public ResponseEntity<ItemResponse> entregar(@PathVariable String id) {
        return ResponseEntity.ok(itemStatusService.entregar(id));
    }
}