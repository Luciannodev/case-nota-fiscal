package br.com.itau.geradornotafiscal.core.strategy;

import br.com.itau.geradornotafiscal.core.model.Regiao;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class NordesteFreteStrategy implements FreteStrategy {
    private final BigDecimal multiplicador;
    public NordesteFreteStrategy(BigDecimal multiplicador) { this.multiplicador = multiplicador; }
    public Regiao regiao() { return Regiao.NORDESTE; }
    public BigDecimal calcular(BigDecimal valorFrete) { return valorFrete.multiply(multiplicador).setScale(2, RoundingMode.HALF_UP); }
}
