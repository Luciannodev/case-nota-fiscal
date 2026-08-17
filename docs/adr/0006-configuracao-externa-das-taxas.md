# ADR 0006 — Configuração externa das taxas no AWS Parameter Store

- Status: aceito
- Data: 2026-08-14
- Atualizado em: 2026-08-17

## Contexto

As alíquotas tributárias e os multiplicadores de frete estavam codificados nas Strategies. Uma alteração fiscal ou comercial exigiria modificar código, gerar nova imagem e executar novo deploy. Variáveis de ambiente resolvem o acoplamento ao código, mas tornam a alteração de um conjunto de 21 valores extensa e sujeita a atualizações parciais.

As taxas não são segredos, porém são configurações financeiras sensíveis: precisam de controle de acesso, versionamento, atualização por ambiente e validação antes de serem usadas.

## Decisão

Em produção, manter todo o conjunto de taxas em um único parâmetro `String` do AWS Systems Manager Parameter Store, no caminho:

```text
/case-nota-fiscal/{ambiente}/taxas
```

O valor segue o contrato JSON documentado em [`docs/parameter-store-taxas.json`](../parameter-store-taxas.json). Uma única chave evita que a aplicação observe somente parte de uma atualização do conjunto.

A aplicação lê o parâmetro uma vez durante o startup, converte os valores para `BigDecimal` e valida o conjunto completo antes de disponibilizar os casos de uso. Alíquotas devem estar entre `0` e `1`; multiplicadores de frete devem ser positivos. JSON malformado, configuração inválida, parâmetro ausente ou falha de acesso impedem o startup. Não existe fallback silencioso para os defaults quando o Parameter Store está habilitado.

A fonte é selecionada por configuração:

- `TAXAS_PARAMETER_STORE_ENABLED=true` habilita o Parameter Store;
- `TAXAS_PARAMETER_STORE_NAME=/case-nota-fiscal/prod/taxas` informa o parâmetro;
- localmente e nos testes, o recurso permanece desabilitado e `TaxasProperties` usa os valores de `application.properties`, que ainda podem ser sobrescritos pelas variáveis individuais existentes.

O adapter usa o provider chain padrão do AWS SDK. No ECS, a task role deve possuir apenas `ssm:GetParameter` para o caminho do próprio ambiente. Como o conteúdo não é secreto, o parâmetro é lido sem descriptografia. O log de startup registra nome, origem e versão carregada, mas nunca os valores financeiros.

As Strategies continuam responsáveis apenas pela seleção da regra e recebem um `TaxasConfig` imutável pelo composition root. Portanto, nem o core nem os casos de uso dependem do SDK da AWS.

## Alternativas consideradas

- **Uma variável de ambiente por taxa:** mantida como facilidade local, mas rejeitada como fonte principal de produção pelo risco de atualização parcial e pela operação de muitas chaves.
- **Um parâmetro por taxa:** rejeitado porque exigiria várias chamadas e não garantiria uma visão atômica do conjunto.
- **Consulta por request:** rejeitada por adicionar latência, custo e dependência da AWS ao caminho crítico de emissão da nota.
- **AWS AppConfig com atualização dinâmica:** pode ser adotado se houver necessidade real de propagação sem rollout, mas adicionaria cache, polling e estratégia de consistência que o cenário atual não exige.
- **Secrets Manager:** rejeitado porque taxas não são credenciais ou segredos.

## Consequências

- Alterações de taxa não exigem recompilação nem mudança das Strategies.
- Uma nova versão exige reinício/rollout das instâncias; não há atualização dinâmica silenciosa.
- O Parameter Store sai do caminho crítico de cada nota fiscal.
- A inicialização passa a depender da disponibilidade, região, credenciais e permissão do SSM.
- O conjunto pode ser revertido publicando novamente uma versão anterior e executando rollout.
- Testes de negócio continuam declarando valores e resultados esperados; testes do adapter cobrem parsing, validação, leitura única e falhas da AWS.
