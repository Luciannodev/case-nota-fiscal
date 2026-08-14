package br.com.itau.geradornotafiscal.port.out;

import br.com.itau.geradornotafiscal.model.NotaFiscal;
import br.com.itau.geradornotafiscal.observability.ContextoExecucao;

@FunctionalInterface
public interface FinanceiroIntegrationPort {
    void enviarContasAReceber(NotaFiscal notaFiscal, ContextoExecucao contexto);
}
