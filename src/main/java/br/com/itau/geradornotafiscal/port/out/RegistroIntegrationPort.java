package br.com.itau.geradornotafiscal.port.out;

import br.com.itau.geradornotafiscal.model.NotaFiscal;

@FunctionalInterface
public interface RegistroIntegrationPort {
    void registrar(NotaFiscal notaFiscal);
}
