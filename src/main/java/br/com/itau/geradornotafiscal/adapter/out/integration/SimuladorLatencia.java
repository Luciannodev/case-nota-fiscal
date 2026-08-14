package br.com.itau.geradornotafiscal.adapter.out.integration;

final class SimuladorLatencia {
    private SimuladorLatencia() {
    }

    static void aguardar(long milissegundos) {
        try {
            Thread.sleep(milissegundos);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Integração interrompida", exception);
        }
    }
}
