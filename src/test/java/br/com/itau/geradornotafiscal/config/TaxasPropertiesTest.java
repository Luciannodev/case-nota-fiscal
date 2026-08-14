package br.com.itau.geradornotafiscal.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Configuration;
import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class TaxasPropertiesTest {

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withUserConfiguration(TestConfig.class)
            .withPropertyValues(
                    "nota-fiscal.taxas.pf.faixa1=0",
                    "nota-fiscal.taxas.pf.faixa2=0.12",
                    "nota-fiscal.taxas.pf.faixa3=0.15",
                    "nota-fiscal.taxas.pf.faixa4=0.17",
                    "nota-fiscal.taxas.simples-nacional.faixa1=0.03",
                    "nota-fiscal.taxas.simples-nacional.faixa2=0.07",
                    "nota-fiscal.taxas.simples-nacional.faixa3=0.13",
                    "nota-fiscal.taxas.simples-nacional.faixa4=0.19",
                    "nota-fiscal.taxas.lucro-real.faixa1=0.03",
                    "nota-fiscal.taxas.lucro-real.faixa2=0.09",
                    "nota-fiscal.taxas.lucro-real.faixa3=0.15",
                    "nota-fiscal.taxas.lucro-real.faixa4=0.20",
                    "nota-fiscal.taxas.lucro-presumido.faixa1=0.03",
                    "nota-fiscal.taxas.lucro-presumido.faixa2=0.09",
                    "nota-fiscal.taxas.lucro-presumido.faixa3=0.16",
                    "nota-fiscal.taxas.lucro-presumido.faixa4=0.20",
                    "nota-fiscal.taxas.frete.norte=1.08",
                    "nota-fiscal.taxas.frete.nordeste=1.085",
                    "nota-fiscal.taxas.frete.centro-oeste=1.07",
                    "nota-fiscal.taxas.frete.sudeste=1.048",
                    "nota-fiscal.taxas.frete.sul=1.06");

    @Test
    void deveUsarTaxaRecebidaPelaConfiguracaoExterna() {
        runner.withPropertyValues("nota-fiscal.taxas.pf.faixa2=0.25")
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context.getBean(TaxasProperties.class).getPf().getFaixa2())
                            .isEqualByComparingTo(new BigDecimal("0.25"));
                });
    }

    @Test
    void deveFalharRapidoQuandoAliquotaForInvalida() {
        runner.withPropertyValues("nota-fiscal.taxas.pf.faixa2=1.20")
                .run(context -> assertThat(context).hasFailed());
    }

    @Configuration(proxyBeanMethods = false)
    @EnableConfigurationProperties(TaxasProperties.class)
    static class TestConfig {
    }
}
