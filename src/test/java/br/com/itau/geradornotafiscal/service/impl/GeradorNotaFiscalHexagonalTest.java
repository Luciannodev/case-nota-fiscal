package br.com.itau.geradornotafiscal.service.impl;

import br.com.itau.geradornotafiscal.model.Destinatario;
import br.com.itau.geradornotafiscal.model.Endereco;
import br.com.itau.geradornotafiscal.model.Finalidade;
import br.com.itau.geradornotafiscal.model.Item;
import br.com.itau.geradornotafiscal.model.Pedido;
import br.com.itau.geradornotafiscal.model.Regiao;
import br.com.itau.geradornotafiscal.model.TipoPessoa;
import br.com.itau.geradornotafiscal.port.out.EntregaIntegrationPort;
import br.com.itau.geradornotafiscal.port.out.EstoqueIntegrationPort;
import br.com.itau.geradornotafiscal.port.out.FinanceiroIntegrationPort;
import br.com.itau.geradornotafiscal.port.out.RegistroIntegrationPort;
import br.com.itau.geradornotafiscal.service.CalculadoraAliquotaProduto;
import br.com.itau.geradornotafiscal.service.CalculadoraFrete;
import br.com.itau.geradornotafiscal.service.CalculadoraTributos;
import br.com.itau.geradornotafiscal.service.strategy.PessoaFisicaAliquotaStrategy;
import br.com.itau.geradornotafiscal.service.strategy.SudesteFreteStrategy;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class GeradorNotaFiscalHexagonalTest {

    @Test
    void deveAcionarTodasAsPortasDeSaidaAoGerarNota() {
        EstoqueIntegrationPort estoque = mock(EstoqueIntegrationPort.class);
        RegistroIntegrationPort registro = mock(RegistroIntegrationPort.class);
        EntregaIntegrationPort entrega = mock(EntregaIntegrationPort.class);
        FinanceiroIntegrationPort financeiro = mock(FinanceiroIntegrationPort.class);
        GeradorNotaFiscalServiceImpl service = new GeradorNotaFiscalServiceImpl(
                new CalculadoraTributos(List.of(new PessoaFisicaAliquotaStrategy()), new CalculadoraAliquotaProduto()),
                new CalculadoraFrete(List.of(new SudesteFreteStrategy())),
                estoque,
                registro,
                entrega,
                financeiro);

        service.gerarNotaFiscal(pedido());

        verify(estoque).baixarEstoque(any());
        verify(registro).registrar(any());
        verify(entrega).agendarEntrega(any());
        verify(financeiro).enviarContasAReceber(any());
    }

    private Pedido pedido() {
        Destinatario destinatario = Destinatario.builder()
                .tipoPessoa(TipoPessoa.FISICA)
                .enderecos(List.of(Endereco.builder()
                        .finalidade(Finalidade.ENTREGA)
                        .regiao(Regiao.SUDESTE)
                        .build()))
                .build();
        return Pedido.builder()
                .valorTotalItens(100)
                .valorFrete(10)
                .itens(List.of(new Item("1", "Produto", 100, 1)))
                .destinatario(destinatario)
                .build();
    }
}
