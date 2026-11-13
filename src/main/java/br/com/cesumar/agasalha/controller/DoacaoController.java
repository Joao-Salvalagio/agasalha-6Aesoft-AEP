
package br.com.cesumar.agasalha.controller;

import br.com.cesumar.agasalha.controller.dto.DoacaoCreateRequest;
import br.com.cesumar.agasalha.controller.dto.DoacaoResponse;
import br.com.cesumar.agasalha.controller.dto.ItemDoacaoRequest;
import br.com.cesumar.agasalha.controller.dto.ItemResponse;
import br.com.cesumar.agasalha.service.DoacaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/doacoes")
@RequiredArgsConstructor
public class DoacaoController {

    private final DoacaoService service;

    @PostMapping
    public ResponseEntity<DoacaoResponse> criar(@Valid @RequestBody DoacaoCreateRequest requisicao) {
        DoacaoResponse criada = service.criar(requisicao);
        return ResponseEntity.created(URI.create("/api/doacoes/" + criada.id())).body(criada);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DoacaoResponse> buscarPorId(@PathVariable String id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PostMapping("/{id}/itens")
    public ResponseEntity<ItemResponse> criarItem(@PathVariable String id,
                                                  @Valid @RequestBody ItemDoacaoRequest requisicao) {
        ItemResponse criado = service.criarItem(id, requisicao);
        return ResponseEntity.created(URI.create("/api/itens/" + criado.id())).body(criado);
    }
}
