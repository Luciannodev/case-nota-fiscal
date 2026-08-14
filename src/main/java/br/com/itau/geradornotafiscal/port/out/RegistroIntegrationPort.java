package br.com.itau.geradornotafiscal.port.out;

import br.com.itau.geradornotafiscal.model.NotaFiscal;
import br.com.itau.geradornotafiscal.observability.ContextoExecucao;

@FunctionalInterface
public interface RegistroIntegrationPort {
    void registrar(NotaFiscal notaFiscal, ContextoExecucao contexto);
}
