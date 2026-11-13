
package br.com.cesumar.agasalha.controller.dto;

import jakarta.validation.constraints.NotBlank;

public record DoadorCreateRequest(
        @NotBlank String nome,
        @NotBlank String contato) {
}
