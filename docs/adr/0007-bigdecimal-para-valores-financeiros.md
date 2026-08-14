# ADR 0007 — BigDecimal para valores financeiros

- Status: aceito
- Data: 2026-08-14

## Contexto

Valores monetários e taxas usavam `double`, que representa números em ponto flutuante binário e pode introduzir diferenças de precisão em tributos, frete e somatórios.

## Decisão

Usar `BigDecimal` em todos os campos e operações financeiras: valores do pedido, item, tributo, frete, total da nota, alíquotas e multiplicadores externos.

- valores monetários são arredondados para duas casas com `RoundingMode.HALF_UP`;
- o tributo é arredondado por unidade antes de ser multiplicado pela quantidade, de acordo com o contrato atual de `valor_tributo_item`;
- taxas preservam sua precisão configurada e só o resultado monetário recebe escala 2;
- constantes de teste usam strings para evitar conversão prévia por ponto flutuante;
- o JSON continua expondo números e mantém os mesmos nomes de campo.

## Consequências

- cálculos financeiros tornam-se determinísticos e independentes de erros binários;
- igualdade passa a considerar escala quando `BigDecimal.equals` é usado; comparações de negócio utilizam `compareTo`;
- há mais verbosidade nas operações e a política de arredondamento fica explícita;
- consumidores continuam compatíveis com o contrato JSON numérico.
