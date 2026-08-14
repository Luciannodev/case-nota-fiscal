package br.com.itau.geradornotafiscal.adapter.out.integration;

import br.com.itau.geradornotafiscal.core.model.ItemNotaFiscal;
import br.com.itau.geradornotafiscal.core.model.NotaFiscal;
import br.com.itau.geradornotafiscal.observability.ContextoExecucao;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTimeout;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IntegracoesAssincronasAdapterTest {

    private static final ContextoExecucao CONTEXTO = new ContextoExecucao("corr-performance");

    @Test
    void deveResponderSemEsperarIntegracoesEExecutaLasEmParaleloParaMaisDeSeisItens() throws Exception {
        CountDownLatch integracoesConcluidas = new CountDownLatch(4);
        List<String> correlationsRecebidas = new CopyOnWriteArrayList<>();
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            IntegracoesAssincronasAdapter adapter = new IntegracoesAssincronasAdapter(
                    (nota, contexto) -> simularIntegracao(200, contexto, integracoesConcluidas, correlationsRecebidas),
                    (nota, contexto) -> simularIntegracao(200, contexto, integracoesConcluidas, correlationsRecebidas),
                    (nota, contexto) -> simularIntegracao(800, contexto, integracoesConcluidas, correlationsRecebidas),
                    (nota, contexto) -> simularIntegracao(200, contexto, integracoesConcluidas, correlationsRecebidas),
                    executor);

            assertTimeout(Duration.ofMillis(200), () -> adapter.publicar(notaComSeteItens(), CONTEXTO));

            assertTrue(integracoesConcluidas.await(1_500, TimeUnit.MILLISECONDS));
            assertEquals(0, integracoesConcluidas.getCount());
            assertEquals(List.of("corr-performance"), correlationsRecebidas.stream().distinct().toList());
        }
    }

    private void simularIntegracao(long duracaoMs, ContextoExecucao contexto, CountDownLatch concluidas,
                                   List<String> correlationsRecebidas) {
        try {
            Thread.sleep(duracaoMs);
            correlationsRecebidas.add(contexto.correlationId());
            concluidas.countDown();
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(exception);
        }
    }

    private NotaFiscal notaComSeteItens() {
        List<ItemNotaFiscal> itens = java.util.stream.IntStream.rangeClosed(1, 7)
                .mapToObj(id -> ItemNotaFiscal.builder().idItem(String.valueOf(id)).build())
                .toList();
        return NotaFiscal.builder().idNotaFiscal("nf-1").itens(itens).build();
    }
}
