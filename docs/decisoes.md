# Decisões de arquitetura (ADRs)

Documento **append-only**. Novos ADRs entram ao fim, com número crescente. Não
reescrever nem remover ADR existente; se uma decisão for revista, adicionar um ADR
novo que a substitui e referenciar o antigo.

Formato: **Contexto** (o problema), **Decisão** (o que foi escolhido),
**Consequência** (o que isso implica).

Base de referência do projeto: `github.com/munifgebara/AepMongoJava2026` (projeto
exemplo do professor). Usado como base; cada divergência é registrada aqui.

---

## ADR-001 — Java 21

**Contexto:** a linguagem precisa demonstrar POO efetiva e ser um LTS atual.
**Decisão:** Java 21.
**Consequência:** `record`, pattern matching e virtual threads disponíveis.
Alinha com a versão da referência.

## ADR-002 — Spring Boot 3.5.x

**Contexto:** precisamos de REST, validação e persistência integrados com pouca
configuração.
**Decisão:** Spring Boot 3.5.16 (mesma linha da referência), starters
`web`, `data-mongodb`, `validation`, `actuator`.
**Consequência:** não migrar para Spring Boot 4.x nesta disciplina, para manter
paridade com o material do professor.

## ADR-003 — MongoDB, coleção única na Entrega 1

**Contexto:** o edital exige NoSQL efetivo e evolução incremental.
**Decisão:** Entrega 1 usa só a coleção `itens` (documento plano e homogêneo).
Entrega 2 introduz `abrigos`, `doadores`, `doacoes`, relacionamento entre
coleções e subdocumentos aninhados em `abrigos.demandas`.
**Consequência:** a modelagem da Entrega 1 fica simples de testar; a evolução para
a Entrega 2 é planejada e não improvisada.

## ADR-004 — Lombok contido (divergência da referência)

**Contexto:** a referência proíbe Lombok para manter tudo explícito ao aluno. O
time aceita um pouco de geração para reduzir boilerplate repetitivo.
**Decisão:** permitido `@Getter`/`@Setter` em `model`,
`@RequiredArgsConstructor` para injeção em `service`/`controller`/`mapper`,
`@Builder` em DTO e `model` quando o construtor for grande, `@Slf4j` para log.
**Proibido** `@Data` em classe `@Document`, `@AllArgsConstructor` público em
entidade, e qualquer anotação que esconda regra de negócio.
**Consequência:** menos código repetitivo; o `equals`/`hashCode` das entidades
não é gerado automaticamente (evita a pegadinha de identidade com o Mongo). Regra
registrada em `docs/padroes-codigo.md`.

## ADR-005 — Health via Actuator (divergência da referência)

**Contexto:** a referência implementa um `HealthController` manual.
**Decisão:** usar `spring-boot-starter-actuator` e expor apenas o endpoint
`health` em `/actuator/health`.
**Consequência:** menos código próprio; o health já verifica o MongoDB junto.
Configuração em `application.yml` (`management.endpoints.web.exposure.include:
health`).

## ADR-006 — Camada Mapper manual, sem MapStruct

**Contexto:** a conversão Model↔DTO precisa ser visível e testável.
**Decisão:** uma classe `Mapper` (`@Component`) por agregado, com métodos de
conversão escritos à mão. Sem MapStruct nem reflection.
**Consequência:** a conversão é explícita e isolada; a skill `revisao-arquitetura`
verifica que o `Mapper` não tem regra de negócio nem acesso a banco e preserva o
`id` no update.

## ADR-007 — Integração contínua com GitHub Actions (divergência da referência)

**Contexto:** a referência marca CI como fora de escopo. O edital pontua
versionamento e cobertura reproduzível.
**Decisão:** workflow `CI` roda `./mvnw verify` em todo push e Pull Request para
`develop` e `main`, publicando o relatório JaCoCo como artefato.
**Consequência:** cobertura e testes verdes viram pré-requisito automático de
merge. Reforça o processo de "IA guiada por gates".

## ADR-008 — Testcontainers para os testes de integração

**Contexto:** os testes de integração precisam de um MongoDB real e reprodutível.
**Decisão:** Testcontainers-MongoDB, um container por suíte, com
`@DynamicPropertySource` apontando o Spring para o container.
**Consequência:** os testes de integração exigem Docker na máquina e no runner da
CI. Não dependem de estado instalado nem de ordem de execução.

## ADR-009 — Frontend vanilla (HTML/CSS/JS)

**Contexto:** o frontend serve para demonstrar o fluxo completo, não para exibir
domínio de framework.
**Decisão:** HTML, CSS e JavaScript puro servidos como static resource do Spring.
Sem build step, sem framework de frontend.
**Consequência:** a cobertura de testes foca no backend Java. O aluno consegue
rastrear do evento de UI até a resposta da API.

## ADR-010 — Pacote `br.com.cesumar.agasalha`

**Contexto:** convenção de nomenclatura de pacote.
**Decisão:** raiz `br.com.cesumar.agasalha`, espelhando a convenção institucional
da referência.
**Consequência:** `groupId` `br.com.cesumar`, `artifactId` `agasalha`. Exclusões
do JaCoCo usam esse caminho.

## ADR-011 — Versionamento 100% manual; a IA não roda `git`/`gh`

**Contexto:** o projeto quer demonstrar uso consciente da IA guiado por regras, e
os colaboradores precisam praticar gitflow na mão.
**Decisão:** nenhum assistente de IA executa `git` ou `gh`, em nenhuma etapa
(nem `git init`). A IA entrega o passo a passo e a mensagem de commit pronta; o
colaborador executa.
**Consequência:** todo o histórico é responsabilidade humana. `docs/versionamento.md`
é o playbook de apoio.

## ADR-012 — Documentos `.md` são append-only

**Contexto:** com 3 pessoas e IA editando, documento de governança vira "terra de
ninguém" se puder ser reescrito livremente.
**Decisão:** depois de commitado, um `.md` do repositório não pode ter conteúdo
reescrito ou removido. Só é permitido **adicionar ao fim** em `docs/decisoes.md`
(novo ADR), `docs/regras-negocio.md` (nova `RN` marcada `[E2]`) e
`docs/http-api.md` (contrato da Entrega 2). Qualquer outra mudança em `.md` é
decisão dos 3.
**Consequência:** o conjunto de governança nasceu completo no Sprint 0. Correções
de rumo viram ADR novo, não edição silenciosa.

## ADR-013 — Branches são permanentes

**Contexto:** o histórico de branches faz parte do registro do processo de
desenvolvimento — quem fez o quê, em qual ramo, com qual Pull Request. Deletar
branches depois do merge apaga esse rastro.
**Decisão:** nenhuma branch é deletada, em hipótese alguma, depois do merge e do
Pull Request aceito. Todas as branches (`feat/*`, `fix/*`, `docs/*`, `test/*`,
`refactor/*`, `build/*`, `ci/*`, `chore/*`, `release/*`) são mantidas para sempre
no repositório remoto. A opção "Automatically delete head branches" do GitHub fica
**desligada**.
**Consequência:** substitui o passo "3.10 Limpeza" de `docs/versionamento.md`, que
mandava rodar `git branch -d` e `git push origin --delete` após o merge — esse
passo não vale mais. Depois do merge, o fluxo é apenas sincronizar o `develop`
local (`git checkout develop && git pull origin develop`); a branch mergeada
permanece no remoto.
