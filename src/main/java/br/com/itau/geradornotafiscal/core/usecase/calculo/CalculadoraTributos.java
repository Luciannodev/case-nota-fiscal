package br.com.itau.geradornotafiscal.core.usecase.calculo;

import br.com.itau.geradornotafiscal.core.model.ItemNotaFiscal;
import br.com.itau.geradornotafiscal.core.model.Pedido;
import br.com.itau.geradornotafiscal.core.strategy.AliquotaTributariaStrategy;

import java.util.List;
import java.math.BigDecimal;

public class CalculadoraTributos {

    private final List<AliquotaTributariaStrategy> strategies;
    private final CalculadoraAliquotaProduto calculadoraAliquotaProduto;

    public CalculadoraTributos(List<AliquotaTributariaStrategy> strategies,
                              CalculadoraAliquotaProduto calculadoraAliquotaProduto) {
        this.strategies = List.copyOf(strategies);
        this.calculadoraAliquotaProduto = calculadoraAliquotaProduto;
    }

    public List<ItemNotaFiscal> calcular(Pedido pedido) {
        AliquotaTributariaStrategy strategy = strategies.stream()
                .filter(candidata -> candidata.suporta(pedido.getDestinatario()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Não existe regra tributária para o destinatário informado"));

        BigDecimal aliquota = strategy.calcularAliquota(pedido.getValorTotalItens());
        return calculadoraAliquotaProduto.calcularAliquota(pedido.getItens(), aliquota);
    }
}
