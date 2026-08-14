package br.com.itau.geradornotafiscal.service.strategy;

import br.com.itau.geradornotafiscal.config.TaxasProperties;
import br.com.itau.geradornotafiscal.model.Destinatario;
import br.com.itau.geradornotafiscal.model.TipoPessoa;
import org.springframework.stereotype.Component;

@Component
public class PessoaFisicaAliquotaStrategy implements AliquotaTributariaStrategy {

    private final TaxasProperties.Faixas taxas;

    public PessoaFisicaAliquotaStrategy(TaxasProperties properties) {
        this.taxas = properties.getPf();
    }

    @Override
    public boolean suporta(Destinatario destinatario) {
        return destinatario.getTipoPessoa() == TipoPessoa.FISICA;
    }

    @Override
    public double calcularAliquota(double valorTotalItens) {
        if (valorTotalItens < 500) {
            return taxas.getFaixa1();
        }
        if (valorTotalItens <= 2_000) {
            return taxas.getFaixa2();
        }
        if (valorTotalItens <= 3_500) {
            return taxas.getFaixa3();
        }
        return taxas.getFaixa4();
    }
}
