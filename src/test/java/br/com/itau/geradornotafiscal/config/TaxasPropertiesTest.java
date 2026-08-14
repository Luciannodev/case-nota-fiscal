package br.com.itau.geradornotafiscal.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;

class TaxasPropertiesTest {

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withUserConfiguration(TestConfig.class)
            .withPropertyValues(
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
                    assertThat(context.getBean(TaxasProperties.class).getPf().getFaixa2()).isEqualTo(0.25);
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
