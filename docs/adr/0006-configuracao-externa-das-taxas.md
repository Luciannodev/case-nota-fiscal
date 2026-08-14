# ADR 0006 — Configuração externa das taxas

- Status: aceito
- Data: 2026-08-14

## Contexto

As alíquotas tributárias e os multiplicadores de frete estavam codificados nas Strategies. Uma alteração fiscal ou comercial exigiria modificar código, gerar nova imagem e executar novo deploy.

## Decisão

Centralizar as taxas em `TaxasProperties`, usando `@ConfigurationProperties`. As Strategies continuam responsáveis pela seleção da faixa, mas recebem seus valores por injeção de dependência.

Cada propriedade possui o valor vigente como default em `application.properties` e pode ser sobrescrita pelas seguintes variáveis de ambiente:

- `TAXA_TRIBUTO_PF_FAIXA_1` até `TAXA_TRIBUTO_PF_FAIXA_4`;
- `TAXA_TRIBUTO_SIMPLES_NACIONAL_FAIXA_1` até `TAXA_TRIBUTO_SIMPLES_NACIONAL_FAIXA_4`;
- `TAXA_TRIBUTO_LUCRO_REAL_FAIXA_1` até `TAXA_TRIBUTO_LUCRO_REAL_FAIXA_4`;
- `TAXA_TRIBUTO_LUCRO_PRESUMIDO_FAIXA_1` até `TAXA_TRIBUTO_LUCRO_PRESUMIDO_FAIXA_4`;
- `TAXA_FRETE_NORTE`, `TAXA_FRETE_NORDESTE`, `TAXA_FRETE_CENTRO_OESTE`, `TAXA_FRETE_SUDESTE` e `TAXA_FRETE_SUL`.

Alíquotas usam formato decimal (`0.12` representa 12%) e devem estar entre `0` e `1`. Fretes usam multiplicador (`1.08` representa acréscimo de 8%) e devem ser positivos. A aplicação valida tudo na inicialização e falha rapidamente diante de configuração inválida.

Em produção, os valores devem ser administrados pelo mecanismo de configuração do ambiente (task definition/Parameter Store), com trilha de auditoria e implantação controlada. Segredos não são necessários para taxas, mas permissões de alteração devem ser restritas.

## Consequências

- Alterações de taxa não exigem recompilação nem mudança das Strategies.
- Uma nova configuração exige reinício/rollout das instâncias; não há atualização dinâmica silenciosa.
- Defaults mantêm o comportamento local e facilitam desenvolvimento.
- Testes de negócio continuam declarando valores esperados e testes de configuração garantem binding e fail-fast.
