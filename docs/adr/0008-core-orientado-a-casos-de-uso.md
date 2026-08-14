# ADR 0008 — Core orientado a casos de uso

- Status: aceito
- Data: 2026-08-14

## Contexto

A primeira separação hexagonal isolou as integrações por portas e adapters, mas manteve o domínio em pacotes genéricos como `model` e `service`. Essa organização ainda misturava a linguagem do framework com a intenção da aplicação e permitia que regras de negócio dependessem da configuração do Spring.

## Decisão

Organizar a aplicação a partir do núcleo de negócio:

- `core/model` permanece como núcleo do domínio;
- `usecase` contém a porta de entrada e a implementação de cada caso de uso;
- `usecase/calculo` contém operações de negócio usadas pelo caso de uso;
- `usecase/strategy` contém as estratégias tributárias e de frete;
- `port/out` define os contratos que o núcleo exige das integrações;
- `adapter/in` recebe requisições e chama casos de uso;
- `adapter/out` implementa as integrações externas;
- `config/UseCaseConfig` é o composition root que converte propriedades externas em valores da aplicação e monta as dependências.

O core não recebe `TaxasProperties` nem conhece casos de uso, observabilidade, ports ou adapters. A camada `usecase` recebe `BigDecimal` e `FaixasAliquota`, tipos independentes do Spring. Testes arquiteturais protegem a direção dessas dependências.

## Consequências

- A nomenclatura passa a expressar ações de negócio em vez de serviços genéricos.
- Modelos e regras podem ser testados sem iniciar o Spring.
- A configuração por variável de ambiente permanece na borda da aplicação.
- Adapters e mecanismos de entrega podem mudar sem alterar o caso de uso.
- A composição explícita adiciona código em `UseCaseConfig`, em troca de dependências visíveis e controladas.
