package br.com.itau.geradornotafiscal.service.strategy;

import br.com.itau.geradornotafiscal.model.Destinatario;
import br.com.itau.geradornotafiscal.model.RegimeTributacaoPJ;
import br.com.itau.geradornotafiscal.model.TipoPessoa;
import org.springframework.stereotype.Component;

@Component
public class SimplesNacionalAliquotaStrategy implements AliquotaTributariaStrategy {

    @Override
    public boolean suporta(Destinatario destinatario) {
        return destinatario.getTipoPessoa() == TipoPessoa.JURIDICA
                && destinatario.getRegimeTributacao() == RegimeTributacaoPJ.SIMPLES_NACIONAL;
    }

    @Override
    public double calcularAliquota(double valorTotalItens) {
        if (valorTotalItens < 1_000) {
            return 0.03;
        }
        if (valorTotalItens <= 2_000) {
            return 0.07;
        }
        if (valorTotalItens <= 5_000) {
            return 0.13;
        }
        return 0.19;
    }
}
