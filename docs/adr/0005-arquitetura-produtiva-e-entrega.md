# ADR 0005 — Arquitetura produtiva e ciclo de entrega

- Status: proposto
- Data: 2026-08-13

## Contexto

O desafio pede uma visão coerente de produção, embora não exija provisionar toda a infraestrutura. O processamento assíncrono em memória melhora latência, mas não oferece durabilidade diante de queda do processo.

## Decisão

Para produção, executar a API em ECS Fargate Multi-AZ e substituir o dispatcher em memória por transactional outbox em Aurora PostgreSQL + SQS. Workers independentes consomem as integrações com idempotência, retry, circuit breaker e DLQ.

Adotar pipeline de CI obrigatório em PR e deploy blue/green/canário com rollback para a task definition anterior. A descrição completa está em `docs/arquitetura-producao.md`.

## Consequências

- Nota e intenção de integração são confirmadas atomicamente.
- A API mantém baixa latência mesmo com dependências lentas.
- Há custo operacional de banco, mensageria, workers, observabilidade e reconciliação.
- Consistência com sistemas externos passa a ser eventual e deve estar explícita para consumidores.
