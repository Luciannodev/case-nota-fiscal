# ADR 0002 — Arquitetura hexagonal para integrações

- Status: aceito
- Data: 2026-08-13

## Contexto

O caso de uso criava serviços e clientes externos com `new`. A regra de negócio conhecia detalhes de latência e os testes precisavam executar esperas reais, levando mais de 45 segundos.

## Decisão

Adotar portas e adapters:

- `GerarNotaFiscalUseCase` é a porta de entrada usada pelo controller;
- estoque, registro, entrega e financeiro são portas de saída;
- adapters de infraestrutura implementam as portas e preservam as latências simuladas;
- o caso de uso recebe todas as dependências por construtor.

O payload HTTP permanece inalterado.

## Consequências

- O domínio não instancia nem conhece clientes externos.
- Integrações podem ser substituídas por HTTP, mensageria ou mocks sem alterar o caso de uso.
- Testes de regra executam sem latência externa e verificam cada colaboração.
- Falhas, timeouts e resiliência passam a ser responsabilidade dos adapters.
