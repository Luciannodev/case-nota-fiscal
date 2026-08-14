package br.com.itau.geradornotafiscal.observability;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EtapaTemporizadaTest {

    private final Logger logger = (Logger) LoggerFactory.getLogger(EtapaTemporizadaTest.class);
    private final ListAppender<ILoggingEvent> appender = new ListAppender<>();
    private final ContextoExecucao contexto = new ContextoExecucao("pedido-observavel-123");

    @BeforeEach
    void configurarCaptura() {
        appender.start();
        logger.addAppender(appender);
    }

    @AfterEach
    void removerCaptura() {
        logger.detachAppender(appender);
        appender.stop();
    }

    @Test
    void deveNarrarInicioEFimDaEtapaMantendoCamposEstruturados() {
        String resultado = EtapaTemporizada.executar(
                logger, EtapaFluxo.CALCULO_TRIBUTOS, contexto, () -> "calculado");

        List<String> mensagens = mensagens();
        assertEquals("calculado", resultado);
        assertTrue(mensagens.get(0).contains("Iniciando etapa: Calculando os tributos de cada item"));
        assertTrue(mensagens.get(0).contains("event=step.started step=calculo.tributos"));
        assertTrue(mensagens.get(1).contains("Etapa concluída: Calculando os tributos de cada item"));
        assertTrue(mensagens.get(1).contains("status=success durationMs="));
        assertTrue(mensagens.get(1).contains("correlationId=pedido-observavel-123"));
    }

    @Test
    void deveDescreverAFalhaComDuracaoECorrelationId() {
        assertThrows(IllegalStateException.class, () -> EtapaTemporizada.executar(
                logger,
                EtapaFluxo.CALCULO_FRETE,
                contexto,
                () -> { throw new IllegalStateException("região inválida"); }));

        String falha = mensagens().get(1);
        assertTrue(falha.contains("Etapa falhou: Calculando o frete conforme a região de entrega"));
        assertTrue(falha.contains("event=step.completed step=calculo.frete status=error durationMs="));
        assertTrue(falha.contains("correlationId=pedido-observavel-123"));
    }

    private List<String> mensagens() {
        return appender.list.stream().map(ILoggingEvent::getFormattedMessage).toList();
    }
}
