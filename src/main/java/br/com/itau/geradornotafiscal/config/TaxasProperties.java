package br.com.itau.geradornotafiscal.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Getter
@Component
@ConfigurationProperties(prefix = "nota-fiscal.taxas")
public class TaxasProperties implements InitializingBean {

    private final Faixas pf = new Faixas();
    private final Faixas simplesNacional = new Faixas();
    private final Faixas lucroReal = new Faixas();
    private final Faixas lucroPresumido = new Faixas();
    private final Frete frete = new Frete();

    @Override
    public void afterPropertiesSet() {
        validarAliquotas("pf", pf);
        validarAliquotas("simples-nacional", simplesNacional);
        validarAliquotas("lucro-real", lucroReal);
        validarAliquotas("lucro-presumido", lucroPresumido);

        Map.of(
                "norte", frete.norte,
                "nordeste", frete.nordeste,
                "centro-oeste", frete.centroOeste,
                "sudeste", frete.sudeste,
                "sul", frete.sul
        ).forEach((nome, valor) -> {
            if (!Double.isFinite(valor) || valor <= 0) {
                throw new IllegalStateException("Multiplicador de frete inválido: " + nome);
            }
        });
    }

    private void validarAliquotas(String nome, Faixas faixas) {
        Map.of(
                "faixa1", faixas.faixa1,
                "faixa2", faixas.faixa2,
                "faixa3", faixas.faixa3,
                "faixa4", faixas.faixa4
        ).forEach((faixa, valor) -> {
            if (!Double.isFinite(valor) || valor < 0 || valor > 1) {
                throw new IllegalStateException("Alíquota inválida: " + nome + "." + faixa);
            }
        });
    }

    @Getter
    @Setter
    public static class Faixas {
        private double faixa1;
        private double faixa2;
        private double faixa3;
        private double faixa4;
    }

    @Getter
    @Setter
    public static class Frete {
        private double norte;
        private double nordeste;
        private double centroOeste;
        private double sudeste;
        private double sul;
    }
}
