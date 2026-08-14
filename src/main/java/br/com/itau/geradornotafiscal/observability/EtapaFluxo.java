package br.com.itau.geradornotafiscal.observability;

public enum EtapaFluxo {
    GERACAO_NOTA_FISCAL("nota-fiscal.total", "Processando a geração da nota fiscal"),
    VALIDACAO_PEDIDO("validacao.pedido", "Validando os dados e valores do pedido"),
    CALCULO_TRIBUTOS("calculo.tributos", "Calculando os tributos de cada item"),
    SOMA_TOTAL_ITENS("calculo.total-itens", "Somando produtos, quantidades e tributos"),
    CALCULO_FRETE("calculo.frete", "Calculando o frete conforme a região de entrega"),
    MONTAGEM_NOTA_FISCAL("montagem.nota-fiscal", "Montando a nota fiscal com os valores calculados"),
    PUBLICACAO_INTEGRACOES("publicacao.integracoes", "Publicando a nota fiscal para as integrações"),
    PROCESSAMENTO_INTEGRACOES("integrations.total", "Processando as integrações externas em paralelo"),
    BAIXA_ESTOQUE("integration.estoque", "Enviando a nota fiscal para baixa de estoque"),
    REGISTRO_NOTA("integration.registro", "Enviando a nota fiscal para registro"),
    AGENDAMENTO_ENTREGA("integration.entrega", "Solicitando o agendamento da entrega"),
    CONTAS_A_RECEBER("integration.financeiro", "Enviando a nota fiscal para contas a receber");

    private final String codigo;
    private final String descricao;

    EtapaFluxo(String codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public String codigo() {
        return codigo;
    }

    public String descricao() {
        return descricao;
    }
}
