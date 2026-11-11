# Handoff — Entrega 2

Documento de passagem de bastião. O João Miguel migrou o curso para EAD depois do
fechamento da Entrega 1 e não participa da Entrega 2; a fatia dele foi redividida
entre o Bruno e o Eric. A decisão está no **ADR-015** (`docs/decisoes.md`) e a
divisão nova está na seção **"Entrega 2 — redivisão após ADR-015"** de
`docs/divisao-tarefas.md`.

Leia este arquivo inteiro uma vez antes de começar. Ele existe para que nenhum
critério do edital seja perdido por esquecimento.

---

## 1. De onde vocês estão partindo

A Entrega 1 está fechada e marcada. Nada precisa ser refeito.

| Item | Estado |
|---|---|
| Tag da Entrega 1 | `v1.0` → commit `a36ada4` = `main` |
| `develop` | `4aa9898` |
| `release/entrega-1` | preservada (ADR-013: branch nunca é deletada) |
| Cobertura | **97,5% de linha** / 86,5% de branch |
| Testes de integração | 17, com Testcontainers |
| CI | 21/21 verde |
| Build do frontend | `tsc -b && vite build` limpo |
| Vídeo da Entrega 1 | gravado e publicado |

A margem de cobertura é grande (97,5% contra um mínimo de 70%), mas ela **cai** à
medida que código novo entra sem teste. O gate do JaCoCo está em 70% de linha e
quebra o build — se o `./mvnw verify` falhar por cobertura, é isso.

## 2. Como subir o projeto

Detalhe completo em `docs/setup.md`. O resumo, nesta ordem:

```
docker compose up -d
./mvnw spring-boot:run
cd apps/web && npm run dev
```

Mural em `localhost:5173`, API em `localhost:8080`, Swagger em `/docs`,
mongo-express pelo compose. Para voltar ao seed original:
`docker compose down --volumes && docker compose up -d`.

## 3. Pendência herdada da Entrega 1

**Bug cosmético nos selects do frontend.** O gatilho do select mostra o enum cru
(`USADO_BOM`, `CASACO`, `DISPONIVEL`) em vez do rótulo amigável. A tela de detalhes
está correta, ou seja, o mapa de rótulos existe e só não está sendo aplicado no
`SelectValue`. É pequeno e aparece na tela durante a gravação do vídeo — vale
corrigir cedo, não na véspera.

## 4. O que a Entrega 2 cobra

### Entregáveis (seção 6 do edital)

- Código-fonte atualizado no GitHub.
- Documentação técnica completa do projeto.
- Testes automatizados executáveis, com cobertura mínima de 70%.
- Vídeo de demonstração de **3 a 5 minutos**.

### Requisito técnico do semestre (seção 8)

A interpretação já confirmada pelo time: no PDF, "1º semestre" = Entrega 1 e
"2º semestre" = Entrega 2. Logo a Entrega 2 **precisa** ter:

- múltiplas coleções (`abrigos`, `doadores`, `doacoes`, além de `itens`);
- relacionamento entre coleções;
- pelo menos uma coleção com objetos complexos — documentos aninhados ou listas de
  subdocumentos (é o `demandas[]` dentro de `abrigos`).

Se algum dos três faltar, o critério de NoSQL cai. É o requisito estrutural da
entrega inteira.

### Os nove critérios, com a evidência que o avaliador procura

| # | Critério | Pts | Evidência esperada | Dono |
|---|---|---|---|---|
| 1 | PoC funcional e evolução | 0,1 | Fluxos principais executam e há evolução **verificável** em relação à Entrega 1 | os dois |
| 2 | Banco de dados NoSQL | 0,1 | Modelagem atende integralmente à seção 8: múltiplas coleções + relacionamento + aninhados | Eric + Bruno |
| 3 | POO e qualidade do código | 0,1 | Responsabilidades bem definidas, legibilidade, sem duplicação ou acoplamento desnecessário | os dois |
| 4 | Metodologia de trabalho | 0,1 | **Quadro de tarefas** | Bruno |
| 5 | Testes automatizados | 0,1 | Conjunto pertinente, executável, cobrindo comportamento relevante | os dois |
| 6 | Cobertura ≥ 70% | 0,1 | Relatório ou comando reproduzível. **Abaixo de 70% este item vale 0** | os dois |
| 7 | Documentação técnica | 0,1 | README e docs permitem compreender, instalar, executar e testar — incluindo tecnologias e **estrutura do banco** | Eric |
| 8 | GitHub e versionamento | 0,1 | Repositório organizado, histórico compatível com a evolução, **versão final claramente identificável** (tag `v2.0`) | Eric |
| 9 | Vídeo de demonstração | 0,2 | Evolução, arquitetura final, execução da PoC, tecnologias, testes e cobertura, principais resultados — **dentro do tempo** | os dois |

### Três armadilhas que custam ponto

1. **O quadro de tarefas precisa existir desde o começo.** Ele é evidência de
   *metodologia de trabalho*, não um print tirado na última hora. Um board criado
   no dia da entrega, com tudo movido para "Done" de uma vez, mostra o contrário
   do que o critério pede. Crie na primeira semana e mova os cartões conforme o
   trabalho acontece.
2. **"Dentro do tempo previsto" está escrito na rubrica do vídeo.** São 0,2, o
   maior critério isolado da entrega. 3 a 5 minutos, cronometrado — não estimado.
3. **O vídeo da Entrega 2 cobra tecnologias, testes e cobertura**, que eram
   proibidos de gastar tempo no vídeo da Entrega 1. É a maior diferença entre os
   dois roteiros. Guardem o print do JaCoCo.

## 5. Ordem sugerida de ataque

A ordem não é opcional nos três primeiros passos — há dependência real de código.

| Ordem | Quem | O quê | Por quê nesta posição |
|---|---|---|---|
| 0 | Bruno | Criar o quadro de tarefas com as tarefas abaixo | É evidência de processo; precisa existir antes do trabalho, não depois |
| 1 | Bruno | `model/Abrigo` + `model/Demanda`, invariantes (RN-08), `deficit()` (RN-09) | Tudo depende do agregado; ele não depende de nada |
| 2 | Eric | `AbrigoRepository`, `AbrigoService`, `AbrigoMapper` (RN-07) | Depende do model do passo 1 |
| 3 | Eric | Endpoints `/api/abrigos` e `/api/abrigos/{id}/demandas` | Depende do service do passo 2 |
| 4 | Eric | `Doador` e `Doacao` em coleções próprias, refs (RN-15) | Independente dos passos 1–3; pode correr em paralelo |
| 5 | Bruno | `MatchingService` (RN-10 a RN-14) | Depende de `deficit()` do passo 1 |
| 6 | Bruno | `POST /api/matches` | Depende dos passos 2 e 5 |
| 7 | Bruno | Seed com abrigos e demandas | Precisa do model; o frontend e a demo do vídeo dependem dele |
| 8 | Bruno | Frontend: tela de abrigos com progresso, match sugerido no cadastro | Depende dos endpoints dos passos 3 e 6 |
| 9 | Eric | `http-api.md` (append) + `arquitetura.md` com diagrama final (append) | Documenta o que já existe |
| 10 | os dois | Gravar o vídeo | Precisa de tudo pronto e do seed limpo |
| 11 | Eric | `release/entrega-2` → `main`, tag `v2.0` | Último passo, sempre |

Rodem `./mvnw verify` antes de cada Pull Request. O gate de cobertura é o primeiro
a reclamar.

## 6. Regras do projeto que continuam valendo

Estão em `AGENTS.md` e `CONTRIBUTING.md`. As que mais pegam no dia a dia:

- **A IA nunca roda `git` ou `gh`.** Ela escreve arquivo e entrega o comando e a
  mensagem de commit prontos; quem executa é o dev.
- **Zero comentário em código.**
- **`.md` de governança é append-only** (ADR-012). Não reescreva linha de
  `decisoes.md`, `regras-negocio.md`, `http-api.md`, `divisao-tarefas.md` — só
  adicione ao fim. Correção de rumo vira ADR novo.
- **Toda task tem no mínimo 2 commits** (regra 7). Versione conforme o avanço.
- **Branch nunca é deletada** (ADR-013). Depois do merge:
  `git checkout develop && git pull origin develop`.
- **`main` só recebe release + tag** (regra 9). Nada de merge direto de `develop`.
- **Decisão fora do que está planejado é decisão dos três** — agora dos dois. Mexer
  em regra de negócio, contrato de endpoint, coleção ou escopo não é decisão
  individual, e vira ADR.
- **`TODO.md` contém o que está deliberadamente fora de escopo.** Paginação,
  ordenação, busca textual, `PATCH`, autenticação, deploy e o resto da lista não
  entram na Entrega 2. Não é esquecimento, é decisão registrada.

## 7. O vídeo da Entrega 2

3 a 5 minutos, dois narradores. O roteiro pronto está em
`docs/roteiro-video-entrega-2.md`, no mesmo formato do roteiro da Entrega 1 — bloco
cronometrado por pessoa, o que falar, o que deixar na tela, e o checklist de antes
de enviar. Ele está versionado justamente para vocês poderem ajustar os tempos
depois do primeiro ensaio.

O vídeo vai para o **YouTube** como **"Não listado"** (não "Privado" — privado o
avaliador não abre). Testem o link em janela anônima antes de entregar.

## 8. Ficha de identificação (seção 11 do edital)

O PDF da Entrega 2 já está montado no modelo da UniCesumar — mesma fonte, mesmas
cores e mesmo logo do PDF original do edital — com o campo do link do vídeo em
branco: `AEP_Entrega2_Agasalha.pdf`, na raiz do repositório. O fonte é
`entrega-2-identificacao.html`, na mesma pasta: editem o HTML, colem o link do
vídeo e gerem o PDF de novo por Ctrl+P → "Salvar como PDF" (margens "Padrão",
desmarcar "Cabeçalhos e rodapés").

Os dois arquivos estão **versionados de propósito**. O roteiro e os PDFs da
Entrega 1 nasceram na área de trabalho do João, que sai do time — o que ficar só
lá se perde.

**Falta decidir:** se o RA do João continua na ficha da Entrega 2. Ele não
participou da entrega, então a decisão é de vocês com a coordenação. Os três RAs
estão preenchidos; apaguem a linha se for o caso.

## 9. Onde está cada coisa

| Preciso de… | Está em |
|---|---|
| Quem faz o quê | `docs/divisao-tarefas.md`, seção "Entrega 2 — redivisão após ADR-015" |
| Por que está assim | `docs/decisoes.md` (ADR-001 a ADR-015) |
| Regras de negócio RN-07 a RN-15 | `docs/regras-negocio.md`, marcadas `[E2]` |
| O que está fora de escopo | `TODO.md` |
| Como rodar e testar | `docs/setup.md` |
| Camadas e diagrama | `docs/arquitetura.md` |
| Contrato dos endpoints | `docs/http-api.md` |
| Padrão de código | `docs/padroes-codigo.md` |
| Fluxo de branch, commit e PR | `docs/versionamento.md` e `CONTRIBUTING.md` |
| Limites da IA | `AGENTS.md` e `docs/fluxo-trabalho-ia.md` |
| Skill de nova feature | `.agents/skills/nova-feature` |

Boa entrega.
