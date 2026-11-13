
package br.com.cesumar.agasalha.controller;

import br.com.cesumar.agasalha.controller.dto.AbrigoCreateRequest;
import br.com.cesumar.agasalha.controller.dto.AbrigoResponse;
import br.com.cesumar.agasalha.controller.dto.AbrigoUpdateRequest;
import br.com.cesumar.agasalha.controller.dto.DemandaCreateRequest;
import br.com.cesumar.agasalha.controller.dto.DemandaResponse;
import br.com.cesumar.agasalha.controller.dto.DemandaUpdateRequest;
import br.com.cesumar.agasalha.service.AbrigoService;
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
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/abrigos")
@RequiredArgsConstructor
public class AbrigoController {

    private final AbrigoService service;

    @PostMapping
    public ResponseEntity<AbrigoResponse> criar(@Valid @RequestBody AbrigoCreateRequest requisicao) {
        AbrigoResponse criado = service.criar(requisicao);
        return ResponseEntity.created(URI.create("/api/abrigos/" + criado.id())).body(criado);
    }

    @GetMapping
    public ResponseEntity<List<AbrigoResponse>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AbrigoResponse> buscarPorId(@PathVariable String id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AbrigoResponse> atualizar(@PathVariable String id,
                                                    @Valid @RequestBody AbrigoUpdateRequest requisicao) {
        return ResponseEntity.ok(service.atualizar(id, requisicao));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable String id) {
        service.remover(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/demandas")
    public ResponseEntity<List<DemandaResponse>> listarDemandas(@PathVariable String id) {
        return ResponseEntity.ok(service.listarDemandas(id));
    }

    @PostMapping("/{id}/demandas")
    public ResponseEntity<DemandaResponse> adicionarDemanda(@PathVariable String id,
                                                            @Valid @RequestBody DemandaCreateRequest requisicao) {
        DemandaResponse criada = service.adicionarDemanda(id, requisicao);
        return ResponseEntity.created(URI.create("/api/abrigos/" + id + "/demandas/" + criada.posicao()))
                .body(criada);
    }

    @PutMapping("/{id}/demandas/{posicao}")
    public ResponseEntity<DemandaResponse> atualizarDemanda(@PathVariable String id,
                                                            @PathVariable int posicao,
                                                            @Valid @RequestBody DemandaUpdateRequest requisicao) {
        return ResponseEntity.ok(service.atualizarDemanda(id, posicao, requisicao));
    }

    @DeleteMapping("/{id}/demandas/{posicao}")
    public ResponseEntity<Void> removerDemanda(@PathVariable String id, @PathVariable int posicao) {
        service.removerDemanda(id, posicao);
        return ResponseEntity.noContent().build();
    }
}
