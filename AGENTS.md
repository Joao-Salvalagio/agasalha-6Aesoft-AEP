# AGENTS.md — Contrato de IA do Agasalha

Este é o documento que **todo assistente de IA lê antes de tocar em qualquer
arquivo** deste repositório. Ele vale para os 3 colaboradores (João Miguel, Eric,
Bruno) e para qualquer ferramenta de IA que eles usem.

Princípio: *a IA acelera a digitação, não substitui a decisão. Toda regra que a IA
deve seguir está num arquivo versionado. Se não está escrito, não é regra.*

---

## 1. Missão e escopo

**Agasalha** é uma PoC (prova de conceito) de um mural de doação e *matching* de
agasalhos, alinhada ao **ODS 1 — Erradicação da Pobreza**: agasalho é proteção
contra o frio, uma necessidade básica de quem vive em situação de pobreza ou de
rua.

O desenvolvimento tem duas entregas incrementais:

- **Entrega 1 (`v1.0`)** — uma única coleção MongoDB (`itens`), documento
  homogêneo, CRUD REST, máquina de estados do item, frontend mínimo do mural.
- **Entrega 2 (`v2.0`)** — múltiplas coleções com relacionamento, `abrigos` com
  subdocumentos `demandas`, `doadores`, `doacoes`, motor de *matching*.

O que é de cada entrega está marcado `[E1]` / `[E2]` em `docs/regras-negocio.md`.
O que está **fora de escopo** está em `TODO.md` — não implementar nada de lá sem
decisão dos 3.

---

## 2. Stack fixa

| Item | Escolha |
|---|---|
| Linguagem | Java 21 |
| Framework | Spring Boot 3.5.x |
| Build | Maven via `./mvnw` (Maven Wrapper) |
| Configuração | `application.yml` |
| Banco | MongoDB (Docker Compose) + mongo-express |
| Acesso a dados | `MongoRepository` (CRUD) + `MongoTemplate` (filtros e *matching*) |
| Contrato Model↔DTO | camada `mapper` dedicada (`@Component`), mapeamento manual |
| Documentação de API | springdoc-openapi → Swagger UI em `/docs` |
| Health | `spring-boot-starter-actuator` → `/actuator/health` |
| Boilerplate | Lombok **contido** (ver seção 4) |
| Testes | JUnit 5 + Mockito + MockMvc + Testcontainers-MongoDB |
| Cobertura | JaCoCo, `./mvnw verify` falha abaixo de **70%** de linha |
| Frontend | HTML + CSS + JS puro, servido como static resource |
| Pacote raiz | `br.com.cesumar.agasalha` |

**Não adicionar, trocar ou remover dependência sem antes registrar um ADR ao fim
de `docs/decisoes.md`** — e isso é decisão dos 3, não da IA.

---

## 3. Arquitetura obrigatória

Detalhe completo em `docs/arquitetura.md`. Resumo que a IA precisa respeitar:

**Fluxo de entrada:** `HTTP → Controller → Request DTO → Service → Repository → MongoDB`

**Fluxo de saída:** `MongoDB → Model → Service → Mapper → Response DTO → Controller → JSON`

Regras:

- **Nenhuma classe `@Document` é serializada numa resposta HTTP.** Sempre passa
  por um `Response DTO` construído pelo `mapper`.
- **Toda regra de negócio mora no `service`.** `controller` só faz protocolo HTTP;
  `repository` só persiste; `mapper` só converte; `model` guarda estado e
  invariantes de domínio.
- **Injeção por construtor** (`@RequiredArgsConstructor` ou construtor explícito).
  Nunca `@Autowired` em campo.
- **Um `Request DTO` por caso de uso.** POST e PUT podem ter contratos diferentes.
- **Erro via `@RestControllerAdvice`**, com representação tipada e consistente
  (`ApiError`). Sem `try/catch` de fluxo normal no `controller`.

---

## 4. Regras de código

Detalhe em `docs/padroes-codigo.md`. O essencial:

- **Idioma:** identificadores e mensagens em português.
- **Zero comentário.** Nenhum `//` ou `/* */` explicativo em nenhum arquivo. Se o
  código precisa de comentário, o nome está ruim ou o método é grande demais.
- `record` para todo DTO.
- **Lombok contido.** Permitido: `@Getter` / `@Setter` em `model`;
  `@RequiredArgsConstructor` em `service` / `controller` / `mapper`; `@Builder`
  em DTO e `model` quando o construtor ficar grande; `@Slf4j` para logging.
  **Proibido:** `@Data` em classe `@Document`; `@AllArgsConstructor` público em
  entidade; qualquer anotação Lombok que esconda regra de negócio.
- Um tipo público por arquivo. Import sem wildcard.
- Nome de teste: `metodo_situacao_resultadoEsperado`.
- Sem interface artificial (só extrair quando houver 2+ implementações reais). Sem
  MapStruct. Sem abstração "por precaução" (YAGNI).

---

## 5. Testes

- **Toda mudança de comportamento tem teste.** Ver `.agents/skills/testing`.
- Unitário para regra de `service` / domínio (sem Spring, sem Mongo, Mockito).
- Controller com `MockMvc` e `service` mockado para o contrato HTTP.
- Integração com Testcontainers para o fluxo completo.
- **Antes de abrir PR:** `./mvnw verify` verde e cobertura de linha ≥ 70%.

---

## 6. Workflow — 6 passos por tarefa

Detalhe em `docs/fluxo-trabalho-ia.md`.

```
1. PEGAR       uma issue do board (escopo fechado, critério de pronto escrito)
2. RAMO        o DEV roda: git switch -c feature/<area>-<slug>  (a partir de develop)
3. GUIAR       abrir a skill certa (.agents/skills/) e dar o contexto:
               "siga docs/arquitetura.md e docs/regras-negocio.md RN-XX..RN-YY"
4. IMPLEMENTAR a IA gera o codigo NA ORDEM da skill; o DEV revisa cada arquivo
5. REVISAR     rodar a skill revisao-arquitetura e ./mvnw verify localmente
6. VERSIONAR   o DEV faz os commits (>= 2, Conventional Commits enxuto), push e PR;
   + PR        OUTRO membro revisa; CI verde; merge
```

---

## 7. Liberdade × limites

**A IA e o dev têm liberdade** em detalhe de implementação: nome de classe
auxiliar, estrutura interna de um método, ordem local de validações, escolha de
uma coleção Java, formato de uma mensagem.

**A IA NÃO decide sozinha** (é decisão dos 3, registrada):

- trocar, adicionar ou remover dependência ou tecnologia da stack;
- criar coleção nova ou mudar o formato de um documento;
- mudar o contrato de um endpoint (caminho, verbo, formato de request/response,
  código de status);
- alterar uma regra de negócio (`RN-NN`);
- pular ou enfraquecer um teste, ou baixar o limite de cobertura;
- mexer no gitflow ou nas regras de branch;
- **editar um arquivo `.md` que já está no repositório** (ver seção 8).

Se a IA perceber que a solução exige uma dessas coisas: **parar, explicar, e
deixar a decisão para os 3.**

---

## 8. Versionamento e documentos

- **A IA nunca executa `git` ou `gh`.** Nem `git init`, nem commit, nem push, nem
  PR, nem proteção de branch. A IA entrega o passo a passo dos comandos e a
  **mensagem de commit pronta** (Conventional Commits, enxuta). **O dev executa.**
- **Toda task tem no mínimo 2 commits.** Versionar conforme o avanço.
- **Documento `.md` já commitado é append-only.** É proibido reescrever ou remover
  conteúdo. É permitido **adicionar ao fim** apenas em: `docs/decisoes.md` (novo
  ADR), `docs/regras-negocio.md` (nova `RN` marcada `[E2]`), `docs/http-api.md`
  (contrato da Entrega 2). Qualquer outra mudança em `.md` é decisão dos 3.
- **Não versionar:** `docs/superpowers/**`, planejamento de sessão, notas de IA,
  configuração de IDE, `devtools`, `.env`, o edital, `target/`.
