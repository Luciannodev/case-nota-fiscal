package br.com.itau.geradornotafiscal.usecase.validation;

import br.com.itau.geradornotafiscal.core.model.Destinatario;
import br.com.itau.geradornotafiscal.core.model.Documento;
import br.com.itau.geradornotafiscal.core.model.Endereco;
import br.com.itau.geradornotafiscal.core.model.Finalidade;
import br.com.itau.geradornotafiscal.core.model.Item;
import br.com.itau.geradornotafiscal.core.model.Pedido;
import br.com.itau.geradornotafiscal.core.model.Regiao;
import br.com.itau.geradornotafiscal.core.model.TipoDocumento;
import br.com.itau.geradornotafiscal.core.model.TipoPessoa;
import br.com.itau.geradornotafiscal.usecase.exception.PedidoInvalidoException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PedidoValidatorTest {

    private final PedidoValidator validator = new PedidoValidator();

    @Test
    void deveAceitarFreteGratisQuandoOsDemaisDadosForemValidos() {
        Pedido pedido = pedidoValido();
        pedido.setValorFrete(BigDecimal.ZERO);

        assertDoesNotThrow(() -> validator.validar(pedido));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("entradasInvalidas")
    void deveRejeitarEntradaInvalida(String cenario, Consumer<Pedido> alteracao, String campoEsperado) {
        Pedido pedido = pedidoValido();
        alteracao.accept(pedido);

        PedidoInvalidoException exception = assertThrows(
                PedidoInvalidoException.class, () -> validator.validar(pedido));

        assertThat(exception.getCodigo()).isEqualTo("PEDIDO_INVALIDO");
        assertThat(exception.getCampo()).isEqualTo(campoEsperado);
    }

    private static Stream<Arguments> entradasInvalidas() {
        return Stream.of(
                Arguments.of("identificador zerado", (Consumer<Pedido>) pedido -> pedido.setIdPedido(0), "id_pedido"),
                Arguments.of("total zerado", (Consumer<Pedido>) pedido -> pedido.setValorTotalItens(BigDecimal.ZERO), "valor_total_itens"),
                Arguments.of("frete negativo", (Consumer<Pedido>) pedido -> pedido.setValorFrete(new BigDecimal("-0.01")), "valor_frete"),
                Arguments.of("lista de itens vazia", (Consumer<Pedido>) pedido -> pedido.setItens(List.of()), "itens"),
                Arguments.of("valor unitário zerado", (Consumer<Pedido>) pedido -> pedido.getItens().getFirst().setValorUnitario(BigDecimal.ZERO), "itens[0].valor_unitario"),
                Arguments.of("quantidade zerada", (Consumer<Pedido>) pedido -> pedido.getItens().getFirst().setQuantidade(0), "itens[0].quantidade"),
                Arguments.of("total divergente dos produtos", (Consumer<Pedido>) pedido -> pedido.setValorTotalItens(new BigDecimal("99.99")), "valor_total_itens"),
                Arguments.of("destinatário ausente", (Consumer<Pedido>) pedido -> pedido.setDestinatario(null), "destinatario"),
                Arguments.of("documento incompatível", (Consumer<Pedido>) pedido -> pedido.getDestinatario().setDocumentos(List.of(new Documento("123", TipoDocumento.CPF))), "destinatario.documentos"),
                Arguments.of("região de entrega ausente", (Consumer<Pedido>) pedido -> pedido.getDestinatario().getEnderecos().getFirst().setRegiao(null), "destinatario.enderecos")
        );
    }

    private static Pedido pedidoValido() {
        Destinatario destinatario = Destinatario.builder()
                .nome("Maria da Silva")
                .tipoPessoa(TipoPessoa.FISICA)
                .documentos(List.of(new Documento("12345678901", TipoDocumento.CPF)))
                .enderecos(List.of(Endereco.builder()
                        .finalidade(Finalidade.ENTREGA)
                        .regiao(Regiao.SUDESTE)
                        .build()))
                .build();
        return Pedido.builder()
                .idPedido(1)
                .data(LocalDate.of(2026, 8, 14))
                .valorTotalItens(new BigDecimal("100.00"))
                .valorFrete(new BigDecimal("10.00"))
                .itens(List.of(new Item("item-1", "Produto", new BigDecimal("50.00"), 2)))
                .destinatario(destinatario)
                .build();
    }
}
