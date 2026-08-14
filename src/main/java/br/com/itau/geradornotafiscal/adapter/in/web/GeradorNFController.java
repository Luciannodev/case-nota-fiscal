package br.com.itau.geradornotafiscal.adapter.in.web;

import br.com.itau.geradornotafiscal.core.model.NotaFiscal;
import br.com.itau.geradornotafiscal.core.model.Pedido;
import br.com.itau.geradornotafiscal.usecase.GerarNotaFiscalUseCase;
import br.com.itau.geradornotafiscal.observability.CorrelationIdContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pedido")
public class GeradorNFController {

	private final GerarNotaFiscalUseCase gerarNotaFiscalUseCase;

	public GeradorNFController(GerarNotaFiscalUseCase gerarNotaFiscalUseCase) {
		this.gerarNotaFiscalUseCase = gerarNotaFiscalUseCase;
	}

	@PostMapping("/gerarNotaFiscal")
	public ResponseEntity<NotaFiscal> gerarNotaFiscal(@RequestBody Pedido pedido) {
		NotaFiscal notaFiscal = gerarNotaFiscalUseCase.gerarNotaFiscal(pedido, CorrelationIdContext.atual());
		return ResponseEntity.ok(notaFiscal);
	}
	
}
