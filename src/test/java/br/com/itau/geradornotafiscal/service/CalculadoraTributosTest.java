package br.com.itau.geradornotafiscal.service;

import br.com.itau.geradornotafiscal.model.Destinatario;
import br.com.itau.geradornotafiscal.model.Item;
import br.com.itau.geradornotafiscal.model.ItemNotaFiscal;
import br.com.itau.geradornotafiscal.model.Pedido;
import br.com.itau.geradornotafiscal.model.RegimeTributacaoPJ;
import br.com.itau.geradornotafiscal.model.TipoPessoa;
import br.com.itau.geradornotafiscal.service.strategy.LucroPresumidoAliquotaStrategy;
import br.com.itau.geradornotafiscal.service.strategy.LucroRealAliquotaStrategy;
import br.com.itau.geradornotafiscal.service.strategy.PessoaFisicaAliquotaStrategy;
import br.com.itau.geradornotafiscal.service.strategy.SimplesNacionalAliquotaStrategy;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CalculadoraTributosTest {

    private final CalculadoraTributos calculadora = new CalculadoraTributos(
            List.of(
                    new PessoaFisicaAliquotaStrategy(),
                    new SimplesNacionalAliquotaStrategy(),
                    new LucroRealAliquotaStrategy(),
                    new LucroPresumidoAliquotaStrategy()),
            new CalculadoraAliquotaProduto());

    @Test
    void deveSelecionarEAplicarARegraDoSimplesNacional() {
        Pedido pedido = pedido(TipoPessoa.JURIDICA, RegimeTributacaoPJ.SIMPLES_NACIONAL, 5_001);

        List<ItemNotaFiscal> itens = calculadora.calcular(pedido);

        assertEquals(950.19, itens.getFirst().getValorTributoItem(), 0.001);
    }

    @Test
    void deveRejeitarRegimeSemRegraTributaria() {
        Pedido pedido = pedido(TipoPessoa.JURIDICA, RegimeTributacaoPJ.OUTROS, 1_000);

        assertThrows(IllegalArgumentException.class, () -> calculadora.calcular(pedido));
    }

    private Pedido pedido(TipoPessoa tipoPessoa, RegimeTributacaoPJ regime, double valor) {
        Destinatario destinatario = Destinatario.builder()
                .tipoPessoa(tipoPessoa)
                .regimeTributacao(regime)
                .build();
        Item item = new Item("1", "Produto", valor, 1);
        return Pedido.builder()
                .valorTotalItens(valor)
                .destinatario(destinatario)
                .itens(List.of(item))
                .build();
    }
}
