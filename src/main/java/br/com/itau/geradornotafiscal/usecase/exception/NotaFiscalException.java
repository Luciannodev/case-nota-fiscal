package br.com.itau.geradornotafiscal.usecase.exception;

public abstract class NotaFiscalException extends RuntimeException {

    private final String codigo;
    private final String campo;

    protected NotaFiscalException(String codigo, String campo, String mensagem) {
        super(mensagem);
        this.codigo = codigo;
        this.campo = campo;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getCampo() {
        return campo;
    }
}
