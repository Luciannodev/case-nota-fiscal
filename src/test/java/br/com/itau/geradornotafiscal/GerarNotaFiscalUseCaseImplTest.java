package br.com.itau.geradornotafiscal;

import br.com.itau.geradornotafiscal.core.model.Destinatario;
import br.com.itau.geradornotafiscal.core.model.Documento;
import br.com.itau.geradornotafiscal.core.model.Endereco;
import br.com.itau.geradornotafiscal.core.model.Finalidade;
import br.com.itau.geradornotafiscal.core.model.Item;
import br.com.itau.geradornotafiscal.core.model.NotaFiscal;
import br.com.itau.geradornotafiscal.core.model.Pedido;
import br.com.itau.geradornotafiscal.core.model.Regiao;
import br.com.itau.geradornotafiscal.core.model.RegimeTributacaoPJ;
import br.com.itau.geradornotafiscal.core.model.TipoPessoa;
import br.com.itau.geradornotafiscal.core.model.TipoDocumento;
import br.com.itau.geradornotafiscal.usecase.validation.PedidoValidator;
import br.com.itau.geradornotafiscal.usecase.calculo.CalculadoraAliquotaProduto;
import br.com.itau.geradornotafiscal.usecase.calculo.CalculadoraFrete;
import br.com.itau.geradornotafiscal.usecase.calculo.CalculadoraTributos;
import br.com.itau.geradornotafiscal.usecase.GerarNotaFiscalUseCaseImpl;
import br.com.itau.geradornotafiscal.observability.ContextoExecucao;
import br.com.itau.geradornotafiscal.config.TaxasProperties;
import br.com.itau.geradornotafiscal.usecase.strategy.CentroOesteFreteStrategy;
import br.com.itau.geradornotafiscal.usecase.strategy.LucroPresumidoAliquotaStrategy;
import br.com.itau.geradornotafiscal.usecase.strategy.LucroRealAliquotaStrategy;
import br.com.itau.geradornotafiscal.usecase.strategy.NordesteFreteStrategy;
import br.com.itau.geradornotafiscal.usecase.strategy.NorteFreteStrategy;
import br.com.itau.geradornotafiscal.usecase.strategy.PessoaFisicaAliquotaStrategy;
import br.com.itau.geradornotafiscal.usecase.strategy.SimplesNacionalAliquotaStrategy;
import br.com.itau.geradornotafiscal.usecase.strategy.SudesteFreteStrategy;
import br.com.itau.geradornotafiscal.usecase.strategy.SulFreteStrategy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.time.LocalDate;
import java.util.stream.Stream;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.assertj.core.api.Assertions.assertThat;
import static br.com.itau.geradornotafiscal.support.TaxasTestFactory.faixas;
import static br.com.itau.geradornotafiscal.support.TaxasTestFactory.taxasPadrao;

class GerarNotaFiscalUseCaseImplTest {

    private static final ContextoExecucao CONTEXTO = new ContextoExecucao("teste-correlation-id");

    private final GerarNotaFiscalUseCaseImpl geradorNotaFiscalService = criarGeradorNotaFiscalService();

    @ParameterizedTest(name = "pessoa fisica: pedido de {0} gera tributo {1} e total {2}")
    @MethodSource("cenariosPessoaFisica")
    void deveCalcularValoresEsperadosParaCadaFaixaDePessoaFisica(
            double valorTotalItens, double tributoEsperado, double totalNotaEsperado) {
        Pedido pedido = pedido(TipoPessoa.FISICA, null, valorTotalItens, List.of(item(valorTotalItens, 1)), Regiao.SUDESTE);

        NotaFiscal notaFiscal = geradorNotaFiscalService.gerarNotaFiscal(pedido, CONTEXTO);

        assertThat(notaFiscal.getItens().getFirst().getValorTributoItem())
                .isEqualByComparingTo(BigDecimal.valueOf(tributoEsperado));
        assertThat(notaFiscal.getValorTotalItens()).isEqualByComparingTo(BigDecimal.valueOf(totalNotaEsperado));
    }

    @ParameterizedTest(name = "{0}: pedido de {1} gera tributo {2} e total {3}")
    @MethodSource("cenariosPessoaJuridica")
    void deveCalcularValoresEsperadosParaCadaFaixaERegimeDePessoaJuridica(
            RegimeTributacaoPJ regime, double valorTotalItens, double tributoEsperado, double totalNotaEsperado) {
        Pedido pedido = pedido(TipoPessoa.JURIDICA, regime, valorTotalItens, List.of(item(valorTotalItens, 1)), Regiao.SUDESTE);

        NotaFiscal notaFiscal = geradorNotaFiscalService.gerarNotaFiscal(pedido, CONTEXTO);

        assertThat(notaFiscal.getItens().getFirst().getValorTributoItem())
                .isEqualByComparingTo(BigDecimal.valueOf(tributoEsperado));
        assertThat(notaFiscal.getValorTotalItens()).isEqualByComparingTo(BigDecimal.valueOf(totalNotaEsperado));
    }

    @Test
    void deveSomarValorDosProdutosComTributosConsiderandoAQuantidadeDeCadaItem() {
        Pedido pedido = pedido(
                TipoPessoa.FISICA,
                null,
                700,
                List.of(item(100, 3), item(200, 2)),
                Regiao.SUDESTE);

        NotaFiscal notaFiscal = geradorNotaFiscalService.gerarNotaFiscal(pedido, CONTEXTO);

        // (3 x (100 + 12)) + (2 x (200 + 24))
        assertEquals(new BigDecimal("784.00"), notaFiscal.getValorTotalItens());
        assertEquals(new BigDecimal("12.00"), notaFiscal.getItens().get(0).getValorTributoItem());
        assertEquals(new BigDecimal("24.00"), notaFiscal.getItens().get(1).getValorTributoItem());
    }

    @ParameterizedTest(name = "frete base de 100 para {0} resulta em {1}")
    @MethodSource("cenariosFrete")
    void deveCalcularValorFinalEsperadoDoFreteParaCadaRegiao(Regiao regiao, double valorFreteEsperado) {
        Pedido pedido = pedido(TipoPessoa.FISICA, null, 100, List.of(item(100, 1)), regiao);
        pedido.setValorFrete(new BigDecimal("100.00"));

        NotaFiscal notaFiscal = geradorNotaFiscalService.gerarNotaFiscal(pedido, CONTEXTO);

        assertThat(notaFiscal.getValorFrete()).isEqualByComparingTo(BigDecimal.valueOf(valorFreteEsperado));
    }

    private static Stream<Arguments> cenariosPessoaFisica() {
        return Stream.of(
                Arguments.of(400d, 0d, 400d),
                Arguments.of(500d, 60d, 560d),
                Arguments.of(2_000d, 240d, 2_240d),
                Arguments.of(2_001d, 300.15d, 2_301.15d),
                Arguments.of(3_500d, 525d, 4_025d),
                Arguments.of(3_501d, 595.17d, 4_096.17d));
    }

    private static Stream<Arguments> cenariosPessoaJuridica() {
        return Stream.of(
                Arguments.of(RegimeTributacaoPJ.SIMPLES_NACIONAL, 999d, 29.97d, 1_028.97d),
                Arguments.of(RegimeTributacaoPJ.SIMPLES_NACIONAL, 1_000d, 70d, 1_070d),
                Arguments.of(RegimeTributacaoPJ.SIMPLES_NACIONAL, 2_000d, 140d, 2_140d),
                Arguments.of(RegimeTributacaoPJ.SIMPLES_NACIONAL, 2_001d, 260.13d, 2_261.13d),
                Arguments.of(RegimeTributacaoPJ.SIMPLES_NACIONAL, 5_000d, 650d, 5_650d),
                Arguments.of(RegimeTributacaoPJ.SIMPLES_NACIONAL, 5_001d, 950.19d, 5_951.19d),
                Arguments.of(RegimeTributacaoPJ.LUCRO_REAL, 999d, 29.97d, 1_028.97d),
                Arguments.of(RegimeTributacaoPJ.LUCRO_REAL, 1_000d, 90d, 1_090d),
                Arguments.of(RegimeTributacaoPJ.LUCRO_REAL, 2_000d, 180d, 2_180d),
                Arguments.of(RegimeTributacaoPJ.LUCRO_REAL, 2_001d, 300.15d, 2_301.15d),
                Arguments.of(RegimeTributacaoPJ.LUCRO_REAL, 5_000d, 750d, 5_750d),
                Arguments.of(RegimeTributacaoPJ.LUCRO_REAL, 5_001d, 1_000.20d, 6_001.20d),
                Arguments.of(RegimeTributacaoPJ.LUCRO_PRESUMIDO, 999d, 29.97d, 1_028.97d),
                Arguments.of(RegimeTributacaoPJ.LUCRO_PRESUMIDO, 1_000d, 90d, 1_090d),
                Arguments.of(RegimeTributacaoPJ.LUCRO_PRESUMIDO, 2_000d, 180d, 2_180d),
                Arguments.of(RegimeTributacaoPJ.LUCRO_PRESUMIDO, 2_001d, 320.16d, 2_321.16d),
                Arguments.of(RegimeTributacaoPJ.LUCRO_PRESUMIDO, 5_000d, 800d, 5_800d),
                Arguments.of(RegimeTributacaoPJ.LUCRO_PRESUMIDO, 5_001d, 1_000.20d, 6_001.20d));
    }

    private static Stream<Arguments> cenariosFrete() {
        return Stream.of(
                Arguments.of(Regiao.NORTE, 108d),
                Arguments.of(Regiao.NORDESTE, 108.50d),
                Arguments.of(Regiao.CENTRO_OESTE, 107d),
                Arguments.of(Regiao.SUDESTE, 104.80d),
                Arguments.of(Regiao.SUL, 106d));
    }

    private static Pedido pedido(TipoPessoa tipoPessoa, RegimeTributacaoPJ regime, double valorTotalItens,
                                 List<Item> itens, Regiao regiao) {
        Destinatario destinatario = new Destinatario();
        destinatario.setNome("Destinatário de teste");
        destinatario.setTipoPessoa(tipoPessoa);
        destinatario.setRegimeTributacao(regime);
        destinatario.setDocumentos(List.of(new Documento(
                tipoPessoa == TipoPessoa.FISICA ? "12345678901" : "12345678000199",
                tipoPessoa == TipoPessoa.FISICA ? TipoDocumento.CPF : TipoDocumento.CNPJ)));
        destinatario.setEnderecos(List.of(Endereco.builder()
                .finalidade(Finalidade.ENTREGA)
                .regiao(regiao)
                .build()));

        Pedido pedido = new Pedido();
        pedido.setIdPedido(1);
        pedido.setData(LocalDate.of(2026, 8, 14));
        pedido.setValorTotalItens(BigDecimal.valueOf(valorTotalItens));
        pedido.setValorFrete(BigDecimal.ZERO);
        pedido.setItens(itens);
        pedido.setDestinatario(destinatario);
        return pedido;
    }

    private static Item item(double valorUnitario, int quantidade) {
        Item item = new Item();
        item.setIdItem("item-1");
        item.setDescricao("Produto de teste");
        item.setValorUnitario(BigDecimal.valueOf(valorUnitario));
        item.setQuantidade(quantidade);
        return item;
    }

    private static GerarNotaFiscalUseCaseImpl criarGeradorNotaFiscalService() {
        TaxasProperties taxas = taxasPadrao();
        CalculadoraTributos calculadoraTributos = new CalculadoraTributos(
                List.of(
                        new PessoaFisicaAliquotaStrategy(faixas(taxas.getPf())),
                        new SimplesNacionalAliquotaStrategy(faixas(taxas.getSimplesNacional())),
                        new LucroRealAliquotaStrategy(faixas(taxas.getLucroReal())),
                        new LucroPresumidoAliquotaStrategy(faixas(taxas.getLucroPresumido()))),
                new CalculadoraAliquotaProduto());
        CalculadoraFrete calculadoraFrete = new CalculadoraFrete(List.of(
                new NorteFreteStrategy(taxas.getFrete().getNorte()),
                new NordesteFreteStrategy(taxas.getFrete().getNordeste()),
                new CentroOesteFreteStrategy(taxas.getFrete().getCentroOeste()),
                new SudesteFreteStrategy(taxas.getFrete().getSudeste()),
                new SulFreteStrategy(taxas.getFrete().getSul())));
        return new GerarNotaFiscalUseCaseImpl(
                calculadoraTributos,
                calculadoraFrete,
                (notaFiscal, contexto) -> { },
                new PedidoValidator());
    }
}
