package br.com.itau.geradornotafiscal.observability;

import org.slf4j.Logger;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public final class EtapaTemporizada {

    private EtapaTemporizada() {
    }

    public static <T> T executar(Logger logger, EtapaFluxo etapa, ContextoExecucao contexto, Supplier<T> acao) {
        long inicio = System.nanoTime();
        logger.info("Iniciando etapa: {}. event=step.started step={} correlationId={}",
                etapa.descricao(), etapa.codigo(), contexto.correlationId());
        try {
            T resultado = acao.get();
            logger.info("Etapa concluída: {}. event=step.completed step={} status=success durationMs={} correlationId={}",
                    etapa.descricao(), etapa.codigo(), duracaoMs(inicio), contexto.correlationId());
            return resultado;
        } catch (RuntimeException exception) {
            logger.error("Etapa falhou: {}. event=step.completed step={} status=error durationMs={} correlationId={}",
                    etapa.descricao(), etapa.codigo(), duracaoMs(inicio), contexto.correlationId(), exception);
            throw exception;
        }
    }

    public static void executar(Logger logger, EtapaFluxo etapa, ContextoExecucao contexto, Runnable acao) {
        executar(logger, etapa, contexto, () -> {
            acao.run();
            return null;
        });
    }

    private static long duracaoMs(long inicio) {
        return TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - inicio);
    }
}
