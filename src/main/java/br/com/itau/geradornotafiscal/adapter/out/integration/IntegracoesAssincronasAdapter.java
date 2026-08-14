package br.com.itau.geradornotafiscal.adapter.out.integration;

import br.com.itau.geradornotafiscal.core.model.NotaFiscal;
import br.com.itau.geradornotafiscal.observability.ContextoExecucao;
import br.com.itau.geradornotafiscal.observability.CorrelationIdContext;
import br.com.itau.geradornotafiscal.observability.EtapaTemporizada;
import br.com.itau.geradornotafiscal.port.out.EntregaIntegrationPort;
import br.com.itau.geradornotafiscal.port.out.EstoqueIntegrationPort;
import br.com.itau.geradornotafiscal.port.out.FinanceiroIntegrationPort;
import br.com.itau.geradornotafiscal.port.out.PublicarIntegracoesNotaFiscalPort;
import br.com.itau.geradornotafiscal.port.out.RegistroIntegrationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Component
public class IntegracoesAssincronasAdapter implements PublicarIntegracoesNotaFiscalPort {

    private static final Logger LOGGER = LoggerFactory.getLogger(IntegracoesAssincronasAdapter.class);

    private final EstoqueIntegrationPort estoque;
    private final RegistroIntegrationPort registro;
    private final EntregaIntegrationPort entrega;
    private final FinanceiroIntegrationPort financeiro;
    private final Executor executor;

    public IntegracoesAssincronasAdapter(EstoqueIntegrationPort estoque,
                                        RegistroIntegrationPort registro,
                                        EntregaIntegrationPort entrega,
                                        FinanceiroIntegrationPort financeiro,
                                        Executor executor) {
        this.estoque = estoque;
        this.registro = registro;
        this.entrega = entrega;
        this.financeiro = financeiro;
        this.executor = executor;
    }

    @Override
    public void publicar(NotaFiscal notaFiscal, ContextoExecucao contexto) {
        CompletableFuture.runAsync(
                        () -> CorrelationIdContext.executar(contexto, () -> processarEmParalelo(notaFiscal, contexto)),
                        executor)
                .exceptionally(exception -> {
                    LOGGER.error("integrations.completed status=error notaFiscal={} correlationId={}",
                            notaFiscal.getIdNotaFiscal(), contexto.correlationId(), exception);
                    return null;
                });
    }

    private void processarEmParalelo(NotaFiscal notaFiscal, ContextoExecucao contexto) {
        EtapaTemporizada.executar(LOGGER, "integrations.total", contexto, () -> {
            CompletableFuture<Void> baixaEstoque = executar(contexto, "integration.estoque",
                    () -> estoque.baixarEstoque(notaFiscal, contexto));
            CompletableFuture<Void> registroNota = executar(contexto, "integration.registro",
                    () -> registro.registrar(notaFiscal, contexto));
            CompletableFuture<Void> agendaEntrega = executar(contexto, "integration.entrega",
                    () -> entrega.agendarEntrega(notaFiscal, contexto));
            CompletableFuture<Void> contasAReceber = executar(contexto, "integration.financeiro",
                    () -> financeiro.enviarContasAReceber(notaFiscal, contexto));

            CompletableFuture.allOf(baixaEstoque, registroNota, agendaEntrega, contasAReceber).join();
        });
    }

    private CompletableFuture<Void> executar(ContextoExecucao contexto, String etapa, Runnable integracao) {
        return CompletableFuture.runAsync(
                () -> CorrelationIdContext.executar(
                        contexto,
                        () -> EtapaTemporizada.executar(LOGGER, etapa, contexto, integracao)),
                executor);
    }
}
