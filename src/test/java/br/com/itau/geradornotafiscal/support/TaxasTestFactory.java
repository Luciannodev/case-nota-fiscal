package br.com.itau.geradornotafiscal.support;

import br.com.itau.geradornotafiscal.config.TaxasProperties;
import br.com.itau.geradornotafiscal.usecase.strategy.FaixasAliquota;
import java.math.BigDecimal;

public final class TaxasTestFactory {

    private TaxasTestFactory() {
    }

    public static TaxasProperties taxasPadrao() {
        TaxasProperties properties = new TaxasProperties();
        configurar(properties.getPf(), "0", "0.12", "0.15", "0.17");
        configurar(properties.getSimplesNacional(), "0.03", "0.07", "0.13", "0.19");
        configurar(properties.getLucroReal(), "0.03", "0.09", "0.15", "0.20");
        configurar(properties.getLucroPresumido(), "0.03", "0.09", "0.16", "0.20");
        properties.getFrete().setNorte(new BigDecimal("1.08"));
        properties.getFrete().setNordeste(new BigDecimal("1.085"));
        properties.getFrete().setCentroOeste(new BigDecimal("1.07"));
        properties.getFrete().setSudeste(new BigDecimal("1.048"));
        properties.getFrete().setSul(new BigDecimal("1.06"));
        return properties;
    }

    public static FaixasAliquota faixas(TaxasProperties.Faixas taxas) {
        return new FaixasAliquota(
                taxas.getFaixa1(), taxas.getFaixa2(), taxas.getFaixa3(), taxas.getFaixa4());
    }

    private static void configurar(TaxasProperties.Faixas faixas, String faixa1, String faixa2,
                                   String faixa3, String faixa4) {
        faixas.setFaixa1(new BigDecimal(faixa1));
        faixas.setFaixa2(new BigDecimal(faixa2));
        faixas.setFaixa3(new BigDecimal(faixa3));
        faixas.setFaixa4(new BigDecimal(faixa4));
    }
}
