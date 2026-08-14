package br.com.itau.geradornotafiscal.usecase.validation;

import br.com.itau.geradornotafiscal.core.model.Destinatario;
import br.com.itau.geradornotafiscal.core.model.Documento;
import br.com.itau.geradornotafiscal.core.model.Endereco;
import br.com.itau.geradornotafiscal.core.model.Finalidade;
import br.com.itau.geradornotafiscal.core.model.Item;
import br.com.itau.geradornotafiscal.core.model.Pedido;
import br.com.itau.geradornotafiscal.core.model.RegimeTributacaoPJ;
import br.com.itau.geradornotafiscal.core.model.TipoDocumento;
import br.com.itau.geradornotafiscal.core.model.TipoPessoa;
import br.com.itau.geradornotafiscal.usecase.exception.PedidoInvalidoException;

import java.math.BigDecimal;
import java.util.List;

public class PedidoValidator {

    public void validar(Pedido pedido) {
        obrigatorio(pedido, "pedido", "O pedido deve ser informado");
        positivo(pedido.getIdPedido(), "id_pedido", "O identificador do pedido deve ser maior que zero");
        obrigatorio(pedido.getData(), "data", "A data do pedido deve ser informada");
        positivo(pedido.getValorTotalItens(), "valor_total_itens", "O valor total dos itens deve ser maior que zero");
        naoNegativo(pedido.getValorFrete(), "valor_frete", "O valor do frete não pode ser negativo");
        validarItens(pedido.getItens(), pedido.getValorTotalItens());
        validarDestinatario(pedido.getDestinatario());
    }

    private void validarItens(List<Item> itens, BigDecimal totalInformado) {
        if (itens == null || itens.isEmpty()) {
            falhar("itens", "O pedido deve possuir ao menos um item");
        }

        BigDecimal totalCalculado = BigDecimal.ZERO;
        for (int indice = 0; indice < itens.size(); indice++) {
            Item item = itens.get(indice);
            String campo = "itens[" + indice + "]";
            obrigatorio(item, campo, "O item deve ser informado");
            textoObrigatorio(item.getIdItem(), campo + ".id_item", "O identificador do item deve ser informado");
            textoObrigatorio(item.getDescricao(), campo + ".descricao", "A descrição do item deve ser informada");
            positivo(item.getValorUnitario(), campo + ".valor_unitario", "O valor unitário deve ser maior que zero");
            positivo(item.getQuantidade(), campo + ".quantidade", "A quantidade deve ser maior que zero");
            totalCalculado = totalCalculado.add(
                    item.getValorUnitario().multiply(BigDecimal.valueOf(item.getQuantidade())));
        }

        if (totalCalculado.compareTo(totalInformado) != 0) {
            falhar("valor_total_itens", "O valor total informado deve corresponder à soma dos itens: " + totalCalculado);
        }
    }

    private void validarDestinatario(Destinatario destinatario) {
        obrigatorio(destinatario, "destinatario", "O destinatário deve ser informado");
        textoObrigatorio(destinatario.getNome(), "destinatario.nome", "O nome do destinatário deve ser informado");
        obrigatorio(destinatario.getTipoPessoa(), "destinatario.tipo_pessoa", "O tipo de pessoa deve ser informado");
        validarRegime(destinatario);
        validarDocumentos(destinatario);
        validarEnderecoEntrega(destinatario.getEnderecos());
    }

    private void validarRegime(Destinatario destinatario) {
        if (destinatario.getTipoPessoa() == TipoPessoa.JURIDICA
                && (destinatario.getRegimeTributacao() == null
                || destinatario.getRegimeTributacao() == RegimeTributacaoPJ.OUTROS)) {
            falhar("destinatario.regime_tributacao", "Pessoa jurídica deve possuir um regime tributário suportado");
        }
        if (destinatario.getTipoPessoa() == TipoPessoa.FISICA && destinatario.getRegimeTributacao() != null) {
            falhar("destinatario.regime_tributacao", "Pessoa física não deve possuir regime tributário de pessoa jurídica");
        }
    }

    private void validarDocumentos(Destinatario destinatario) {
        List<Documento> documentos = destinatario.getDocumentos();
        if (documentos == null || documentos.isEmpty()) {
            falhar("destinatario.documentos", "O destinatário deve possuir um documento");
        }

        TipoDocumento tipoEsperado = destinatario.getTipoPessoa() == TipoPessoa.FISICA
                ? TipoDocumento.CPF : TipoDocumento.CNPJ;
        int tamanhoEsperado = tipoEsperado == TipoDocumento.CPF ? 11 : 14;
        boolean documentoValido = documentos.stream()
                .filter(documento -> documento != null && documento.getTipo() == tipoEsperado)
                .map(Documento::getNumero)
                .anyMatch(numero -> numero != null && numero.matches("\\d{" + tamanhoEsperado + "}"));
        if (!documentoValido) {
            falhar("destinatario.documentos", "Deve ser informado um " + tipoEsperado + " com " + tamanhoEsperado + " dígitos");
        }
    }

    private void validarEnderecoEntrega(List<Endereco> enderecos) {
        if (enderecos == null || enderecos.isEmpty()) {
            falhar("destinatario.enderecos", "O destinatário deve possuir um endereço de entrega");
        }
        boolean entregaValida = enderecos.stream()
                .filter(endereco -> endereco != null && enderecoEntrega(endereco))
                .anyMatch(endereco -> endereco.getRegiao() != null);
        if (!entregaValida) {
            falhar("destinatario.enderecos", "Deve existir um endereço de entrega com região informada");
        }
    }

    private boolean enderecoEntrega(Endereco endereco) {
        return endereco.getFinalidade() == Finalidade.ENTREGA
                || endereco.getFinalidade() == Finalidade.COBRANCA_ENTREGA;
    }

    private void positivo(BigDecimal valor, String campo, String mensagem) {
        if (valor == null || valor.signum() <= 0) {
            falhar(campo, mensagem);
        }
    }

    private void positivo(int valor, String campo, String mensagem) {
        if (valor <= 0) {
            falhar(campo, mensagem);
        }
    }

    private void naoNegativo(BigDecimal valor, String campo, String mensagem) {
        if (valor == null || valor.signum() < 0) {
            falhar(campo, mensagem);
        }
    }

    private void textoObrigatorio(String valor, String campo, String mensagem) {
        if (valor == null || valor.isBlank()) {
            falhar(campo, mensagem);
        }
    }

    private void obrigatorio(Object valor, String campo, String mensagem) {
        if (valor == null) {
            falhar(campo, mensagem);
        }
    }

    private void falhar(String campo, String mensagem) {
        throw new PedidoInvalidoException(campo, mensagem);
    }
}
