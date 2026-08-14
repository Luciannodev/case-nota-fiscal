package br.com.itau.geradornotafiscal.usecase;

import br.com.itau.geradornotafiscal.core.model.Destinatario;
import br.com.itau.geradornotafiscal.core.model.Documento;
import br.com.itau.geradornotafiscal.core.model.Endereco;
import br.com.itau.geradornotafiscal.core.model.Finalidade;
import br.com.itau.geradornotafiscal.core.model.Item;
import br.com.itau.geradornotafiscal.core.model.Pedido;
import br.com.itau.geradornotafiscal.core.model.Regiao;
import br.com.itau.geradornotafiscal.core.model.TipoPessoa;
import br.com.itau.geradornotafiscal.core.model.TipoDocumento;
import br.com.itau.geradornotafiscal.config.TaxasProperties;
import br.com.itau.geradornotafiscal.port.out.PublicarIntegracoesNotaFiscalPort;
import br.com.itau.geradornotafiscal.observability.ContextoExecucao;
import br.com.itau.geradornotafiscal.usecase.calculo.CalculadoraAliquotaProduto;
import br.com.itau.geradornotafiscal.usecase.calculo.CalculadoraFrete;
import br.com.itau.geradornotafiscal.usecase.calculo.CalculadoraTributos;
import br.com.itau.geradornotafiscal.usecase.strategy.PessoaFisicaAliquotaStrategy;
import br.com.itau.geradornotafiscal.usecase.strategy.SudesteFreteStrategy;
import br.com.itau.geradornotafiscal.usecase.validation.PedidoValidator;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static br.com.itau.geradornotafiscal.support.TaxasTestFactory.faixas;
import static br.com.itau.geradornotafiscal.support.TaxasTestFactory.taxasPadrao;

class GerarNotaFiscalUseCaseHexagonalTest {

    private static final ContextoExecucao CONTEXTO = new ContextoExecucao("corr-hexagonal");

    @Test
    void devePublicarNotaGeradaPelaPortaDeSaida() {
        PublicarIntegracoesNotaFiscalPort publicador = mock(PublicarIntegracoesNotaFiscalPort.class);
        TaxasProperties taxas = taxasPadrao();
        GerarNotaFiscalUseCaseImpl useCase = new GerarNotaFiscalUseCaseImpl(
                new CalculadoraTributos(List.of(new PessoaFisicaAliquotaStrategy(faixas(taxas.getPf()))), new CalculadoraAliquotaProduto()),
                new CalculadoraFrete(List.of(new SudesteFreteStrategy(taxas.getFrete().getSudeste()))),
                publicador,
                new PedidoValidator());

        useCase.gerarNotaFiscal(pedido(), CONTEXTO);

        verify(publicador).publicar(any(), eq(CONTEXTO));
    }

    private Pedido pedido() {
        Destinatario destinatario = Destinatario.builder()
                .nome("Destinatário")
                .tipoPessoa(TipoPessoa.FISICA)
                .documentos(List.of(new Documento("12345678901", TipoDocumento.CPF)))
                .enderecos(List.of(Endereco.builder()
                        .finalidade(Finalidade.ENTREGA)
                        .regiao(Regiao.SUDESTE)
                        .build()))
                .build();
        return Pedido.builder()
                .idPedido(1)
                .data(LocalDate.of(2026, 8, 14))
                .valorTotalItens(new BigDecimal("100.00"))
                .valorFrete(new BigDecimal("10.00"))
                .itens(List.of(new Item("1", "Produto", new BigDecimal("100.00"), 1)))
                .destinatario(destinatario)
                .build();
    }
}
