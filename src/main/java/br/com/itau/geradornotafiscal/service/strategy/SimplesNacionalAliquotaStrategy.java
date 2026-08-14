package br.com.itau.geradornotafiscal.service.strategy;

import br.com.itau.geradornotafiscal.config.TaxasProperties;
import br.com.itau.geradornotafiscal.model.Destinatario;
import br.com.itau.geradornotafiscal.model.RegimeTributacaoPJ;
import br.com.itau.geradornotafiscal.model.TipoPessoa;
import org.springframework.stereotype.Component;

@Component
public class SimplesNacionalAliquotaStrategy implements AliquotaTributariaStrategy {

    private final TaxasProperties.Faixas taxas;

    public SimplesNacionalAliquotaStrategy(TaxasProperties properties) {
        this.taxas = properties.getSimplesNacional();
    }

    @Override
    public boolean suporta(Destinatario destinatario) {
        return destinatario.getTipoPessoa() == TipoPessoa.JURIDICA
                && destinatario.getRegimeTributacao() == RegimeTributacaoPJ.SIMPLES_NACIONAL;
    }

    @Override
    public double calcularAliquota(double valorTotalItens) {
        if (valorTotalItens < 1_000) {
            return taxas.getFaixa1();
        }
        if (valorTotalItens <= 2_000) {
            return taxas.getFaixa2();
        }
        if (valorTotalItens <= 5_000) {
            return taxas.getFaixa3();
        }
        return taxas.getFaixa4();
    }
}
