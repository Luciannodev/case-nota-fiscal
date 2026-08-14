package br.com.itau.geradornotafiscal.service;

import br.com.itau.geradornotafiscal.model.Endereco;
import br.com.itau.geradornotafiscal.model.Finalidade;
import br.com.itau.geradornotafiscal.model.Pedido;
import br.com.itau.geradornotafiscal.model.Regiao;
import br.com.itau.geradornotafiscal.service.strategy.FreteStrategy;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
public class CalculadoraFrete {

    private final Map<Regiao, FreteStrategy> strategies;

    public CalculadoraFrete(List<FreteStrategy> strategies) {
        this.strategies = new EnumMap<>(Regiao.class);
        strategies.forEach(strategy -> this.strategies.put(strategy.regiao(), strategy));
    }

    public double calcular(Pedido pedido) {
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
