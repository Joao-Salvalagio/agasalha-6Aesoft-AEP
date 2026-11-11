
package br.com.cesumar.agasalha.controller;

import br.com.cesumar.agasalha.controller.dto.MatchRequest;
import br.com.cesumar.agasalha.controller.dto.MatchResponse;
import br.com.cesumar.agasalha.service.MatchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/matches")
@RequiredArgsConstructor
public class MatchController {

    private final MatchService matchService;

    @PostMapping
    public ResponseEntity<MatchResponse> sugerir(@Valid @RequestBody MatchRequest requisicao) {
        return ResponseEntity.ok(matchService.sugerir(requisicao.itemId()));
    }
}
