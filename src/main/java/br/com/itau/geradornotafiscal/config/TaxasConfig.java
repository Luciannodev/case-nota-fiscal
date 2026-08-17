package br.com.itau.geradornotafiscal.config;

import java.math.BigDecimal;

public record TaxasConfig(
        Faixas pf,
        Faixas simplesNacional,
        Faixas lucroReal,
        Faixas lucroPresumido,
        Frete frete) {

    public TaxasConfig validar() {
        validarFaixas("pf", pf);
        validarFaixas("simplesNacional", simplesNacional);
        validarFaixas("lucroReal", lucroReal);
        validarFaixas("lucroPresumido", lucroPresumido);
        if (frete == null) {
            throw new IllegalStateException("Configuração de frete não informada");
        }
        validarFrete("norte", frete.norte());
        validarFrete("nordeste", frete.nordeste());
        validarFrete("centroOeste", frete.centroOeste());
        validarFrete("sudeste", frete.sudeste());
        validarFrete("sul", frete.sul());
        return this;
    }

    public static TaxasConfig from(TaxasProperties properties) {
        return new TaxasConfig(
                faixas(properties.getPf()),
                faixas(properties.getSimplesNacional()),
                faixas(properties.getLucroReal()),
                faixas(properties.getLucroPresumido()),
                new Frete(
                        properties.getFrete().getNorte(),
                        properties.getFrete().getNordeste(),
                        properties.getFrete().getCentroOeste(),
                        properties.getFrete().getSudeste(),
                        properties.getFrete().getSul()))
                .validar();
    }

    private static Faixas faixas(TaxasProperties.Faixas faixas) {
        return new Faixas(
                faixas.getFaixa1(), faixas.getFaixa2(), faixas.getFaixa3(), faixas.getFaixa4());
    }

    private static void validarFaixas(String nome, Faixas faixas) {
        if (faixas == null) {
            throw new IllegalStateException("Configuração tributária não informada: " + nome);
        }
        validarAliquota(nome + ".faixa1", faixas.faixa1());
        validarAliquota(nome + ".faixa2", faixas.faixa2());
        validarAliquota(nome + ".faixa3", faixas.faixa3());
        validarAliquota(nome + ".faixa4", faixas.faixa4());
    }

    private static void validarAliquota(String nome, BigDecimal valor) {
        if (valor == null || valor.signum() < 0 || valor.compareTo(BigDecimal.ONE) > 0) {
            throw new IllegalStateException("Alíquota inválida: " + nome);
        }
    }

    private static void validarFrete(String nome, BigDecimal valor) {
        if (valor == null || valor.signum() <= 0) {
            throw new IllegalStateException("Multiplicador de frete inválido: " + nome);
        }
    }

    public record Faixas(BigDecimal faixa1, BigDecimal faixa2, BigDecimal faixa3, BigDecimal faixa4) {
    }

    public record Frete(
            BigDecimal norte,
            BigDecimal nordeste,
            BigDecimal centroOeste,
            BigDecimal sudeste,
            BigDecimal sul) {
    }
}
