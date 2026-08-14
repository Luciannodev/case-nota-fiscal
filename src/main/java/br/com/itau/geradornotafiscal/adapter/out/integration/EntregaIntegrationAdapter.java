package br.com.itau.geradornotafiscal.adapter.out.integration;

import br.com.itau.geradornotafiscal.model.NotaFiscal;
import br.com.itau.geradornotafiscal.port.out.EntregaIntegrationPort;
import org.springframework.stereotype.Component;

@Component
public class EntregaIntegrationAdapter implements EntregaIntegrationPort {
    @Override
    public void agendarEntrega(NotaFiscal notaFiscal) {
        SimuladorLatencia.aguardar(150);
        if (notaFiscal.getItens().size() > 5) {
            SimuladorLatencia.aguardar(5_000);
        }
        SimuladorLatencia.aguardar(200);
    }
}
