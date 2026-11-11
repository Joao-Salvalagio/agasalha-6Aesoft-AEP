
package br.com.cesumar.agasalha.controller.dto;

import java.time.Instant;

public record DoacaoResponse(String id, String doadorId, String abrigoId, Instant dataDoacao) {
}
