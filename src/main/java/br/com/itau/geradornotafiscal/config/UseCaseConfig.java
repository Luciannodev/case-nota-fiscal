package br.com.itau.geradornotafiscal.config;

import br.com.itau.geradornotafiscal.usecase.strategy.CentroOesteFreteStrategy;
import br.com.itau.geradornotafiscal.usecase.strategy.FaixasAliquota;
import br.com.itau.geradornotafiscal.usecase.strategy.LucroPresumidoAliquotaStrategy;
import br.com.itau.geradornotafiscal.usecase.strategy.LucroRealAliquotaStrategy;
import br.com.itau.geradornotafiscal.usecase.strategy.NordesteFreteStrategy;
import br.com.itau.geradornotafiscal.usecase.strategy.NorteFreteStrategy;
import br.com.itau.geradornotafiscal.usecase.strategy.PessoaFisicaAliquotaStrategy;
import br.com.itau.geradornotafiscal.usecase.strategy.SimplesNacionalAliquotaStrategy;
import br.com.itau.geradornotafiscal.usecase.strategy.SudesteFreteStrategy;
import br.com.itau.geradornotafiscal.usecase.strategy.SulFreteStrategy;
import br.com.itau.geradornotafiscal.usecase.GerarNotaFiscalUseCase;
import br.com.itau.geradornotafiscal.usecase.GerarNotaFiscalUseCaseImpl;
import br.com.itau.geradornotafiscal.usecase.calculo.CalculadoraAliquotaProduto;
import br.com.itau.geradornotafiscal.usecase.calculo.CalculadoraFrete;
import br.com.itau.geradornotafiscal.usecase.calculo.CalculadoraTributos;
import br.com.itau.geradornotafiscal.usecase.validation.PedidoValidator;
import br.com.itau.geradornotafiscal.port.out.PublicarIntegracoesNotaFiscalPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class UseCaseConfig {

    @Bean
    PedidoValidator pedidoValidator() {
        return new PedidoValidator();
    }

    @Bean
    CalculadoraAliquotaProduto calculadoraAliquotaProduto() {
        return new CalculadoraAliquotaProduto();
    }

    @Bean
    CalculadoraTributos calculadoraTributos(TaxasProperties taxas,
                                            CalculadoraAliquotaProduto calculadoraAliquotaProduto) {
        return new CalculadoraTributos(List.of(
                new PessoaFisicaAliquotaStrategy(faixas(taxas.getPf())),
                new SimplesNacionalAliquotaStrategy(faixas(taxas.getSimplesNacional())),
                new LucroRealAliquotaStrategy(faixas(taxas.getLucroReal())),
                new LucroPresumidoAliquotaStrategy(faixas(taxas.getLucroPresumido()))), calculadoraAliquotaProduto);
    }

    @Bean
    CalculadoraFrete calculadoraFrete(TaxasProperties taxas) {
        return new CalculadoraFrete(List.of(
                new NorteFreteStrategy(taxas.getFrete().getNorte()),
                new NordesteFreteStrategy(taxas.getFrete().getNordeste()),
                new CentroOesteFreteStrategy(taxas.getFrete().getCentroOeste()),
                new SudesteFreteStrategy(taxas.getFrete().getSudeste()),
                new SulFreteStrategy(taxas.getFrete().getSul())));
    }

    @Bean
    GerarNotaFiscalUseCase gerarNotaFiscalUseCase(CalculadoraTributos tributos,
                                                  CalculadoraFrete frete,
                                                  PublicarIntegracoesNotaFiscalPort integracoes,
                                                  PedidoValidator pedidoValidator) {
        return new GerarNotaFiscalUseCaseImpl(tributos, frete, integracoes, pedidoValidator);
    }

    private FaixasAliquota faixas(TaxasProperties.Faixas taxas) {
        return new FaixasAliquota(
                taxas.getFaixa1(), taxas.getFaixa2(), taxas.getFaixa3(), taxas.getFaixa4());
    }
}
