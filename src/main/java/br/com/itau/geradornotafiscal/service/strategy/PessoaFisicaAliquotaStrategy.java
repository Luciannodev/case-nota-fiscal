package br.com.itau.geradornotafiscal.service.strategy;

import br.com.itau.geradornotafiscal.config.TaxasProperties;
import br.com.itau.geradornotafiscal.model.Destinatario;
import br.com.itau.geradornotafiscal.model.TipoPessoa;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

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
    public BigDecimal calcularAliquota(BigDecimal valorTotalItens) {
        if (valorTotalItens.compareTo(BigDecimal.valueOf(500)) < 0) {
            return taxas.getFaixa1();
        }
        if (valorTotalItens.compareTo(BigDecimal.valueOf(2_000)) <= 0) {
            return taxas.getFaixa2();
        }
        if (valorTotalItens.compareTo(BigDecimal.valueOf(3_500)) <= 0) {
            return taxas.getFaixa3();
        }
        return taxas.getFaixa4();
    }
}
