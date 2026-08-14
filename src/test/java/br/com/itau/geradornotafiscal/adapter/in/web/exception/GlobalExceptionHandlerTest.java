package br.com.itau.geradornotafiscal.adapter.in.web.exception;

import br.com.itau.geradornotafiscal.observability.CorrelationIdContext;
import br.com.itau.geradornotafiscal.usecase.exception.PedidoInvalidoException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mock.http.MockHttpInputMessage;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @AfterEach
    void limparContexto() {
        MDC.clear();
    }

    @Test
    void deveTransformarErroDeNegocioEmRespostaBadRequestRastreavel() {
        MDC.put(CorrelationIdContext.MDC_KEY, "corr-erro-123");
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/pedido/gerarNotaFiscal");

        var response = handler.tratarErroDeNegocio(
                new PedidoInvalidoException("itens[0].quantidade", "A quantidade deve ser maior que zero"),
                request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().codigo()).isEqualTo("PEDIDO_INVALIDO");
        assertThat(response.getBody().campo()).isEqualTo("itens[0].quantidade");
        assertThat(response.getBody().correlationId()).isEqualTo("corr-erro-123");
        assertThat(response.getBody().path()).isEqualTo("/api/pedido/gerarNotaFiscal");
    }

    @Test
    void deveResponderBadRequestParaJsonOuEnumInvalido() {
        MDC.put(CorrelationIdContext.MDC_KEY, "corr-json-123");
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/pedido/gerarNotaFiscal");
        HttpMessageNotReadableException exception = new HttpMessageNotReadableException(
                "tipo_pessoa inválido", new MockHttpInputMessage(new byte[0]));

        var response = handler.tratarEntradaMalformada(exception, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().codigo()).isEqualTo("ENTRADA_MALFORMADA");
        assertThat(response.getBody().correlationId()).isEqualTo("corr-json-123");
    }
}
