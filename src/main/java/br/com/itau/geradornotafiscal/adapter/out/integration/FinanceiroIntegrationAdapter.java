package br.com.itau.geradornotafiscal.adapter.out.integration;

import br.com.itau.geradornotafiscal.model.NotaFiscal;
import br.com.itau.geradornotafiscal.port.out.FinanceiroIntegrationPort;
import org.springframework.stereotype.Component;

@Component
public class FinanceiroIntegrationAdapter implements FinanceiroIntegrationPort {
    @Override
    public void enviarContasAReceber(NotaFiscal notaFiscal) {
        SimuladorLatencia.aguardar(250);
    }
}
