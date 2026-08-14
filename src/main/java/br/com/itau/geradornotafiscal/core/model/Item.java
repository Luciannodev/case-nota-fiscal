package br.com.itau.geradornotafiscal.core.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class Item {
	 @JsonProperty("id_item")
	    private String idItem;

	    @JsonProperty("descricao")
	    private String descricao;

	    @JsonProperty("valor_unitario")
	    private BigDecimal valorUnitario = BigDecimal.ZERO;

	    @JsonProperty("quantidade")
	    private int quantidade;




}

