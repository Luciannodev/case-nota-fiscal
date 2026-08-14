# ADR 0001 — Strategy para regras de cálculo

- Status: aceito
- Data: 2026-08-13

## Contexto

O fluxo de geração da nota concentrava seleção de alíquota, faixas tributárias e percentuais de frete em uma única classe. Toda inclusão de regime ou região alterava o orquestrador e ampliava sua complexidade ciclomática.

## Decisão

Cada tipo/regime tributário implementa `AliquotaTributariaStrategy`. Cada região implementa `FreteStrategy`. `CalculadoraTributos` e `CalculadoraFrete` selecionam a estratégia compatível e o serviço principal apenas orquestra o resultado.

O contrato HTTP continua usando `double` para manter compatibilidade. Os valores esperados são protegidos por testes de negócio com resultados explícitos. Uma migração futura para `BigDecimal` deve ser feita no domínio sem alterar o payload.

Regimes e endereços sem regra deixam de produzir silenciosamente total ou frete zero e passam a gerar erro explícito.

## Consequências

- Novas regras são adicionadas sem editar o fluxo principal.
- Cada estratégia pode ser testada isoladamente.
- Há mais classes, porém cada uma possui uma única razão para mudar.
- A validação HTTP dos erros de domínio será tratada em evolução posterior.
