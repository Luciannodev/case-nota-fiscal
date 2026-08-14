package br.com.itau.geradornotafiscal.service.strategy;

import br.com.itau.geradornotafiscal.model.Regiao;
import org.springframework.stereotype.Component;

@Component
public class NorteFreteStrategy implements FreteStrategy {
    public Regiao regiao() { return Regiao.NORTE; }
    public double calcular(double valorFrete) { return valorFrete * 1.08; }
}
