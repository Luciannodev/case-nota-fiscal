package br.com.itau.geradornotafiscal.port.out;

import br.com.itau.geradornotafiscal.core.model.NotaFiscal;
import br.com.itau.geradornotafiscal.observability.ContextoExecucao;

@FunctionalInterface
public interface EstoqueIntegrationPort {
    void baixarEstoque(NotaFiscal notaFiscal, ContextoExecucao contexto);
}
