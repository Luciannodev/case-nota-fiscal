package br.com.itau.geradornotafiscal.adapter.out.integration;

import br.com.itau.geradornotafiscal.model.NotaFiscal;
import br.com.itau.geradornotafiscal.port.out.RegistroIntegrationPort;
import br.com.itau.geradornotafiscal.observability.ContextoExecucao;
import org.springframework.stereotype.Component;

@Component
public class RegistroIntegrationAdapter implements RegistroIntegrationPort {
    @Override
    public void registrar(NotaFiscal notaFiscal, ContextoExecucao contexto) {
        SimuladorLatencia.aguardar(500);
    }
}
