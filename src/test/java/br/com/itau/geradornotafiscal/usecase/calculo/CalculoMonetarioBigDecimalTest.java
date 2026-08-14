package br.com.itau.geradornotafiscal.usecase.calculo;

import br.com.itau.geradornotafiscal.config.TaxasProperties;
import br.com.itau.geradornotafiscal.core.model.Item;
import br.com.itau.geradornotafiscal.usecase.strategy.SudesteFreteStrategy;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static br.com.itau.geradornotafiscal.support.TaxasTestFactory.taxasPadrao;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CalculoMonetarioBigDecimalTest {

    @Test
    void deveArredondarTributoMonetarioParaDuasCasas() {
        Item item = new Item("1", "Produto", new BigDecimal("10.05"), 1);

        BigDecimal tributo = new CalculadoraAliquotaProduto()
                .calcularAliquota(List.of(item), new BigDecimal("0.17"))
                .getFirst()
                .getValorTributoItem();

        assertEquals(new BigDecimal("1.71"), tributo);
    }

    @Test
    void deveArredondarFreteMonetarioParaDuasCasas() {
        TaxasProperties taxas = taxasPadrao();

        BigDecimal frete = new SudesteFreteStrategy(taxas.getFrete().getSudeste())
                .calcular(new BigDecimal("100.01"));

        assertEquals(new BigDecimal("104.81"), frete);
    }
}
