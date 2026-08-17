package br.com.itau.geradornotafiscal.adapter.out.parameterstore;

import br.com.itau.geradornotafiscal.config.TaxasConfig;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.GetParameterRequest;
import software.amazon.awssdk.services.ssm.model.GetParameterResponse;

public class ParameterStoreTaxasAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ParameterStoreTaxasAdapter.class);

    private final SsmClient ssmClient;
    private final ObjectMapper objectMapper;
    private final String parameterName;

    public ParameterStoreTaxasAdapter(SsmClient ssmClient, ObjectMapper objectMapper, String parameterName) {
        this.ssmClient = ssmClient;
        this.objectMapper = objectMapper;
        this.parameterName = parameterName;
    }

    public TaxasConfig carregar() {
        validarNome();
        try {
            GetParameterResponse response = ssmClient.getParameter(GetParameterRequest.builder()
                    .name(parameterName)
                    .withDecryption(false)
                    .build());
            if (response.parameter() == null || response.parameter().value() == null
                    || response.parameter().value().isBlank()) {
                throw new IllegalStateException("Parameter Store retornou configuração de taxas vazia");
            }

            TaxasConfig taxas = objectMapper.readValue(response.parameter().value(), TaxasConfig.class).validar();
            LOGGER.info("Taxas carregadas do AWS Parameter Store. parameterName={} version={} source=parameter-store",
                    parameterName, response.parameter().version());
            return taxas;
        } catch (JacksonException exception) {
            throw new IllegalStateException("JSON de taxas inválido no parâmetro " + parameterName, exception);
        } catch (SdkException exception) {
            throw new IllegalStateException("Não foi possível carregar as taxas do parâmetro " + parameterName, exception);
        }
    }

    private void validarNome() {
        if (parameterName == null || parameterName.isBlank()) {
            throw new IllegalStateException("O nome do parâmetro de taxas deve ser informado");
        }
    }
}
