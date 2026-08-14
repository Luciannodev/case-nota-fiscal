package br.com.itau.geradornotafiscal.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.math.BigDecimal;

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
            if (valor == null || valor.signum() <= 0) {
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
            if (valor == null || valor.signum() < 0 || valor.compareTo(BigDecimal.ONE) > 0) {
                throw new IllegalStateException("Alíquota inválida: " + nome + "." + faixa);
            }
        });
    }

    @Getter
    @Setter
    public static class Faixas {
        private BigDecimal faixa1;
        private BigDecimal faixa2;
        private BigDecimal faixa3;
        private BigDecimal faixa4;
    }

    @Getter
    @Setter
    public static class Frete {
        private BigDecimal norte;
        private BigDecimal nordeste;
        private BigDecimal centroOeste;
        private BigDecimal sudeste;
        private BigDecimal sul;
    }
}
