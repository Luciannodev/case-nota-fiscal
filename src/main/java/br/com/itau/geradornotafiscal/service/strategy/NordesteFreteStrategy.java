package br.com.itau.geradornotafiscal.service.strategy;

import br.com.itau.geradornotafiscal.config.TaxasProperties;
import br.com.itau.geradornotafiscal.model.Regiao;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class NordesteFreteStrategy implements FreteStrategy {
    private final BigDecimal multiplicador;
    public NordesteFreteStrategy(TaxasProperties properties) { this.multiplicador = properties.getFrete().getNordeste(); }
    public Regiao regiao() { return Regiao.NORDESTE; }
    public BigDecimal calcular(BigDecimal valorFrete) { return valorFrete.multiply(multiplicador).setScale(2, RoundingMode.HALF_UP); }
}
