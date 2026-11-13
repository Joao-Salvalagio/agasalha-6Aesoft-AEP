
package br.com.cesumar.agasalha.controller.dto;

import jakarta.validation.constraints.NotBlank;

public record DoacaoCreateRequest(
        @NotBlank String doadorId,
        String abrigoId) {
}
