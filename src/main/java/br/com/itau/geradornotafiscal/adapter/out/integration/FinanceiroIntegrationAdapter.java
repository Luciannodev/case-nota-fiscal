package br.com.itau.geradornotafiscal.adapter.out.integration;

import br.com.itau.geradornotafiscal.core.model.NotaFiscal;
import br.com.itau.geradornotafiscal.port.out.FinanceiroIntegrationPort;
import br.com.itau.geradornotafiscal.observability.ContextoExecucao;
import org.springframework.stereotype.Component;

@Component
public class FinanceiroIntegrationAdapter implements FinanceiroIntegrationPort {
    @Override
    public void enviarContasAReceber(NotaFiscal notaFiscal, ContextoExecucao contexto) {
        SimuladorLatencia.aguardar(250);
    }
}
