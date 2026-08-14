package br.com.itau.geradornotafiscal.adapter.out.integration;

import br.com.itau.geradornotafiscal.model.ItemNotaFiscal;
import br.com.itau.geradornotafiscal.model.NotaFiscal;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTimeout;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IntegracoesAssincronasAdapterTest {

    @Test
    void deveResponderSemEsperarIntegracoesEExecutaLasEmParaleloParaMaisDeSeisItens() throws Exception {
        CountDownLatch integracoesConcluidas = new CountDownLatch(4);
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            IntegracoesAssincronasAdapter adapter = new IntegracoesAssincronasAdapter(
                    nota -> simularIntegracao(200, integracoesConcluidas),
                    nota -> simularIntegracao(200, integracoesConcluidas),
                    nota -> simularIntegracao(800, integracoesConcluidas),
                    nota -> simularIntegracao(200, integracoesConcluidas),
                    executor);

            assertTimeout(Duration.ofMillis(200), () -> adapter.publicar(notaComSeteItens()));

            assertTrue(integracoesConcluidas.await(1_500, TimeUnit.MILLISECONDS));
            assertEquals(0, integracoesConcluidas.getCount());
        }
    }

    private void simularIntegracao(long duracaoMs, CountDownLatch concluidas) {
        try {
            Thread.sleep(duracaoMs);
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
