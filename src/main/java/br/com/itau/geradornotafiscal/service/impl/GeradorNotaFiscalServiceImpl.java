package br.com.itau.geradornotafiscal.service.impl;

import br.com.itau.geradornotafiscal.model.*;
import br.com.itau.geradornotafiscal.service.CalculadoraFrete;
import br.com.itau.geradornotafiscal.service.CalculadoraTributos;
import br.com.itau.geradornotafiscal.port.in.GerarNotaFiscalUseCase;
import br.com.itau.geradornotafiscal.port.out.PublicarIntegracoesNotaFiscalPort;
import br.com.itau.geradornotafiscal.observability.ContextoExecucao;
import br.com.itau.geradornotafiscal.observability.CorrelationIdContext;
import br.com.itau.geradornotafiscal.observability.EtapaTemporizada;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class GeradorNotaFiscalServiceImpl implements GerarNotaFiscalUseCase {
	private static final Logger LOGGER = LoggerFactory.getLogger(GeradorNotaFiscalServiceImpl.class);

	private final CalculadoraTributos calculadoraTributos;
	private final CalculadoraFrete calculadoraFrete;
	private final PublicarIntegracoesNotaFiscalPort integracoesNotaFiscal;

	public GeradorNotaFiscalServiceImpl(CalculadoraTributos calculadoraTributos,
									 CalculadoraFrete calculadoraFrete,
									 PublicarIntegracoesNotaFiscalPort integracoesNotaFiscal) {
		this.calculadoraTributos = calculadoraTributos;
		this.calculadoraFrete = calculadoraFrete;
		this.integracoesNotaFiscal = integracoesNotaFiscal;
	}

	@Override
	public NotaFiscal gerarNotaFiscal(Pedido pedido, ContextoExecucao contexto) {
		return CorrelationIdContext.executar(contexto,
				() -> EtapaTemporizada.executar(LOGGER, "nota-fiscal.total", contexto, () -> {
			List<ItemNotaFiscal> itens = EtapaTemporizada.executar(
					LOGGER, "calculo.tributos", contexto, () -> calculadoraTributos.calcular(pedido));
			double totalItens = EtapaTemporizada.executar(
					LOGGER, "calculo.total-itens", contexto, () -> somarValorItemsComTributos(itens));
			double frete = EtapaTemporizada.executar(
					LOGGER, "calculo.frete", contexto, () -> calculadoraFrete.calcular(pedido));

			NotaFiscal notaFiscal = EtapaTemporizada.executar(
					LOGGER, "montagem.nota-fiscal", contexto, () -> NotaFiscal.builder()
							.idNotaFiscal(UUID.randomUUID().toString())
							.data(LocalDateTime.now())
							.valorTotalItens(totalItens)
							.valorFrete(frete)
							.itens(itens)
							.destinatario(pedido.getDestinatario())
							.build());

			EtapaTemporizada.executar(LOGGER, "publicacao.integracoes", contexto,
					() -> integracoesNotaFiscal.publicar(notaFiscal, contexto));
			return notaFiscal;
		}));
	}

	private double somarValorItemsComTributos(List<ItemNotaFiscal> itemNotaFiscalList) {
        return itemNotaFiscalList.stream().
                mapToDouble(itemNotaFiscal -> itemNotaFiscal.getQuantidade() * (itemNotaFiscal.getValorUnitario() + itemNotaFiscal.getValorTributoItem())).sum();
	}
}
