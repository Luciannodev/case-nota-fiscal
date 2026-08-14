package br.com.itau.geradornotafiscal.usecase.strategy;

import br.com.itau.geradornotafiscal.core.model.Destinatario;
import br.com.itau.geradornotafiscal.core.model.RegimeTributacaoPJ;
import br.com.itau.geradornotafiscal.core.model.TipoPessoa;
import java.math.BigDecimal;

public class LucroRealAliquotaStrategy implements AliquotaTributariaStrategy {

    private final FaixasAliquota taxas;

    public LucroRealAliquotaStrategy(FaixasAliquota taxas) {
        this.taxas = taxas;
    }

    @Override
    public boolean suporta(Destinatario destinatario) {
        return destinatario.getTipoPessoa() == TipoPessoa.JURIDICA
                && destinatario.getRegimeTributacao() == RegimeTributacaoPJ.LUCRO_REAL;
    }

    @Override
    public BigDecimal calcularAliquota(BigDecimal valorTotalItens) {
        if (valorTotalItens.compareTo(BigDecimal.valueOf(1_000)) < 0) {
            return taxas.faixa1();
        }
        if (valorTotalItens.compareTo(BigDecimal.valueOf(2_000)) <= 0) {
            return taxas.faixa2();
        }
        if (valorTotalItens.compareTo(BigDecimal.valueOf(5_000)) <= 0) {
            return taxas.faixa3();
        }
        return taxas.faixa4();
    }
}
