package br.com.cesumar.agasalha.exception;

import br.com.cesumar.agasalha.controller.dto.ApiError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.Instant;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> tratarCorpoInvalido(MethodArgumentNotValidException ex) {
        List<String> detalhes = ex.getBindingResult().getFieldErrors().stream()
                .map(GlobalExceptionHandler::formatarErroDeCampo)
                .toList();
        return construir(HttpStatus.BAD_REQUEST, "dados invalidos", detalhes);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> tratarParametroInvalido(MethodArgumentTypeMismatchException ex) {
        String detalhe = "parametro " + ex.getName() + ": valor invalido";
        return construir(HttpStatus.BAD_REQUEST, "parametro invalido", List.of(detalhe));
    }

    @ExceptionHandler(DadosInvalidosException.class)
    public ResponseEntity<ApiError> tratarDadosInvalidos(DadosInvalidosException ex) {
        return construir(HttpStatus.BAD_REQUEST, ex.getMessage(), List.of());
    }

    @ExceptionHandler(ItemNaoEncontradoException.class)
    public ResponseEntity<ApiError> tratarItemNaoEncontrado(ItemNaoEncontradoException ex) {
        return construir(HttpStatus.NOT_FOUND, ex.getMessage(), List.of());
    }

    @ExceptionHandler(TransicaoInvalidaException.class)
    public ResponseEntity<ApiError> tratarTransicaoInvalida(TransicaoInvalidaException ex) {
        return construir(HttpStatus.CONFLICT, ex.getMessage(), List.of());
    }

    private static String formatarErroDeCampo(FieldError erro) {
        return "campo " + erro.getField() + ": " + erro.getDefaultMessage();
    }

    private static ResponseEntity<ApiError> construir(HttpStatus status, String mensagem, List<String> detalhes) {
        return ResponseEntity.status(status).body(new ApiError(Instant.now(), status.value(), mensagem, detalhes));
    }
}