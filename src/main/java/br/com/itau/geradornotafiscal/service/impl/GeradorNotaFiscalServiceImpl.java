package br.com.itau.geradornotafiscal.service.impl;

import br.com.itau.geradornotafiscal.model.*;
import br.com.itau.geradornotafiscal.service.CalculadoraFrete;
import br.com.itau.geradornotafiscal.service.CalculadoraTributos;
import br.com.itau.geradornotafiscal.port.in.GerarNotaFiscalUseCase;
import br.com.itau.geradornotafiscal.port.out.EntregaIntegrationPort;
import br.com.itau.geradornotafiscal.port.out.EstoqueIntegrationPort;
import br.com.itau.geradornotafiscal.port.out.FinanceiroIntegrationPort;
import br.com.itau.geradornotafiscal.port.out.RegistroIntegrationPort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class GeradorNotaFiscalServiceImpl implements GerarNotaFiscalUseCase {

	private final CalculadoraTributos calculadoraTributos;
	private final CalculadoraFrete calculadoraFrete;
	private final EstoqueIntegrationPort estoqueIntegrationPort;
	private final RegistroIntegrationPort registroIntegrationPort;
	private final EntregaIntegrationPort entregaIntegrationPort;
	private final FinanceiroIntegrationPort financeiroIntegrationPort;

	public GeradorNotaFiscalServiceImpl(CalculadoraTributos calculadoraTributos,
									 CalculadoraFrete calculadoraFrete,
									 EstoqueIntegrationPort estoqueIntegrationPort,
									 RegistroIntegrationPort registroIntegrationPort,
									 EntregaIntegrationPort entregaIntegrationPort,
									 FinanceiroIntegrationPort financeiroIntegrationPort) {
		this.calculadoraTributos = calculadoraTributos;
		this.calculadoraFrete = calculadoraFrete;
		this.estoqueIntegrationPort = estoqueIntegrationPort;
		this.registroIntegrationPort = registroIntegrationPort;
		this.entregaIntegrationPort = entregaIntegrationPort;
		this.financeiroIntegrationPort = financeiroIntegrationPort;
	}

	@Override
	public NotaFiscal gerarNotaFiscal(Pedido pedido) {

		Destinatario destinatario = pedido.getDestinatario();
		List<ItemNotaFiscal> itemNotaFiscalList = calculadoraTributos.calcular(pedido);
		// soma valores items com tributos
		double valorTotalItensComTributos = somarValorItemsComTributos(itemNotaFiscalList);

		//Regras diferentes para frete
		double valorFreteComPercentual = calculadoraFrete.calcular(pedido);

		// Create the NotaFiscal object
		String idNotaFiscal = UUID.randomUUID().toString();

		NotaFiscal notaFiscal = NotaFiscal.builder()
				.idNotaFiscal(idNotaFiscal)
				.data(LocalDateTime.now())
				.valorTotalItens(valorTotalItensComTributos)
				.valorFrete(valorFreteComPercentual)
				.itens(itemNotaFiscalList)
				.destinatario(pedido.getDestinatario())
				.build();

		estoqueIntegrationPort.baixarEstoque(notaFiscal);
		registroIntegrationPort.registrar(notaFiscal);
		entregaIntegrationPort.agendarEntrega(notaFiscal);
		financeiroIntegrationPort.enviarContasAReceber(notaFiscal);

		return notaFiscal;
	}

	private double somarValorItemsComTributos(List<ItemNotaFiscal> itemNotaFiscalList) {
        return itemNotaFiscalList.stream().
                mapToDouble(itemNotaFiscal -> itemNotaFiscal.getQuantidade() * (itemNotaFiscal.getValorUnitario() + itemNotaFiscal.getValorTributoItem())).sum();
	}
}
