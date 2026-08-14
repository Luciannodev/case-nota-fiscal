package br.com.itau.geradornotafiscal.port.in;

import br.com.itau.geradornotafiscal.model.NotaFiscal;
import br.com.itau.geradornotafiscal.model.Pedido;
import br.com.itau.geradornotafiscal.observability.ContextoExecucao;

public interface GerarNotaFiscalUseCase {
    NotaFiscal gerarNotaFiscal(Pedido pedido, ContextoExecucao contexto);
}
