# Divisão de tarefas — Agasalha

Quem faz o quê, nas duas entregas. As fatias são **verticais**: cada pessoa toca
POO + persistência/REST + testes + parte visível da sua área.

---

## Para a IA — onboarding por prompt

Quando um colaborador abrir a IA e disser algo como
**"quero fazer a AEP, sou o Bruno"** (ou João, ou Eric):

1. **Identifique a pessoa** pelo nome (mapa na seção "Integrantes" abaixo).
2. **Descubra a entrega atual:** se ainda não existe a tag `v1.0`, é a **Entrega
   1**; se existe `v1.0` mas não `v2.0`, é a **Entrega 2**. Em dúvida, pergunte.
3. **Abra a fatia dessa pessoa** na entrega atual (seções abaixo) e liste as
   tarefas dela ainda não concluídas, cruzando com as issues do board se houver.
4. **Escolha uma tarefa** com a pessoa e siga a skill `nova-feature`
   (`.agents/skills/nova-feature`) a partir dali: ler `docs/arquitetura.md` e as
   `RN-NN` de `docs/regras-negocio.md` que a tarefa cita, criar a branch (o dev
   roda o `git`), implementar na ordem da skill de implementação, revisar, PR.
5. **Nunca** rode `git`/`gh` — entregue os comandos e a mensagem de commit prontos.

A IA respeita todos os limites do `AGENTS.md`: não muda regra de negócio, escopo,
contrato de endpoint ou stack; não edita `.md` já commitado.

---

## Integrantes

| Nome | Trata como |
|---|---|
| João Miguel Silva Salvalagio | "João", "João Miguel", "Salvalagio" |
| Bruno Koji Fujisaki | "Bruno", "Koji" |
| Eric Delefrati Rocha Leite | "Eric", "Delefrati" |

---

## Entrega 1 — `v1.0` (coleção única `itens`, CRUD, máquina de estados, mural)

### João Miguel — núcleo do domínio e persistência

**Entregável**
- `model/`: `ItemAgasalho` (`@Document("itens")`) + enums `TipoPeca`, `Tamanho`,
  `Genero`, `EstadoConservacao`, `StatusItem`, `AcaoStatus`. Método de domínio
  `validar()` (RN-02, RN-03).
- `repository/ItemRepository` (`MongoRepository`) + `FiltroItem` (record de
  critérios opcionais, RN-06).
- `service/ItemService`: `criar`, `buscarPorId`, `listar(FiltroItem)` via
  `MongoTemplate`, `atualizar` (RN-05), `remover`. Exceção
  `ItemNaoEncontradoException`.
- `mapper/ItemMapper` (`@Component`): `ItemCreateRequest → ItemAgasalho`,
  `ItemUpdateRequest → ItemAgasalho`, `ItemAgasalho → ItemResponse` /
  `ItemSummaryResponse`.
- `docs/setup.md` e `docs/arquitetura.md` revisados para a Entrega 1 (o que for
  adição pontual entra como append). Config JaCoCo já está no `pom.xml`.

**Testes que escreve**
- unit do `model` e dos enums (valores, `valueOf`, `validar()` em todos os campos);
- unit do `ItemService` com `ItemRepository` e `MongoTemplate` **mockados** (CRUD
  + filtro montando a `Query` certa).

**Regras de negócio:** RN-01, RN-02, RN-03, RN-05, RN-06.

### Eric — camada REST e contrato HTTP

**Entregável**
- `controller/dto/`: `ItemCreateRequest`, `ItemUpdateRequest` (records com Bean
  Validation), `ItemResponse`, `ItemSummaryResponse`.
- `controller/ItemController`: `POST /api/itens`, `GET /api/itens` (+ query params
  `tamanho`, `tipoPeca`, `genero`, `status`), `GET /api/itens/{id}`,
  `PUT /api/itens/{id}`, `DELETE /api/itens/{id}`, `POST /api/itens/{id}/reserva`,
  `POST /api/itens/{id}/entrega`. Só protocolo HTTP, delega a `ItemService` e
  `ItemStatusService`.
- `exception/`: `ApiError` (representação tipada) + `GlobalExceptionHandler`
  (`@RestControllerAdvice`) → 400 (validação / `DadosInvalidosException`), 404
  (`ItemNaoEncontradoException`), 409 (`TransicaoInvalidaException`).
- `config/OpenApiConfiguration` (metadados do Swagger) — o starter já existe.
- **Adiciona ao fim de `docs/http-api.md`** a tabela de contrato da Entrega 1
  (método / caminho / entrada / resposta / erro) + JSON de exemplo.

**Testes que escreve**
- integração `ItemApiIT` com **MockMvc + Testcontainers-MongoDB**: cada endpoint,
  caminho feliz + erro (404, payload inválido 400, transição inválida 409).

**Regras de negócio:** todas as da Entrega 1, pela borda HTTP (contrato + status).

### Bruno — regra de estados e frontend do mural

**Entregável**
- `service/StatusTransitionService`: lógica **pura** `aplicar(StatusItem,
  AcaoStatus) → StatusItem`. Só `DISPONIVEL→RESERVADO→ENTREGUE`; qualquer outra
  combinação lança `TransicaoInvalidaException` (RN-04).
- `service/ItemStatusService`: `reservar(id)` / `entregar(id)` — carrega o item
  (404 se ausente), aplica a transição, persiste.
- Frontend em `src/main/resources/static/`: `index.html` + `app.js` + `style.css`.
  Mural que lista itens, formulário de cadastro, filtro, botões reservar/entregar
  consumindo `/api/itens`.

**Testes que escreve**
- unit **exaustivo** de `StatusTransitionService`: tabela com toda transição
  válida e toda inválida (incluindo `status` nulo);
- unit de `ItemStatusService` com repositório mockado (feliz, item inexistente,
  transição inválida).

**Regras de negócio:** RN-04.

---

## Entrega 2 — `v2.0` (múltiplas coleções, relacionamento, aninhamento, matching)

### João Miguel — agregado `Abrigo` com `demandas` aninhadas

**Entregável**
- `model/Abrigo` (`@Document("abrigos")`) com lista `List<Demanda> demandas` e
  `endereco` aninhado; `model/Demanda` (subdocumento). Invariantes: RN-08.
  Método `deficit()` (RN-09).
- `repository/AbrigoRepository`, `service/AbrigoService` (CRUD de abrigo + adicionar
  / atualizar / remover demanda), `mapper/AbrigoMapper`.
- Atualiza o seed (`scripts/seed/`) com abrigos e demandas de exemplo.
- Conduz o fechamento: `release/entrega-2`, tag `v2.0` (o dev roda o `git`).

**Testes:** unit do agregado `Abrigo`/`Demanda` (invariantes, adicionar/atualizar
demanda, cálculo de déficit).

**Regras de negócio:** RN-07, RN-08, RN-09.

### Eric — múltiplas coleções e relacionamentos

**Entregável**
- Extrai `Doador` para coleção própria; cria `Doacao` (`@Document("doacoes")`) com
  refs `doadorId` + `abrigoId`; vincula `Item` a `doacaoId` e, quando casado, a
  `abrigoId` (RN-15). Ajusta o seed.
- Endpoints REST de `/api/abrigos`, `/api/abrigos/{id}/demandas`, `/api/doadores`.
- **Adiciona ao fim de `docs/http-api.md`** o contrato da Entrega 2. Documentação
  técnica: modelo das coleções + diagrama de arquitetura final.

**Testes:** integração (Testcontainers) dos relacionamentos — criar doação com
doador + abrigo, navegar item → doação → doador, cascata de estados.

**Regras de negócio:** RN-15.

### Bruno — motor de matching

**Entregável**
- `service/MatchingService` (lógica pura): compatibilidade de tamanho (RN-10) e
  gênero (RN-11), limite de estoque (RN-12), só casa com demanda em aberto
  (RN-13), prioriza abrigo com maior déficit (RN-14).
- `POST /api/matches` (roda o matching de um item e devolve a sugestão).
- Frontend evoluído: tela de abrigos com demandas + barra de progresso; ao
  cadastrar item, mostra "match sugerido: Abrigo X".
- Monta e mantém o **quadro de tarefas** (GitHub Projects) — critério de
  metodologia da Entrega 2.

**Testes:** unit **exaustivo** do matching — tabela cobrindo tamanho igual/
diferente, `UNISSEX` nos dois lados, estoque no limite / estourando, demanda já
atendida, escolha por maior déficit.

**Regras de negócio:** RN-10 a RN-14.

---

## Carga de trabalho

Equivalente entre os três. A fatia do João é mais pesada no começo de cada
entrega (setup / agregado base), mas é trabalho mais mecânico e *front-loaded*; as
fatias de Eric e Bruno concentram a lógica mais densa (contrato HTTP, máquina de
estados, matching).

---

# Entrega 2 — redivisão após ADR-015

**Esta seção substitui a seção "Entrega 2" acima.** As fatias originais de três
pessoas ficam no documento como registro histórico do que foi planejado na
Entrega 1; o que vale para executar a Entrega 2 é o que está aqui. A troca está
registrada no ADR-015 de `docs/decisoes.md`: o João Miguel migrou para EAD depois
da tag `v1.0` e não participa da Entrega 2.

## Para a IA — onboarding por prompt na Entrega 2

Substitui os passos 1 e 3 da seção de onboarding do topo do documento. O resto
(passos 2, 4, 5 e os limites do `AGENTS.md`) continua valendo igual.

| Quem se apresenta | O que fazer |
|---|---|
| "sou o Bruno" / "Koji" | Abrir a fatia **Bruno** desta seção |
| "sou o Eric" / "Delefrati" | Abrir a fatia **Eric** desta seção |
| "sou o João" / "Salvalagio" | O João não participa da Entrega 2 (ADR-015). Avisar, e perguntar se a pessoa é o Eric ou o Bruno antes de seguir |

Antes de escolher tarefa, leia `docs/handoff-entrega-2.md` — é onde está o estado
do projeto no fim da Entrega 1, a lista de critérios do edital com dono, e a ordem
sugerida de ataque.

## Bruno — domínio do abrigo e motor de matching

**Entregável**
- `model/Abrigo` (`@Document("abrigos")`) com `List<Demanda> demandas` e `endereco`
  aninhado; `model/Demanda` (subdocumento). Invariantes: RN-08. Método `deficit()`
  (RN-09). *(vinha da fatia do João)*
- Atualiza o seed (`scripts/seed/`) com abrigos e demandas de exemplo.
  *(vinha da fatia do João)*
- `service/MatchingService` (lógica pura): compatibilidade de tamanho (RN-10) e
  gênero (RN-11), limite de estoque (RN-12), só casa com demanda em aberto
  (RN-13), prioriza abrigo com maior déficit (RN-14).
- `POST /api/matches` — roda o matching de um item e devolve a sugestão.
- Frontend evoluído em `apps/web` (ADR-014, não voltar para página estática): tela
  de abrigos com demandas e barra de progresso; ao cadastrar item, mostra "match
  sugerido: Abrigo X".
- Monta e mantém o **quadro de tarefas** (GitHub Projects) — é critério próprio de
  metodologia na Entrega 2, vale 0,1 sozinho.

**Testes que escreve**
- unit do agregado `Abrigo`/`Demanda`: invariantes, adicionar / atualizar / remover
  demanda, cálculo de déficit;
- unit **exaustivo** do matching — tabela cobrindo tamanho igual e diferente,
  `UNISSEX` nos dois lados, estoque no limite e estourando, demanda já atendida,
  escolha por maior déficit.

**Regras de negócio:** RN-08, RN-09, RN-10, RN-11, RN-12, RN-13, RN-14.

## Eric — coleções, relacionamentos e fechamento

**Entregável**
- `repository/AbrigoRepository`, `service/AbrigoService` (CRUD de abrigo +
  adicionar / atualizar / remover demanda), `mapper/AbrigoMapper` (RN-07).
  *(vinha da fatia do João)*
- Extrai `Doador` para coleção própria; cria `Doacao` (`@Document("doacoes")`) com
  refs `doadorId` + `abrigoId`; vincula `Item` a `doacaoId` e, quando casado, a
  `abrigoId` (RN-15).
- Endpoints REST de `/api/abrigos`, `/api/abrigos/{id}/demandas`, `/api/doadores`.
- **Adiciona ao fim de `docs/http-api.md`** o contrato da Entrega 2. Documentação
  técnica: modelo das coleções + diagrama de arquitetura final em
  `docs/arquitetura.md` (append).
- Conduz o fechamento: `release/entrega-2`, tag `v2.0` (o dev roda o `git`).
  *(vinha da fatia do João)*

**Testes que escreve**
- integração (Testcontainers) do `AbrigoService`: CRUD de abrigo e manipulação de
  demandas aninhadas;
- integração (Testcontainers) dos relacionamentos — criar doação com doador e
  abrigo, navegar item → doação → doador, cascata de estados.

**Regras de negócio:** RN-07, RN-15.

## Carga de trabalho

Equivalente entre os dois. O Bruno concentra a lógica densa (agregado, invariantes,
matching) e o frontend; o Eric concentra a superfície (persistência, contrato HTTP,
relacionamentos, documentação) e a condução da release. O quadro de tarefas fica
com o Bruno porque a release fica com o Eric — cada um carrega um dos dois
critérios de processo.

## Critérios do edital — quem responde por qual

O vídeo (0,2) e a cobertura (0,1) são dos dois. Os demais têm dono claro:

| Critério | Pontos | Dono |
|---|---|---|
| PoC funcional e evolução | 0,1 | os dois |
| Banco de dados NoSQL (múltiplas coleções, relacionamento, aninhados) | 0,1 | Eric (coleções e refs) + Bruno (aninhados em `abrigos`) |
| POO e qualidade do código | 0,1 | os dois, na própria fatia |
| Metodologia de trabalho (quadro de tarefas) | 0,1 | Bruno |
| Testes automatizados | 0,1 | os dois, na própria fatia |
| Cobertura ≥ 70% | 0,1 | os dois — o gate do JaCoCo já quebra o build |
| Documentação técnica | 0,1 | Eric |
| GitHub e versionamento | 0,1 | Eric (release e tag `v2.0`) |
| Vídeo de demonstração (3 a 5 min) | 0,2 | os dois, um bloco cada |

Detalhamento de cada critério, com a evidência que o avaliador procura, está em
`docs/handoff-entrega-2.md`.
