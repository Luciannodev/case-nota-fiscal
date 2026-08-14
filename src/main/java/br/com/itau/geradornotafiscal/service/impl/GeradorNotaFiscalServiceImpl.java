package br.com.itau.geradornotafiscal.service.impl;

import br.com.itau.geradornotafiscal.model.*;
import br.com.itau.geradornotafiscal.service.CalculadoraFrete;
import br.com.itau.geradornotafiscal.service.CalculadoraTributos;
import br.com.itau.geradornotafiscal.port.in.GerarNotaFiscalUseCase;
import br.com.itau.geradornotafiscal.port.out.PublicarIntegracoesNotaFiscalPort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class GeradorNotaFiscalServiceImpl implements GerarNotaFiscalUseCase {

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

		integracoesNotaFiscal.publicar(notaFiscal);

		return notaFiscal;
	}

	private double somarValorItemsComTributos(List<ItemNotaFiscal> itemNotaFiscalList) {
        return itemNotaFiscalList.stream().
                mapToDouble(itemNotaFiscal -> itemNotaFiscal.getQuantidade() * (itemNotaFiscal.getValorUnitario() + itemNotaFiscal.getValorTributoItem())).sum();
	}
}
