package br.com.itau.geradornotafiscal.service.strategy;

import br.com.itau.geradornotafiscal.config.TaxasProperties;
import br.com.itau.geradornotafiscal.model.Regiao;
import org.springframework.stereotype.Component;

@Component
public class SulFreteStrategy implements FreteStrategy {
    private final double multiplicador;
    public SulFreteStrategy(TaxasProperties properties) { this.multiplicador = properties.getFrete().getSul(); }
    public Regiao regiao() { return Regiao.SUL; }
    public double calcular(double valorFrete) { return valorFrete * multiplicador; }
}
