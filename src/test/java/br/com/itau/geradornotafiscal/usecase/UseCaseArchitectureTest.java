package br.com.itau.geradornotafiscal.usecase;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class UseCaseArchitectureTest {

    private static final Path USE_CASE = Path.of(
            "src/main/java/br/com/itau/geradornotafiscal/usecase");
    private static final List<String> DEPENDENCIAS_PROIBIDAS = List.of(
            "import org.springframework.",
            "import br.com.itau.geradornotafiscal.adapter.",
            "import br.com.itau.geradornotafiscal.config.");

    @Test
    void useCaseNaoDeveDependerDeSpringConfiguracaoOuAdapters() throws IOException {
        try (var arquivos = Files.walk(USE_CASE)) {
            List<String> violacoes = arquivos
                    .filter(arquivo -> arquivo.toString().endsWith(".java"))
                    .flatMap(arquivo -> dependenciasProibidas(arquivo).stream())
                    .toList();

            assertTrue(violacoes.isEmpty(), () -> "Dependências proibidas no use case: " + violacoes);
        }
    }

    private List<String> dependenciasProibidas(Path arquivo) {
        try {
            String fonte = Files.readString(arquivo);
            return DEPENDENCIAS_PROIBIDAS.stream()
                    .filter(fonte::contains)
                    .map(dependencia -> arquivo + " -> " + dependencia)
                    .toList();
        } catch (IOException exception) {
            throw new IllegalStateException("Não foi possível ler " + arquivo, exception);
        }
    }
}
