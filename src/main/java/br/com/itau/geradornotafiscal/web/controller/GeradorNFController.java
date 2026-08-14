package br.com.itau.geradornotafiscal.web.controller;

import br.com.itau.geradornotafiscal.model.NotaFiscal;
import br.com.itau.geradornotafiscal.model.Pedido;
import br.com.itau.geradornotafiscal.port.in.GerarNotaFiscalUseCase;
import org.springframework.http.HttpStatus;
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
		// Lógica de processamento do pedido
		// Aqui você pode realizar as operações desejadas com o objeto Pedido

		// Exemplo de retorno
		NotaFiscal notaFiscal = gerarNotaFiscalUseCase.gerarNotaFiscal(pedido);
		return new ResponseEntity<>(notaFiscal, HttpStatus.OK);
	}
	
}
