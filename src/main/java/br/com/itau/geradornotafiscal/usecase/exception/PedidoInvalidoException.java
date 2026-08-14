package br.com.itau.geradornotafiscal.usecase.exception;

public class PedidoInvalidoException extends NotaFiscalException {

    public PedidoInvalidoException(String campo, String mensagem) {
        super("PEDIDO_INVALIDO", campo, mensagem);
    }
}
