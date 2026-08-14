package br.com.itau.geradornotafiscal.usecase.strategy;

import java.math.BigDecimal;

public record FaixasAliquota(
        BigDecimal faixa1,
        BigDecimal faixa2,
        BigDecimal faixa3,
        BigDecimal faixa4) {
}
