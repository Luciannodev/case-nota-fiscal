package br.com.itau.geradornotafiscal.adapter.in.web.exception;

import java.time.OffsetDateTime;

public record ApiError(
        OffsetDateTime timestamp,
        int status,
        String codigo,
        String mensagem,
        String campo,
        String path,
        String correlationId) {
}
