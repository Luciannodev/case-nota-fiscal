# ADR 0004 — Observabilidade e propagação de Correlation ID

- Status: aceito
- Data: 2026-08-13

## Contexto

Não era possível reconstruir o fluxo de uma requisição nem identificar qual integração consumia tempo. O processamento assíncrono também perde automaticamente o contexto baseado em `ThreadLocal` ao trocar de thread.

## Decisão

- aceitar `X-Correlation-ID` válido ou gerar UUID no primeiro filtro HTTP;
- devolver o identificador no header da resposta;
- armazená-lo no MDC durante o request;
- passá-lo explicitamente em `ContextoExecucao` pelo caso de uso e por todas as portas de saída;
- restaurar o MDC dentro de cada virtual thread;
- registrar logs `key=value` no início e fim de cada request, etapa de cálculo e integração, incluindo `status` e `durationMs`.

O contexto é explícito nas portas porque depender somente do MDC seria frágil em execução assíncrona e não garantiria a propagação futura no header das chamadas HTTP.

## Consequências

- Uma nota pode ser rastreada do ingresso até estoque, registro, entrega e financeiro.
- Gargalos ficam visíveis por etapa e integração.
- Logs não carregam conteúdo sensível do pedido ou documentos.
- Em produção, os logs estruturados podem ser enviados para CloudWatch/OpenSearch e convertidos em métricas e alertas de latência/erro.
