package br.com.itau.geradornotafiscal.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class ItemNotaFiscal {
    @JsonProperty("id_item")
    private String idItem;

    @JsonProperty("descricao")
    private String descricao;

    @JsonProperty("valor_unitario")
    @Builder.Default
    private BigDecimal valorUnitario = BigDecimal.ZERO;

    @JsonProperty("quantidade")
    private int quantidade;

    @JsonProperty("valor_tributo_item")
    @Builder.Default
    private BigDecimal valorTributoItem = BigDecimal.ZERO;

}
