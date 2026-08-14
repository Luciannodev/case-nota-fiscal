package br.com.itau.geradornotafiscal.support;

import br.com.itau.geradornotafiscal.config.TaxasProperties;

public final class TaxasTestFactory {

    private TaxasTestFactory() {
    }

    public static TaxasProperties taxasPadrao() {
        TaxasProperties properties = new TaxasProperties();
        configurar(properties.getPf(), 0, 0.12, 0.15, 0.17);
        configurar(properties.getSimplesNacional(), 0.03, 0.07, 0.13, 0.19);
        configurar(properties.getLucroReal(), 0.03, 0.09, 0.15, 0.20);
        configurar(properties.getLucroPresumido(), 0.03, 0.09, 0.16, 0.20);
        properties.getFrete().setNorte(1.08);
        properties.getFrete().setNordeste(1.085);
        properties.getFrete().setCentroOeste(1.07);
        properties.getFrete().setSudeste(1.048);
        properties.getFrete().setSul(1.06);
        return properties;
    }

    private static void configurar(TaxasProperties.Faixas faixas, double faixa1, double faixa2,
                                   double faixa3, double faixa4) {
        faixas.setFaixa1(faixa1);
        faixas.setFaixa2(faixa2);
        faixas.setFaixa3(faixa3);
        faixas.setFaixa4(faixa4);
    }
}
