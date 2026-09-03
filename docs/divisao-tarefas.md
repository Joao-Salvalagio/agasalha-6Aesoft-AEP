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
