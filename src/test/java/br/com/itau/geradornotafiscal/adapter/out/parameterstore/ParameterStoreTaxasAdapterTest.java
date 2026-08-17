package br.com.itau.geradornotafiscal.adapter.out.parameterstore;

import br.com.itau.geradornotafiscal.config.TaxasConfig;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.GetParameterRequest;
import software.amazon.awssdk.services.ssm.model.GetParameterResponse;
import software.amazon.awssdk.services.ssm.model.Parameter;
import software.amazon.awssdk.services.ssm.model.SsmException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ParameterStoreTaxasAdapterTest {

    private static final String PARAMETER_NAME = "/case-nota-fiscal/test/taxas";
    private final SsmClient ssmClient = mock(SsmClient.class);
    private final ParameterStoreTaxasAdapter adapter =
            new ParameterStoreTaxasAdapter(ssmClient, new ObjectMapper(), PARAMETER_NAME);

    @Test
    void deveCarregarTodasAsTaxasDoParameterStoreEmUmaUnicaLeitura() {
        when(ssmClient.getParameter(org.mockito.ArgumentMatchers.any(GetParameterRequest.class)))
                .thenReturn(respostaCom(JSON_VALIDO));

        TaxasConfig taxas = adapter.carregar();

        assertThat(taxas.pf().faixa2()).isEqualByComparingTo("0.12");
        assertThat(taxas.simplesNacional().faixa4()).isEqualByComparingTo("0.19");
        assertThat(taxas.lucroReal().faixa3()).isEqualByComparingTo("0.15");
        assertThat(taxas.lucroPresumido().faixa3()).isEqualByComparingTo("0.16");
        assertThat(taxas.frete().centroOeste()).isEqualByComparingTo("1.07");

        ArgumentCaptor<GetParameterRequest> captor = ArgumentCaptor.forClass(GetParameterRequest.class);
        verify(ssmClient).getParameter(captor.capture());
        assertThat(captor.getValue().name()).isEqualTo(PARAMETER_NAME);
        assertThat(captor.getValue().withDecryption()).isFalse();
    }

    @Test
    void deveFalharNoStartupQuandoOJsonEstiverMalformado() {
        when(ssmClient.getParameter(org.mockito.ArgumentMatchers.any(GetParameterRequest.class)))
                .thenReturn(respostaCom("{json-invalido"));

        assertThatThrownBy(adapter::carregar)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("JSON de taxas inválido")
                .hasMessageContaining(PARAMETER_NAME);
    }

    @Test
    void deveFalharNoStartupQuandoUmaAliquotaEstiverForaDaRegra() {
        String jsonInvalido = JSON_VALIDO.replace("\"faixa2\": 0.12", "\"faixa2\": 1.20");
        when(ssmClient.getParameter(org.mockito.ArgumentMatchers.any(GetParameterRequest.class)))
                .thenReturn(respostaCom(jsonInvalido));

        assertThatThrownBy(adapter::carregar)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Alíquota inválida: pf.faixa2");
    }

    @Test
    void deveFalharNoStartupQuandoOParametroEstiverVazio() {
        when(ssmClient.getParameter(org.mockito.ArgumentMatchers.any(GetParameterRequest.class)))
                .thenReturn(respostaCom(" "));

        assertThatThrownBy(adapter::carregar)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("configuração de taxas vazia");
    }

    @Test
    void deveTraduzirFalhaDaAwsSemPermitirStartupComDefaults() {
        when(ssmClient.getParameter(org.mockito.ArgumentMatchers.any(GetParameterRequest.class)))
                .thenThrow(SsmException.builder().message("acesso negado").build());

        assertThatThrownBy(adapter::carregar)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Não foi possível carregar as taxas")
                .hasCauseInstanceOf(SsmException.class);
    }

    @Test
    void deveExigirONomeDoParametro() {
        ParameterStoreTaxasAdapter semNome = new ParameterStoreTaxasAdapter(ssmClient, new ObjectMapper(), " ");

        assertThatThrownBy(semNome::carregar)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("nome do parâmetro");
    }

    private GetParameterResponse respostaCom(String valor) {
        return GetParameterResponse.builder()
                .parameter(Parameter.builder()
                        .name(PARAMETER_NAME)
                        .value(valor)
                        .version(7L)
                        .build())
                .build();
    }

    private static final String JSON_VALIDO = """
            {
              "pf": {"faixa1": 0.00, "faixa2": 0.12, "faixa3": 0.15, "faixa4": 0.17},
              "simplesNacional": {"faixa1": 0.03, "faixa2": 0.07, "faixa3": 0.13, "faixa4": 0.19},
              "lucroReal": {"faixa1": 0.03, "faixa2": 0.09, "faixa3": 0.15, "faixa4": 0.20},
              "lucroPresumido": {"faixa1": 0.03, "faixa2": 0.09, "faixa3": 0.16, "faixa4": 0.20},
              "frete": {"norte": 1.08, "nordeste": 1.085, "centroOeste": 1.07, "sudeste": 1.048, "sul": 1.06}
            }
            """;
}
