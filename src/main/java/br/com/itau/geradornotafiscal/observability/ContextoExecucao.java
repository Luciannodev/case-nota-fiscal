package br.com.itau.geradornotafiscal.observability;

import java.util.Objects;

public record ContextoExecucao(String correlationId) {
    public ContextoExecucao {
        Objects.requireNonNull(correlationId, "correlationId é obrigatório");
    }
}
