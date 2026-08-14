package br.com.itau.geradornotafiscal.service.strategy;

import br.com.itau.geradornotafiscal.config.TaxasProperties;
import br.com.itau.geradornotafiscal.model.Regiao;
import org.springframework.stereotype.Component;

@Component
public class NordesteFreteStrategy implements FreteStrategy {
    private final double multiplicador;
    public NordesteFreteStrategy(TaxasProperties properties) { this.multiplicador = properties.getFrete().getNordeste(); }
    public Regiao regiao() { return Regiao.NORDESTE; }
    public double calcular(double valorFrete) { return valorFrete * multiplicador; }
}
