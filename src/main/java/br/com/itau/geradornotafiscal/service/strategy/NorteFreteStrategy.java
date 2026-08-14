package br.com.itau.geradornotafiscal.service.strategy;

import br.com.itau.geradornotafiscal.config.TaxasProperties;
import br.com.itau.geradornotafiscal.model.Regiao;
import org.springframework.stereotype.Component;

@Component
public class NorteFreteStrategy implements FreteStrategy {
    private final double multiplicador;
    public NorteFreteStrategy(TaxasProperties properties) { this.multiplicador = properties.getFrete().getNorte(); }
    public Regiao regiao() { return Regiao.NORTE; }
    public double calcular(double valorFrete) { return valorFrete * multiplicador; }
}
