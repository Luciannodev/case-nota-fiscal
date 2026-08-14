package br.com.itau.geradornotafiscal.service.impl;

import br.com.itau.geradornotafiscal.model.*;
import br.com.itau.geradornotafiscal.service.CalculadoraFrete;
import br.com.itau.geradornotafiscal.service.CalculadoraTributos;
import br.com.itau.geradornotafiscal.service.GeradorNotaFiscalService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class GeradorNotaFiscalServiceImpl implements GeradorNotaFiscalService{

	private final CalculadoraTributos calculadoraTributos;
	private final CalculadoraFrete calculadoraFrete;

	public GeradorNotaFiscalServiceImpl(CalculadoraTributos calculadoraTributos, CalculadoraFrete calculadoraFrete) {
		this.calculadoraTributos = calculadoraTributos;
		this.calculadoraFrete = calculadoraFrete;
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

		new EstoqueService().enviarNotaFiscalParaBaixaEstoque(notaFiscal);
		new RegistroService().registrarNotaFiscal(notaFiscal);
		new EntregaService().agendarEntrega(notaFiscal);
		new FinanceiroService().enviarNotaFiscalParaContasReceber(notaFiscal);

		return notaFiscal;
	}

	private double somarValorItemsComTributos(List<ItemNotaFiscal> itemNotaFiscalList) {
        return itemNotaFiscalList.stream().
                mapToDouble(itemNotaFiscal -> itemNotaFiscal.getQuantidade() * (itemNotaFiscal.getValorUnitario() + itemNotaFiscal.getValorTributoItem())).sum();
	}
}
