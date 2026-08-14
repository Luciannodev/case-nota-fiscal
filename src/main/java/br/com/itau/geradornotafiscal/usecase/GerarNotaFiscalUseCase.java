package br.com.itau.geradornotafiscal.usecase;

import br.com.itau.geradornotafiscal.core.model.NotaFiscal;
import br.com.itau.geradornotafiscal.core.model.Pedido;
import br.com.itau.geradornotafiscal.observability.ContextoExecucao;

public interface GerarNotaFiscalUseCase {
    NotaFiscal gerarNotaFiscal(Pedido pedido, ContextoExecucao contexto);
}
