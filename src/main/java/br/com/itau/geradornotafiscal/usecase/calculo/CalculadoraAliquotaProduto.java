package br.com.itau.geradornotafiscal.usecase.calculo;

import br.com.itau.geradornotafiscal.core.model.Item;
import br.com.itau.geradornotafiscal.core.model.ItemNotaFiscal;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class CalculadoraAliquotaProduto {

    public List<ItemNotaFiscal> calcularAliquota(List<Item> items, BigDecimal aliquotaPercentual) {
        List<ItemNotaFiscal> itemNotaFiscalList = new ArrayList<>();
        for (Item item : items) {
            BigDecimal valorTributo = item.getValorUnitario()
                    .multiply(aliquotaPercentual)
                    .setScale(2, RoundingMode.HALF_UP);
            ItemNotaFiscal itemNotaFiscal = ItemNotaFiscal.builder()
                    .idItem(item.getIdItem())
                    .descricao(item.getDescricao())
                    .valorUnitario(item.getValorUnitario())
                    .quantidade(item.getQuantidade())
                    .valorTributoItem(valorTributo)
                    .build();
            itemNotaFiscalList.add(itemNotaFiscal);
        }
        return itemNotaFiscalList;
    }
}



