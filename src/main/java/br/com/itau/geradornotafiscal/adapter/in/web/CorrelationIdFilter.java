package br.com.itau.geradornotafiscal.adapter.in.web;

import br.com.itau.geradornotafiscal.observability.CorrelationIdContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

@Component
public class CorrelationIdFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(CorrelationIdFilter.class);
    private static final Pattern VALOR_VALIDO = Pattern.compile("[A-Za-z0-9._-]{1,128}");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String correlationId = resolverCorrelationId(request.getHeader(CorrelationIdContext.HEADER));
        long inicio = System.nanoTime();
        MDC.put(CorrelationIdContext.MDC_KEY, correlationId);
        response.setHeader(CorrelationIdContext.HEADER, correlationId);
        LOGGER.info("request.started method={} path={} correlationId={}",
                request.getMethod(), request.getRequestURI(), correlationId);
        try {
            filterChain.doFilter(request, response);
        } finally {
            long durationMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - inicio);
            LOGGER.info("request.completed method={} path={} status={} durationMs={} correlationId={}",
                    request.getMethod(), request.getRequestURI(), response.getStatus(), durationMs, correlationId);
            MDC.remove(CorrelationIdContext.MDC_KEY);
        }
    }

    private String resolverCorrelationId(String recebido) {
        return recebido != null && VALOR_VALIDO.matcher(recebido).matches()
                ? recebido
                : UUID.randomUUID().toString();
    }
}
