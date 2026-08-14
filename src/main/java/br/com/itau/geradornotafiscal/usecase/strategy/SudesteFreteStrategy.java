package br.com.itau.geradornotafiscal.usecase.strategy;

import br.com.itau.geradornotafiscal.core.model.Regiao;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class SudesteFreteStrategy implements FreteStrategy {
    private final BigDecimal multiplicador;
    public SudesteFreteStrategy(BigDecimal multiplicador) { this.multiplicador = multiplicador; }
    public Regiao regiao() { return Regiao.SUDESTE; }
    public BigDecimal calcular(BigDecimal valorFrete) { return valorFrete.multiply(multiplicador).setScale(2, RoundingMode.HALF_UP); }
}
