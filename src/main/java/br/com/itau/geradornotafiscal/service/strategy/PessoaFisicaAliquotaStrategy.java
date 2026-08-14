package br.com.itau.geradornotafiscal.service.strategy;

import br.com.itau.geradornotafiscal.model.Destinatario;
import br.com.itau.geradornotafiscal.model.TipoPessoa;
import org.springframework.stereotype.Component;

@Component
public class PessoaFisicaAliquotaStrategy implements AliquotaTributariaStrategy {

    @Override
    public boolean suporta(Destinatario destinatario) {
        return destinatario.getTipoPessoa() == TipoPessoa.FISICA;
    }

    @Override
    public double calcularAliquota(double valorTotalItens) {
        if (valorTotalItens < 500) {
            return 0;
        }
        if (valorTotalItens <= 2_000) {
            return 0.12;
        }
        if (valorTotalItens <= 3_500) {
            return 0.15;
        }
        return 0.17;
    }
}
