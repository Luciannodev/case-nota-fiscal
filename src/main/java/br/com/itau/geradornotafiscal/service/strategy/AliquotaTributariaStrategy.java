package br.com.itau.geradornotafiscal.service.strategy;

import br.com.itau.geradornotafiscal.model.Destinatario;

public interface AliquotaTributariaStrategy {

    boolean suporta(Destinatario destinatario);

    double calcularAliquota(double valorTotalItens);
}
