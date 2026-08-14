package br.com.itau.geradornotafiscal.service.strategy;

import br.com.itau.geradornotafiscal.config.TaxasProperties;
import br.com.itau.geradornotafiscal.model.Regiao;
import org.springframework.stereotype.Component;

@Component
public class SudesteFreteStrategy implements FreteStrategy {
    private final double multiplicador;
    public SudesteFreteStrategy(TaxasProperties properties) { this.multiplicador = properties.getFrete().getSudeste(); }
    public Regiao regiao() { return Regiao.SUDESTE; }
    public double calcular(double valorFrete) { return valorFrete * multiplicador; }
}
