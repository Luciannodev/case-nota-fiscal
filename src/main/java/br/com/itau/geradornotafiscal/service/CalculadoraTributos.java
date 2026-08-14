package br.com.itau.geradornotafiscal.service;

import br.com.itau.geradornotafiscal.model.ItemNotaFiscal;
import br.com.itau.geradornotafiscal.model.Pedido;
import br.com.itau.geradornotafiscal.service.strategy.AliquotaTributariaStrategy;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
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

        double aliquota = strategy.calcularAliquota(pedido.getValorTotalItens());
        return calculadoraAliquotaProduto.calcularAliquota(pedido.getItens(), aliquota);
    }
}
