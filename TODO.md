# TODO.md — Escopo diferido

Este documento existe para conter o *scope creep*. Nada aqui é implementado antes
da hora, e nada é implementado sem decisão dos 3.

---

## `[E2]` — Entrega 2

Planejado, será feito na segunda entrega:

- múltiplas coleções: `abrigos`, `doadores`, `doacoes` (além de `itens`)
- `abrigos` com subdocumentos aninhados `demandas[]`
- relacionamentos entre coleções (`doacao` referencia `doadorId` + `abrigoId`;
  `item` referencia `doacaoId` e, quando casado, `abrigoId`)
- invariantes do agregado `Abrigo` / `Demanda`
- `MatchingService` — compatibilidade de tamanho e gênero, limite de estoque,
  priorização por déficit
- endpoints `/abrigos`, `/abrigos/{id}/demandas`, `/doadores`, `POST /matches`
- frontend evoluído: tela de abrigos com demandas e barra de progresso, sugestão
  de match no cadastro de item
- documentação técnica completa + diagrama de arquitetura final
- quadro de tarefas mantido (GitHub Projects) como evidência de metodologia

As regras correspondentes estão em `docs/regras-negocio.md` marcadas `[E2]`.

---

## `[fora]` — Fora de escopo (as duas entregas)

Não implementar sem decisão explícita dos 3 e ADR:

- paginação
- ordenação
- busca textual por nome
- filtros avançados além de `tamanho` / `tipoPeca` / `genero` / `status`
- `PATCH`
- validação de duplicidade
- autenticação
- autorização
- métricas / observabilidade
- versionamento da API
- auditoria
- deploy / hospedagem

As funcionalidades são priorizadas conforme o objetivo pedagógico da disciplina,
sem implementação antecipada.
