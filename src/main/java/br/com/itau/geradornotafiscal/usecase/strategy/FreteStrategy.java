package br.com.itau.geradornotafiscal.usecase.strategy;

import br.com.itau.geradornotafiscal.core.model.Regiao;
import java.math.BigDecimal;

public interface FreteStrategy {

    Regiao regiao();

    BigDecimal calcular(BigDecimal valorFrete);
}
