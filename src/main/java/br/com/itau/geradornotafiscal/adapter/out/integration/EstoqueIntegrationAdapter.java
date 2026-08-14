package br.com.itau.geradornotafiscal.adapter.out.integration;

import br.com.itau.geradornotafiscal.core.model.NotaFiscal;
import br.com.itau.geradornotafiscal.port.out.EstoqueIntegrationPort;
import br.com.itau.geradornotafiscal.observability.ContextoExecucao;
import org.springframework.stereotype.Component;

@Component
public class EstoqueIntegrationAdapter implements EstoqueIntegrationPort {
    @Override
    public void baixarEstoque(NotaFiscal notaFiscal, ContextoExecucao contexto) {
        SimuladorLatencia.aguardar(380);
    }
}
