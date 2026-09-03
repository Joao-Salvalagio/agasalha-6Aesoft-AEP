# CONTRIBUTING.md — Como contribuir no Agasalha

Leia junto com `AGENTS.md` (contrato de IA), `HARNESS.md` (comandos e Definition
of Done) e `docs/fluxo-trabalho-ia.md` (como conduzir a IA).

---

## Gitflow

```
main       so releases: tags v1.0 (Entrega 1) e v2.0 (Entrega 2)
  ^
develop    integracao continua do time
  ^
<tipo>/<slug>   trabalho de uma issue, sai de develop
```

- `main` e `develop` são **protegidas**: merge só por Pull Request, com **1
  aprovação** de outro membro e **CI verde**.
- `release/entrega-1` e `release/entrega-2` saem de `develop` para congelar o
  escopo, ajustar versão e fazer o merge em `main` com a tag.

### Nome de branch

O `<tipo>` é o mesmo conjunto dos Conventional Commits, para branch e commit
falarem a mesma língua:

| `<tipo>` | Uso | Exemplo |
|---|---|---|
| `feat` | funcionalidade nova | `feat/item-crud` |
| `fix` | correção de bug | `fix/status-invalido` |
| `docs` | só documentação | `docs/atualiza-http-api` |
| `test` | só testes | `test/matching-limites` |
| `refactor` | refatoração sem mudar comportamento | `refactor/extrai-mapper` |
| `build` | build, dependências, empacotamento | `build/jacoco-90` |
| `ci` | pipeline / GitHub Actions | `ci/cache-maven` |
| `chore` | infra, configuração, tarefas de repositório | `chore/setup-inicial` |

`<slug>` em kebab-case, curto. A branch sai de `develop` e volta por PR revisado
por **outro** membro.

---

## Commits

- Padrão **Conventional Commits**: `feat:`, `fix:`, `docs:`, `test:`,
  `refactor:`, `build:`, `ci:`, `chore:`. Escopo opcional: `feat(item): ...`.
- Mensagem **enxuta**: imperativo, sem ponto final, título com no máximo 72
  caracteres. Corpo só quando agrega ("por quê", não "o quê").
- **Toda task tem no mínimo 2 commits.** Commitar conforme o avanço do trabalho,
  nunca um único "commitão" no fim. O mínimo natural: um commit com o teste, um
  com a implementação.

Exemplos bons:

```
feat(item): adiciona maquina de estados do agasalho
test(item): cobre transicoes invalidas de status
docs: registra ADR-013 sobre indice de tamanho
fix: rejeita contato vazio no cadastro de item
```

---

## Pull Request

- Um PR por issue. PR pequeno.
- Preencher o template (`.github/pull_request_template.md`).
- Antes de abrir: rodar a skill `revisao-arquitetura` e `./mvnw verify` local.
- O PR não passa sem: testes verdes, cobertura ≥ 70%, 1 aprovação de outro
  membro, CI verde, **nenhum comentário adicionado ao código**.

---

## Regras de trabalho

Estas sete regras valem para todos os colaboradores e para a IA.

1. **Versionamento é 100% manual, feito por cada contribuidor.** A IA **nunca**
   executa `git` ou `gh` — nem no Sprint 0. A IA entrega o passo a passo dos
   comandos e a mensagem de commit pronta; o dev executa.
2. **Zero comentário em código.** Nenhum `//` ou `/* */` explicativo, em nenhum
   arquivo, incluindo exemplos dentro de skills e docs.
3. **`.md` gerado durante uma sessão de desenvolvimento não sobe.**
   `docs/superpowers/**`, planejamento de sessão e notas de IA ficam locais
   (estão no `.gitignore`).
4. **Ferramenta de desenvolvimento individual não sobe.** Configuração de IDE,
   `devtools`, scripts pessoais, `.env` real — nunca versionados.
5. **É proibido reescrever ou remover conteúdo de um `.md` que já está no
   repositório.** Documentos de governança são **append-only**. É permitido
   apenas **adicionar ao fim** em `docs/decisoes.md` (novo ADR),
   `docs/regras-negocio.md` (nova `RN` marcada `[E2]`) e `docs/http-api.md`
   (contrato da Entrega 2). Qualquer outra mudança em `.md` é decisão dos 3.
6. **Toda decisão fica dentro do que já está planejado e proposto no projeto.**
   Cada dev tem liberdade de decisão individual no detalhe de implementação,
   **desde que não afete a regra de negócio nem fuja do escopo**. Mexer em regra
   de negócio, contrato de endpoint, coleção ou escopo é decisão dos 3,
   registrada (issue + ADR quando couber).
7. **Toda task tem no mínimo 2 commits.** O versionamento do código acompanha o
   avanço do desenvolvimento.

---

## Como conduzir a IA

Ver `docs/fluxo-trabalho-ia.md`. Em resumo: pegue uma issue com critério de pronto
escrito, crie a branch **você mesmo**, abra a skill certa em `.agents/skills/`,
dê à IA o contexto dos documentos relevantes (`docs/arquitetura.md`,
`docs/regras-negocio.md` pelas `RN-NN`), revise cada arquivo gerado, rode
`revisao-arquitetura` e `./mvnw verify`, e **faça você os commits e o PR**.
