package br.com.cesumar.agasalha.controller.dto;

import java.time.Instant;
import java.util.List;

public record ApiError(Instant timestamp, int status, String erro, List<String> detalhes) {
}
