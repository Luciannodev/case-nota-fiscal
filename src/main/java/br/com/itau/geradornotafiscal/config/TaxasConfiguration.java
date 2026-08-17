package br.com.itau.geradornotafiscal.config;

import br.com.itau.geradornotafiscal.adapter.out.parameterstore.ParameterStoreTaxasAdapter;
import tools.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.services.ssm.SsmClient;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(ParameterStoreTaxasProperties.class)
public class TaxasConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaxasConfiguration.class);

    @Bean(destroyMethod = "close")
    @ConditionalOnProperty(name = "nota-fiscal.taxas.parameter-store.enabled", havingValue = "true")
    SsmClient ssmClient() {
        return SsmClient.create();
    }

    @Bean
    @ConditionalOnProperty(name = "nota-fiscal.taxas.parameter-store.enabled", havingValue = "true")
    ParameterStoreTaxasAdapter parameterStoreTaxasAdapter(
            SsmClient ssmClient,
            ObjectMapper objectMapper,
            ParameterStoreTaxasProperties properties) {
        return new ParameterStoreTaxasAdapter(ssmClient, objectMapper, properties.getName());
    }

    @Bean
    TaxasConfig taxasConfig(
            TaxasProperties taxasLocais,
            ParameterStoreTaxasProperties properties,
            ObjectProvider<ParameterStoreTaxasAdapter> parameterStoreAdapter) {
        if (properties.isEnabled()) {
            return parameterStoreAdapter.getObject().carregar();
        }
        LOGGER.info("Taxas carregadas da configuração local. source=local");
        return TaxasConfig.from(taxasLocais);
    }
}
