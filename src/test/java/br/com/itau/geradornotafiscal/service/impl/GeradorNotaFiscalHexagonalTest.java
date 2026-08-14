package br.com.itau.geradornotafiscal.service.impl;

import br.com.itau.geradornotafiscal.model.Destinatario;
import br.com.itau.geradornotafiscal.model.Endereco;
import br.com.itau.geradornotafiscal.model.Finalidade;
import br.com.itau.geradornotafiscal.model.Item;
import br.com.itau.geradornotafiscal.model.Pedido;
import br.com.itau.geradornotafiscal.model.Regiao;
import br.com.itau.geradornotafiscal.model.TipoPessoa;
import br.com.itau.geradornotafiscal.config.TaxasProperties;
import br.com.itau.geradornotafiscal.port.out.PublicarIntegracoesNotaFiscalPort;
import br.com.itau.geradornotafiscal.observability.ContextoExecucao;
import br.com.itau.geradornotafiscal.service.CalculadoraAliquotaProduto;
import br.com.itau.geradornotafiscal.service.CalculadoraFrete;
import br.com.itau.geradornotafiscal.service.CalculadoraTributos;
import br.com.itau.geradornotafiscal.service.strategy.PessoaFisicaAliquotaStrategy;
import br.com.itau.geradornotafiscal.service.strategy.SudesteFreteStrategy;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static br.com.itau.geradornotafiscal.support.TaxasTestFactory.taxasPadrao;

class GeradorNotaFiscalHexagonalTest {

    private static final ContextoExecucao CONTEXTO = new ContextoExecucao("corr-hexagonal");

    @Test
    void devePublicarNotaGeradaPelaPortaDeSaida() {
        PublicarIntegracoesNotaFiscalPort publicador = mock(PublicarIntegracoesNotaFiscalPort.class);
        TaxasProperties taxas = taxasPadrao();
        GeradorNotaFiscalServiceImpl service = new GeradorNotaFiscalServiceImpl(
                new CalculadoraTributos(List.of(new PessoaFisicaAliquotaStrategy(taxas)), new CalculadoraAliquotaProduto()),
                new CalculadoraFrete(List.of(new SudesteFreteStrategy(taxas))),
                publicador);

        service.gerarNotaFiscal(pedido(), CONTEXTO);

        verify(publicador).publicar(any(), eq(CONTEXTO));
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
                .valorTotalItens(new BigDecimal("100.00"))
                .valorFrete(new BigDecimal("10.00"))
                .itens(List.of(new Item("1", "Produto", new BigDecimal("100.00"), 1)))
                .destinatario(destinatario)
                .build();
    }
}
