package br.com.itau.geradornotafiscal.adapter.out.integration;

import br.com.itau.geradornotafiscal.core.model.NotaFiscal;
import br.com.itau.geradornotafiscal.port.out.EntregaIntegrationPort;
import br.com.itau.geradornotafiscal.observability.ContextoExecucao;
import org.springframework.stereotype.Component;

@Component
public class EntregaIntegrationAdapter implements EntregaIntegrationPort {
    @Override
    public void agendarEntrega(NotaFiscal notaFiscal, ContextoExecucao contexto) {
        SimuladorLatencia.aguardar(150);
        if (notaFiscal.getItens().size() > 5) {
            SimuladorLatencia.aguardar(5_000);
        }
        SimuladorLatencia.aguardar(200);
    }
}
