package br.com.itau.geradornotafiscal.port.out;

import br.com.itau.geradornotafiscal.core.model.NotaFiscal;
import br.com.itau.geradornotafiscal.observability.ContextoExecucao;

@FunctionalInterface
public interface PublicarIntegracoesNotaFiscalPort {
    void publicar(NotaFiscal notaFiscal, ContextoExecucao contexto);
}
