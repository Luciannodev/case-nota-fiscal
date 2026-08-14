package br.com.itau.geradornotafiscal.core.model;

import java.time.LocalDate;
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
public class Pedido {
	 @JsonProperty("id_pedido")
	    private int idPedido;

	    @JsonProperty("data")
	    private LocalDate data;

	    @JsonProperty("valor_total_itens")
	    @Builder.Default
	    private BigDecimal valorTotalItens = BigDecimal.ZERO;

	    @JsonProperty("valor_frete")
	    @Builder.Default
	    private BigDecimal valorFrete = BigDecimal.ZERO;

	    @JsonProperty("itens")
	    private List<Item> itens;

	    @JsonProperty("destinatario")
	    private Destinatario destinatario;

}
