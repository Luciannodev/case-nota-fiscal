package br.com.itau.geradornotafiscal;

import br.com.itau.geradornotafiscal.model.Item;
import br.com.itau.geradornotafiscal.model.ItemNotaFiscal;
import br.com.itau.geradornotafiscal.service.CalculadoraAliquotaProduto;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CalculadoraAliquotaProdutoTest {

    private final CalculadoraAliquotaProduto calculadora = new CalculadoraAliquotaProduto();

    @Test
    void deveInformarTributoEValorFinalEsperadosParaUmProduto() {
        Item produto = criarItem("1", 100.00);

        List<ItemNotaFiscal> itensNotaFiscal = calculadora.calcularAliquota(List.of(produto), 0.20);
        ItemNotaFiscal itemNotaFiscal = itensNotaFiscal.getFirst();

        assertEquals(1, itensNotaFiscal.size());
        assertEquals(100.00, itemNotaFiscal.getValorUnitario(), 0.001);
        assertEquals(20.00, itemNotaFiscal.getValorTributoItem(), 0.001);
        assertEquals(120.00, itemNotaFiscal.getValorUnitario() + itemNotaFiscal.getValorTributoItem(), 0.001);
    }

    private Item criarItem(String id, double valorUnitario) {
        return new Item(id, "Produto " + id, valorUnitario, 1);
    }
}
