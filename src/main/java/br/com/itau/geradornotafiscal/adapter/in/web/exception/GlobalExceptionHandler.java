package br.com.itau.geradornotafiscal.adapter.in.web.exception;

import br.com.itau.geradornotafiscal.observability.CorrelationIdContext;
import br.com.itau.geradornotafiscal.usecase.exception.NotaFiscalException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(NotaFiscalException.class)
    ResponseEntity<ApiError> tratarErroDeNegocio(NotaFiscalException exception, HttpServletRequest request) {
        LOGGER.warn("A requisição foi rejeitada por uma regra de negócio. codigo={} campo={} correlationId={}",
                exception.getCodigo(), exception.getCampo(), CorrelationIdContext.atual().correlationId());
        return resposta(HttpStatus.BAD_REQUEST, exception.getCodigo(), exception.getMessage(),
                exception.getCampo(), request);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    ResponseEntity<ApiError> tratarEntradaMalformada(HttpMessageNotReadableException exception,
                                                      HttpServletRequest request) {
        LOGGER.warn("Não foi possível interpretar o JSON recebido. codigo=ENTRADA_MALFORMADA correlationId={}",
                CorrelationIdContext.atual().correlationId());
        return resposta(HttpStatus.BAD_REQUEST, "ENTRADA_MALFORMADA",
                "O corpo da requisição contém JSON ou valores inválidos", null, request);
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ApiError> tratarErroInesperado(Exception exception, HttpServletRequest request) {
        LOGGER.error("Falha inesperada ao processar a requisição. codigo=ERRO_INTERNO correlationId={}",
                CorrelationIdContext.atual().correlationId(), exception);
        return resposta(HttpStatus.INTERNAL_SERVER_ERROR, "ERRO_INTERNO",
                "Não foi possível processar a solicitação", null, request);
    }

    private ResponseEntity<ApiError> resposta(HttpStatus status, String codigo, String mensagem,
                                               String campo, HttpServletRequest request) {
        ApiError erro = new ApiError(
                OffsetDateTime.now(),
                status.value(),
                codigo,
                mensagem,
                campo,
                request.getRequestURI(),
                CorrelationIdContext.atual().correlationId());
        return ResponseEntity.status(status).body(erro);
    }
}
