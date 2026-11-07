package br.com.cesumar.agasalha.controller.dto;

import jakarta.validation.constraints.NotBlank;

public record MatchRequest(@NotBlank String itemId) {
}
