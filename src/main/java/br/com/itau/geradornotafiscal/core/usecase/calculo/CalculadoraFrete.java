package br.com.itau.geradornotafiscal.core.usecase.calculo;

import br.com.itau.geradornotafiscal.core.model.Endereco;
import br.com.itau.geradornotafiscal.core.model.Finalidade;
import br.com.itau.geradornotafiscal.core.model.Pedido;
import br.com.itau.geradornotafiscal.core.model.Regiao;
import br.com.itau.geradornotafiscal.core.strategy.FreteStrategy;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.math.BigDecimal;

public class CalculadoraFrete {

    private final Map<Regiao, FreteStrategy> strategies;

    public CalculadoraFrete(List<FreteStrategy> strategies) {
        this.strategies = new EnumMap<>(Regiao.class);
        strategies.forEach(strategy -> this.strategies.put(strategy.regiao(), strategy));
    }

    public BigDecimal calcular(Pedido pedido) {
        Regiao regiaoEntrega = pedido.getDestinatario().getEnderecos().stream()
                .filter(this::enderecoDeEntrega)
                .map(Endereco::getRegiao)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Pedido sem endereço de entrega"));

        FreteStrategy strategy = strategies.get(regiaoEntrega);
        if (strategy == null) {
            throw new IllegalArgumentException("Não existe regra de frete para a região " + regiaoEntrega);
        }
        return strategy.calcular(pedido.getValorFrete());
    }

    private boolean enderecoDeEntrega(Endereco endereco) {
        return endereco.getFinalidade() == Finalidade.ENTREGA
                || endereco.getFinalidade() == Finalidade.COBRANCA_ENTREGA;
    }
}
