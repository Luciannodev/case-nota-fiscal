package br.com.itau.geradornotafiscal.usecase.calculo;

import br.com.itau.geradornotafiscal.core.model.Destinatario;
import br.com.itau.geradornotafiscal.core.model.Item;
import br.com.itau.geradornotafiscal.core.model.ItemNotaFiscal;
import br.com.itau.geradornotafiscal.core.model.Pedido;
import br.com.itau.geradornotafiscal.core.model.RegimeTributacaoPJ;
import br.com.itau.geradornotafiscal.core.model.TipoPessoa;
import br.com.itau.geradornotafiscal.config.TaxasProperties;
import br.com.itau.geradornotafiscal.usecase.strategy.LucroPresumidoAliquotaStrategy;
import br.com.itau.geradornotafiscal.usecase.strategy.LucroRealAliquotaStrategy;
import br.com.itau.geradornotafiscal.usecase.strategy.PessoaFisicaAliquotaStrategy;
import br.com.itau.geradornotafiscal.usecase.strategy.SimplesNacionalAliquotaStrategy;
import br.com.itau.geradornotafiscal.usecase.exception.RegraCalculoNaoEncontradaException;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static br.com.itau.geradornotafiscal.support.TaxasTestFactory.faixas;
import static br.com.itau.geradornotafiscal.support.TaxasTestFactory.taxasPadrao;

class CalculadoraTributosTest {

    private static final TaxasProperties TAXAS = taxasPadrao();

    private final CalculadoraTributos calculadora = new CalculadoraTributos(
            List.of(
                    new PessoaFisicaAliquotaStrategy(faixas(TAXAS.getPf())),
                    new SimplesNacionalAliquotaStrategy(faixas(TAXAS.getSimplesNacional())),
                    new LucroRealAliquotaStrategy(faixas(TAXAS.getLucroReal())),
                    new LucroPresumidoAliquotaStrategy(faixas(TAXAS.getLucroPresumido()))),
            new CalculadoraAliquotaProduto());

    @Test
    void deveSelecionarEAplicarARegraDoSimplesNacional() {
        Pedido pedido = pedido(TipoPessoa.JURIDICA, RegimeTributacaoPJ.SIMPLES_NACIONAL, 5_001);

        List<ItemNotaFiscal> itens = calculadora.calcular(pedido);

        assertEquals(new BigDecimal("950.19"), itens.getFirst().getValorTributoItem());
    }

    @Test
    void deveRejeitarRegimeSemRegraTributaria() {
        Pedido pedido = pedido(TipoPessoa.JURIDICA, RegimeTributacaoPJ.OUTROS, 1_000);

        RegraCalculoNaoEncontradaException exception = assertThrows(
                RegraCalculoNaoEncontradaException.class, () -> calculadora.calcular(pedido));
        assertEquals("REGRA_CALCULO_NAO_ENCONTRADA", exception.getCodigo());
    }

    private Pedido pedido(TipoPessoa tipoPessoa, RegimeTributacaoPJ regime, double valor) {
        Destinatario destinatario = Destinatario.builder()
                .tipoPessoa(tipoPessoa)
                .regimeTributacao(regime)
                .build();
        BigDecimal valorMonetario = BigDecimal.valueOf(valor);
        Item item = new Item("1", "Produto", valorMonetario, 1);
        return Pedido.builder()
                .valorTotalItens(valorMonetario)
                .destinatario(destinatario)
                .itens(List.of(item))
                .build();
    }
}
