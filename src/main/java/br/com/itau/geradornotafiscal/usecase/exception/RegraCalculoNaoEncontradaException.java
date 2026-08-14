package br.com.itau.geradornotafiscal.usecase.exception;

public class RegraCalculoNaoEncontradaException extends NotaFiscalException {

    public RegraCalculoNaoEncontradaException(String campo, String mensagem) {
        super("REGRA_CALCULO_NAO_ENCONTRADA", campo, mensagem);
    }
}
