package br.com.itau.geradornotafiscal.observability;

import org.slf4j.MDC;

import java.util.function.Supplier;

public final class CorrelationIdContext {

    public static final String HEADER = "X-Correlation-ID";
    public static final String MDC_KEY = "correlationId";

    private CorrelationIdContext() {
    }

    public static ContextoExecucao atual() {
        String correlationId = MDC.get(MDC_KEY);
        if (correlationId == null || correlationId.isBlank()) {
            throw new IllegalStateException("Correlation ID não inicializado");
        }
        return new ContextoExecucao(correlationId);
    }

    public static void executar(ContextoExecucao contexto, Runnable acao) {
        executar(contexto, () -> {
            acao.run();
            return null;
        });
    }

    public static <T> T executar(ContextoExecucao contexto, Supplier<T> acao) {
        String anterior = MDC.get(MDC_KEY);
        try {
            MDC.put(MDC_KEY, contexto.correlationId());
            return acao.get();
        } finally {
            if (anterior == null) {
                MDC.remove(MDC_KEY);
            } else {
                MDC.put(MDC_KEY, anterior);
            }
        }
    }
}
