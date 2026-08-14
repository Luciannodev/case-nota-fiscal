package br.com.itau.geradornotafiscal.service.strategy;

import br.com.itau.geradornotafiscal.config.TaxasProperties;
import br.com.itau.geradornotafiscal.model.Regiao;
import org.springframework.stereotype.Component;

@Component
public class CentroOesteFreteStrategy implements FreteStrategy {
    private final double multiplicador;
    public CentroOesteFreteStrategy(TaxasProperties properties) { this.multiplicador = properties.getFrete().getCentroOeste(); }
    public Regiao regiao() { return Regiao.CENTRO_OESTE; }
    public double calcular(double valorFrete) { return valorFrete * multiplicador; }
}
