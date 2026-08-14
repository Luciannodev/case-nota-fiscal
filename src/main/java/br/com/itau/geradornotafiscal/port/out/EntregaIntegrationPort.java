package br.com.itau.geradornotafiscal.port.out;

import br.com.itau.geradornotafiscal.model.NotaFiscal;
import br.com.itau.geradornotafiscal.observability.ContextoExecucao;

@FunctionalInterface
public interface EntregaIntegrationPort {
    void agendarEntrega(NotaFiscal notaFiscal, ContextoExecucao contexto);
}
