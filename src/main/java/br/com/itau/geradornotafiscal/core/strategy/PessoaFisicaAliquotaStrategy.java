package br.com.itau.geradornotafiscal.core.strategy;

import br.com.itau.geradornotafiscal.core.model.Destinatario;
import br.com.itau.geradornotafiscal.core.model.TipoPessoa;
import java.math.BigDecimal;

public class PessoaFisicaAliquotaStrategy implements AliquotaTributariaStrategy {

    private final FaixasAliquota taxas;

    public PessoaFisicaAliquotaStrategy(FaixasAliquota taxas) {
        this.taxas = taxas;
    }

    @Override
    public boolean suporta(Destinatario destinatario) {
        return destinatario.getTipoPessoa() == TipoPessoa.FISICA;
    }

    @Override
    public BigDecimal calcularAliquota(BigDecimal valorTotalItens) {
        if (valorTotalItens.compareTo(BigDecimal.valueOf(500)) < 0) {
            return taxas.faixa1();
        }
        if (valorTotalItens.compareTo(BigDecimal.valueOf(2_000)) <= 0) {
            return taxas.faixa2();
        }
        if (valorTotalItens.compareTo(BigDecimal.valueOf(3_500)) <= 0) {
            return taxas.faixa3();
        }
        return taxas.faixa4();
    }
}
