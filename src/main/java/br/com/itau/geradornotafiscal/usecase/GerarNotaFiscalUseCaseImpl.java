package br.com.itau.geradornotafiscal.usecase;

import br.com.itau.geradornotafiscal.core.model.*;
import br.com.itau.geradornotafiscal.usecase.calculo.CalculadoraFrete;
import br.com.itau.geradornotafiscal.usecase.calculo.CalculadoraTributos;
import br.com.itau.geradornotafiscal.port.out.PublicarIntegracoesNotaFiscalPort;
import br.com.itau.geradornotafiscal.observability.ContextoExecucao;
import br.com.itau.geradornotafiscal.observability.CorrelationIdContext;
import br.com.itau.geradornotafiscal.observability.EtapaTemporizada;
import br.com.itau.geradornotafiscal.observability.EtapaFluxo;
import br.com.itau.geradornotafiscal.usecase.validation.PedidoValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

public class GerarNotaFiscalUseCaseImpl implements GerarNotaFiscalUseCase {
	private static final Logger LOGGER = LoggerFactory.getLogger(GerarNotaFiscalUseCaseImpl.class);

	private final CalculadoraTributos calculadoraTributos;
	private final CalculadoraFrete calculadoraFrete;
	private final PublicarIntegracoesNotaFiscalPort integracoesNotaFiscal;
	private final PedidoValidator pedidoValidator;

	public GerarNotaFiscalUseCaseImpl(CalculadoraTributos calculadoraTributos,
									 CalculadoraFrete calculadoraFrete,
									 PublicarIntegracoesNotaFiscalPort integracoesNotaFiscal,
									 PedidoValidator pedidoValidator) {
		this.calculadoraTributos = calculadoraTributos;
		this.calculadoraFrete = calculadoraFrete;
		this.integracoesNotaFiscal = integracoesNotaFiscal;
		this.pedidoValidator = pedidoValidator;
	}

	@Override
	public NotaFiscal gerarNotaFiscal(Pedido pedido, ContextoExecucao contexto) {
		return CorrelationIdContext.executar(contexto,
				() -> EtapaTemporizada.executar(LOGGER, EtapaFluxo.GERACAO_NOTA_FISCAL, contexto, () -> {
			EtapaTemporizada.executar(LOGGER, EtapaFluxo.VALIDACAO_PEDIDO, contexto,
					() -> pedidoValidator.validar(pedido));
			List<ItemNotaFiscal> itens = EtapaTemporizada.executar(
					LOGGER, EtapaFluxo.CALCULO_TRIBUTOS, contexto, () -> calculadoraTributos.calcular(pedido));
			BigDecimal totalItens = EtapaTemporizada.executar(
					LOGGER, EtapaFluxo.SOMA_TOTAL_ITENS, contexto, () -> somarValorItemsComTributos(itens));
			BigDecimal frete = EtapaTemporizada.executar(
					LOGGER, EtapaFluxo.CALCULO_FRETE, contexto, () -> calculadoraFrete.calcular(pedido));

			NotaFiscal notaFiscal = EtapaTemporizada.executar(
					LOGGER, EtapaFluxo.MONTAGEM_NOTA_FISCAL, contexto, () -> NotaFiscal.builder()
							.idNotaFiscal(UUID.randomUUID().toString())
							.data(LocalDateTime.now())
							.valorTotalItens(totalItens)
							.valorFrete(frete)
							.itens(itens)
							.destinatario(pedido.getDestinatario())
							.build());

			EtapaTemporizada.executar(LOGGER, EtapaFluxo.PUBLICACAO_INTEGRACOES, contexto,
					() -> integracoesNotaFiscal.publicar(notaFiscal, contexto));
			return notaFiscal;
		}));
	}

	private BigDecimal somarValorItemsComTributos(List<ItemNotaFiscal> itemNotaFiscalList) {
        return itemNotaFiscalList.stream()
                .map(item -> item.getValorUnitario()
                        .add(item.getValorTributoItem())
                        .multiply(BigDecimal.valueOf(item.getQuantidade())))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
	}
}
