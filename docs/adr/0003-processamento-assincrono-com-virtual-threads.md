# ADR 0003 — Processamento assíncrono com virtual threads

- Status: aceito
- Data: 2026-08-13

## Contexto

As quatro integrações são independentes e bloqueantes. A entrega adiciona cinco segundos para notas com mais de cinco itens. O fluxo sequencial fazia a API somar todas as latências e degradava a resposta.

## Alternativas consideradas

1. **Spring WebFlux:** rejeitado. Os clientes atuais são bloqueantes, a API é MVC e WebFlux não reduz a latência do serviço remoto. A migração aumentaria a complexidade e só teria benefício com uma cadeia completamente reativa.
2. **Paralelizar e aguardar tudo:** reduz a soma para a maior latência, mas a resposta ainda levaria mais de cinco segundos.
3. **Despachar de forma assíncrona e paralela:** escolhido. A API publica o trabalho e retorna a nota; estoque, registro, entrega e financeiro executam simultaneamente em virtual threads.

## Decisão

Criar `PublicarIntegracoesNotaFiscalPort` e um adapter que faz fan-out em `ExecutorService` de virtual threads do Java 21. As latências simuladas continuam presentes e são executadas integralmente.

## Consequências

- Pedidos com mais de seis itens não bloqueiam a resposta HTTP pela latência de entrega.
- Virtual threads atendem bem chamadas bloqueantes sem exigir uma pilha reativa.
- Falhas externas acontecem depois da resposta e exigem monitoramento e retentativa.
- O dispatcher em memória é adequado ao desafio, mas não garante entrega após queda do processo. Em produção, este port deve ser implementado com transactional outbox e mensageria durável (por exemplo, SQS), incluindo idempotência e DLQ.
