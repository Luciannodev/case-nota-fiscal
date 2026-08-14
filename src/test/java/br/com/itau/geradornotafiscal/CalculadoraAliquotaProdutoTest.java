package br.com.itau.geradornotafiscal;

import br.com.itau.geradornotafiscal.model.Item;
import br.com.itau.geradornotafiscal.model.ItemNotaFiscal;
import br.com.itau.geradornotafiscal.service.CalculadoraAliquotaProduto;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CalculadoraAliquotaProdutoTest {

    private final CalculadoraAliquotaProduto calculadora = new CalculadoraAliquotaProduto();

    @Test
    void deveInformarTributoEValorFinalEsperadosParaUmProduto() {
        Item produto = criarItem("1", "100.00");

        List<ItemNotaFiscal> itensNotaFiscal = calculadora.calcularAliquota(
                List.of(produto), new BigDecimal("0.20"));
        ItemNotaFiscal itemNotaFiscal = itensNotaFiscal.getFirst();

        assertEquals(1, itensNotaFiscal.size());
        assertEquals(new BigDecimal("100.00"), itemNotaFiscal.getValorUnitario());
        assertEquals(new BigDecimal("20.00"), itemNotaFiscal.getValorTributoItem());
        assertEquals(new BigDecimal("120.00"),
                itemNotaFiscal.getValorUnitario().add(itemNotaFiscal.getValorTributoItem()));
    }

    private Item criarItem(String id, String valorUnitario) {
        return new Item(id, "Produto " + id, new BigDecimal(valorUnitario), 1);
    }
}
