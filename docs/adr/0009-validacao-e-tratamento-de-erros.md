# ADR 0009 — Validação e tratamento consistente de erros

- Status: aceito
- Data: 2026-08-14

## Contexto

O fluxo acessava campos aninhados sem validação e lançava `IllegalArgumentException` genérica quando não encontrava uma regra. Entradas nulas, zeradas, negativas ou inconsistentes podiam resultar em `NullPointerException`, cálculo com faixa tributária incorreta ou respostas HTTP sem formato previsível.

## Decisão

- Validar o pedido no início do caso de uso, antes de executar qualquer Strategy.
- Exigir valores positivos para identificador, total dos itens, preço unitário e quantidade.
- Permitir frete zero, representando frete grátis, e rejeitar frete negativo.
- Conferir se `valor_total_itens` corresponde à soma de `valor_unitario × quantidade`.
- Validar destinatário, tipo de pessoa, regime tributário, documento e endereço de entrega com região.
- Representar erros esperados por exceções próprias em `usecase/exception`.
- Converter erros de negócio e JSON inválido em HTTP 400 por um handler no adapter web.
- Não devolver detalhes internos em falhas inesperadas; responder HTTP 500 com código estável.
- Incluir campo, código, caminho e `correlationId` na resposta de erro.

## Consequências

- Strategies podem assumir que receberam dados válidos e permanecem focadas no cálculo.
- Consumidores recebem erros previsíveis e rastreáveis.
- Total informado incorretamente não influencia silenciosamente a escolha da alíquota.
- A validação interrompe o fluxo no primeiro erro; uma futura necessidade de retornar todos os erros exigirá um agregador de violações.
