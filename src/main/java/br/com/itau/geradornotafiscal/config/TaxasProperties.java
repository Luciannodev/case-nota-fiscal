package br.com.itau.geradornotafiscal.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

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
        TaxasConfig.from(this);
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
