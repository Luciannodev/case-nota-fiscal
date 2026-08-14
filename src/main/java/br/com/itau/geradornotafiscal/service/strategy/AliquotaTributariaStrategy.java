package br.com.itau.geradornotafiscal.service.strategy;

import br.com.itau.geradornotafiscal.model.Destinatario;
import java.math.BigDecimal;

public interface AliquotaTributariaStrategy {

    boolean suporta(Destinatario destinatario);

    BigDecimal calcularAliquota(BigDecimal valorTotalItens);
}
