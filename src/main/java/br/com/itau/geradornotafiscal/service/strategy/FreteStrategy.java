package br.com.itau.geradornotafiscal.service.strategy;

import br.com.itau.geradornotafiscal.model.Regiao;
import java.math.BigDecimal;

public interface FreteStrategy {

    Regiao regiao();

    BigDecimal calcular(BigDecimal valorFrete);
}
