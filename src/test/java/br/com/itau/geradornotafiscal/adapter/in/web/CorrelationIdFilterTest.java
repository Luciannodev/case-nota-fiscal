package br.com.itau.geradornotafiscal.adapter.in.web;

import br.com.itau.geradornotafiscal.observability.CorrelationIdContext;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class CorrelationIdFilterTest {

    private final CorrelationIdFilter filter = new CorrelationIdFilter();

    @Test
    void devePropagarCorrelationIdRecebidoDuranteTodoORequest() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/pedido/gerarNotaFiscal");
        request.addHeader(CorrelationIdContext.HEADER, "pedido-123");
        MockHttpServletResponse response = new MockHttpServletResponse();
        AtomicReference<String> idObservadoNoFluxo = new AtomicReference<>();

        filter.doFilter(request, response,
                (req, res) -> idObservadoNoFluxo.set(MDC.get(CorrelationIdContext.MDC_KEY)));

        assertEquals("pedido-123", idObservadoNoFluxo.get());
        assertEquals("pedido-123", response.getHeader(CorrelationIdContext.HEADER));
        assertNull(MDC.get(CorrelationIdContext.MDC_KEY));
    }

    @Test
    void deveGerarCorrelationIdQuandoOClienteNaoInformar() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/pedido/gerarNotaFiscal");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, (req, res) -> { });

        assertNotNull(response.getHeader(CorrelationIdContext.HEADER));
    }
}
