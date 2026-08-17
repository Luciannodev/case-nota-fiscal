package br.com.itau.geradornotafiscal.config;

import br.com.itau.geradornotafiscal.adapter.out.parameterstore.ParameterStoreTaxasAdapter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;

import static br.com.itau.geradornotafiscal.support.TaxasTestFactory.taxasPadrao;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TaxasConfigurationTest {

    private final TaxasConfiguration configuration = new TaxasConfiguration();

    @Test
    void deveUsarConfiguracaoLocalQuandoParameterStoreEstiverDesabilitado() {
        ParameterStoreTaxasProperties properties = new ParameterStoreTaxasProperties();
        ObjectProvider<ParameterStoreTaxasAdapter> provider = providerMock();

        TaxasConfig taxas = configuration.taxasConfig(taxasPadrao(), properties, provider);

        assertThat(taxas.pf().faixa2()).isEqualByComparingTo("0.12");
        assertThat(taxas.frete().sudeste()).isEqualByComparingTo("1.048");
        verify(provider, never()).getObject();
    }

    @Test
    void deveUsarSomenteParameterStoreQuandoEstiverHabilitado() {
        ParameterStoreTaxasProperties properties = new ParameterStoreTaxasProperties();
        properties.setEnabled(true);
        ParameterStoreTaxasAdapter adapter = mock(ParameterStoreTaxasAdapter.class);
        ObjectProvider<ParameterStoreTaxasAdapter> provider = providerMock();
        TaxasConfig esperada = TaxasConfig.from(taxasPadrao());
        when(provider.getObject()).thenReturn(adapter);
        when(adapter.carregar()).thenReturn(esperada);

        TaxasConfig taxas = configuration.taxasConfig(new TaxasProperties(), properties, provider);

        assertThat(taxas).isSameAs(esperada);
        verify(adapter).carregar();
    }

    @SuppressWarnings("unchecked")
    private ObjectProvider<ParameterStoreTaxasAdapter> providerMock() {
        return mock(ObjectProvider.class);
    }
}
