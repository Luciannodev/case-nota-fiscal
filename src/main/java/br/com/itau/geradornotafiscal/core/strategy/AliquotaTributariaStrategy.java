package br.com.itau.geradornotafiscal.core.strategy;

import br.com.itau.geradornotafiscal.core.model.Destinatario;
import java.math.BigDecimal;

public interface AliquotaTributariaStrategy {

    boolean suporta(Destinatario destinatario);

    BigDecimal calcularAliquota(BigDecimal valorTotalItens);
}
