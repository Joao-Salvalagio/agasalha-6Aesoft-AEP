
package br.com.cesumar.agasalha.controller;

import br.com.cesumar.agasalha.controller.dto.DoadorCreateRequest;
import br.com.cesumar.agasalha.controller.dto.DoadorResponse;
import br.com.cesumar.agasalha.controller.dto.DoadorUpdateRequest;
import br.com.cesumar.agasalha.service.DoadorService;
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
@RequestMapping("/api/doadores")
@RequiredArgsConstructor
public class DoadorController {

    private final DoadorService service;

    @PostMapping
    public ResponseEntity<DoadorResponse> criar(@Valid @RequestBody DoadorCreateRequest requisicao) {
        DoadorResponse criado = service.criar(requisicao);
        return ResponseEntity.created(URI.create("/api/doadores/" + criado.id())).body(criado);
    }

    @GetMapping
    public ResponseEntity<List<DoadorResponse>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DoadorResponse> buscarPorId(@PathVariable String id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DoadorResponse> atualizar(@PathVariable String id,
                                                    @Valid @RequestBody DoadorUpdateRequest requisicao) {
        return ResponseEntity.ok(service.atualizar(id, requisicao));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable String id) {
        service.remover(id);
        return ResponseEntity.noContent().build();
    }
}
