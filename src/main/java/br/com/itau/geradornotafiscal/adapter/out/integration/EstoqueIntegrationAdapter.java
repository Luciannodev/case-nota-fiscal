package br.com.itau.geradornotafiscal.adapter.out.integration;

import br.com.itau.geradornotafiscal.model.NotaFiscal;
import br.com.itau.geradornotafiscal.port.out.EstoqueIntegrationPort;
import org.springframework.stereotype.Component;

@Component
public class EstoqueIntegrationAdapter implements EstoqueIntegrationPort {
    @Override
    public void baixarEstoque(NotaFiscal notaFiscal) {
        SimuladorLatencia.aguardar(380);
    }
}
