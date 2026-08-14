package br.com.itau.geradornotafiscal.model;

import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Builder
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class NotaFiscal {
    @JsonProperty("id_nota_fiscal")
    private String idNotaFiscal;

    @JsonProperty("data")
    private LocalDateTime data;

    @JsonProperty("valor_total_itens")
    @Builder.Default
    private BigDecimal valorTotalItens = BigDecimal.ZERO;

    @JsonProperty("valor_frete")
    @Builder.Default
    private BigDecimal valorFrete = BigDecimal.ZERO;

    @JsonProperty("itens")
    private List<ItemNotaFiscal> itens;

    @JsonProperty("destinatario")
    private Destinatario destinatario;

}
