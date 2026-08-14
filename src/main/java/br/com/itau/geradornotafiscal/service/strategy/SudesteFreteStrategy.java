package br.com.itau.geradornotafiscal.service.strategy;

import br.com.itau.geradornotafiscal.config.TaxasProperties;
import br.com.itau.geradornotafiscal.model.Regiao;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class SudesteFreteStrategy implements FreteStrategy {
    private final BigDecimal multiplicador;
    public SudesteFreteStrategy(TaxasProperties properties) { this.multiplicador = properties.getFrete().getSudeste(); }
    public Regiao regiao() { return Regiao.SUDESTE; }
    public BigDecimal calcular(BigDecimal valorFrete) { return valorFrete.multiply(multiplicador).setScale(2, RoundingMode.HALF_UP); }
}
