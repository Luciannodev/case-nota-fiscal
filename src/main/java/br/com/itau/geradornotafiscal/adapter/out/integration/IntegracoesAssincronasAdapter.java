package br.com.itau.geradornotafiscal.adapter.out.integration;

import br.com.itau.geradornotafiscal.model.NotaFiscal;
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
    public void publicar(NotaFiscal notaFiscal) {
        CompletableFuture.runAsync(() -> processarEmParalelo(notaFiscal), executor)
                .exceptionally(exception -> {
                    LOGGER.error("Falha no processamento assíncrono da notaFiscal={}",
                            notaFiscal.getIdNotaFiscal(), exception);
                    return null;
                });
    }

    private void processarEmParalelo(NotaFiscal notaFiscal) {
        CompletableFuture<Void> baixaEstoque = executar(() -> estoque.baixarEstoque(notaFiscal));
        CompletableFuture<Void> registroNota = executar(() -> registro.registrar(notaFiscal));
        CompletableFuture<Void> agendaEntrega = executar(() -> entrega.agendarEntrega(notaFiscal));
        CompletableFuture<Void> contasAReceber = executar(() -> financeiro.enviarContasAReceber(notaFiscal));

        CompletableFuture.allOf(baixaEstoque, registroNota, agendaEntrega, contasAReceber).join();
    }

    private CompletableFuture<Void> executar(Runnable integracao) {
        return CompletableFuture.runAsync(integracao, executor);
    }
}
