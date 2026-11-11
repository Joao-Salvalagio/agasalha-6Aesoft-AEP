
package br.com.cesumar.agasalha.controller.dto;

import jakarta.validation.constraints.NotBlank;

public record EnderecoRequest(
        @NotBlank String logradouro,
        String numero,
        String bairro,
        @NotBlank String cidade,
        @NotBlank String estado) {
}
