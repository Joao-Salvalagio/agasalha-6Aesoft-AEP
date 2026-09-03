# Fluxo de trabalho com IA

*A IA acelera a digitação, não substitui a decisão. Toda regra que a IA deve
seguir está num arquivo versionado. Se não está escrito, não é regra.*

Isto não é *vibe coding*: é uso consciente da IA, guiado pelas regras de negócio
(`docs/regras-negocio.md`), pela arquitetura (`docs/arquitetura.md`) e pelos
padrões (`docs/padroes-codigo.md`).

---

## Começando: diga quem você é

Abra sua IA e diga, por exemplo:

> "Quero fazer a AEP. Sou o Bruno."

A IA deve então:

1. reconhecer você em `docs/divisao-tarefas.md` (seção "Integrantes");
2. identificar a entrega atual (sem tag `v1.0` → Entrega 1; com `v1.0` e sem
   `v2.0` → Entrega 2);
3. abrir a sua fatia dessa entrega e listar o que ainda falta;
4. escolher uma tarefa com você e seguir a skill `nova-feature`.

Se a IA não fizer isso, aponte: "leia `docs/divisao-tarefas.md` e `AGENTS.md`".

---

## Os 6 passos de uma tarefa

```
1. PEGAR       uma issue do board (escopo fechado, criterio de pronto escrito)
2. RAMO        VOCE roda:  git checkout -b <tipo>/<slug>   (a partir de develop)
3. GUIAR       abrir a skill certa (.agents/skills/) e dar o contexto:
               "siga docs/arquitetura.md e docs/regras-negocio.md RN-XX..RN-YY"
4. IMPLEMENTAR a IA gera o codigo NA ORDEM da skill; VOCE revisa cada arquivo
5. REVISAR     rodar a skill revisao-arquitetura e ./mvnw verify localmente
6. VERSIONAR   VOCE faz os commits (>= 2, Conventional Commits enxuto que a IA
   + PR        sugeriu), push, abre o PR; OUTRO membro revisa; CI verde; merge
```

Comandos de cada passo de versionamento: `docs/versionamento.md`.

---

## Como dar contexto bom para a IA

A IA rende conforme o contexto que recebe. Um bom prompt de tarefa cita:

- **o que** fazer (o entregável da issue);
- **onde** as regras estão (`docs/regras-negocio.md` pelas `RN-NN`, não "as regras
  de negócio" no vago);
- **qual skill** usar (`spring-crud` para um recurso REST, `testing` para testes);
- **os limites** (não mudar contrato, não criar coleção, não editar `.md`).

### Prompt ruim

> "cria o CRUD de item pra mim"

Vago: a IA vai inventar estrutura, nomes, validação e testes sem ancoragem.

### Prompt bom

> "Implemente `ItemService` (create/read/update/delete + `listar(FiltroItem)`).
> Siga a ordem da skill `spring-crud`, a arquitetura de `docs/arquitetura.md` e as
> regras RN-01, RN-02, RN-03, RN-05, RN-06 de `docs/regras-negocio.md`. Repositório
> e `MongoTemplate` mockados nos testes unitários. Não mexa no contrato HTTP."

---

## Quando a IA sugere algo fora das regras

Acontece: a IA propõe uma dependência nova, uma coleção extra, um endpoint com
outro formato, ou "vou só ajustar esse `.md`".

**Pare.** Não aceite na hora. Registre a proposta na issue e leve para os 3. Se o
time concordar:

- mudança de stack ou de arquitetura → **novo ADR** ao fim de `docs/decisoes.md`;
- nova regra de negócio → **nova `RN`** marcada `[E2]` ao fim de
  `docs/regras-negocio.md`;
- novo contrato → **adição** ao fim de `docs/http-api.md`.

Nunca por edição silenciosa de um `.md` já commitado.

---

## O que a IA nunca faz

- rodar `git` ou `gh` (nem `git init`, nem commit, nem push, nem PR);
- decidir sozinha trocar stack, criar coleção, mudar contrato de endpoint ou
  regra de negócio;
- pular teste ou baixar o limite de cobertura;
- adicionar comentário ao código;
- editar um `.md` que já está no repositório.
