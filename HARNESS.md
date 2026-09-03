# HARNESS.md — Comandos e Definition of Done

Complementa o `AGENTS.md` com os comandos exatos de validação e o critério do que
é "pronto". Todo comando usa o Maven Wrapper (`./mvnw`), para todos rodarem a
mesma versão sem instalar Maven.

---

## Pré-requisitos

- Java 21 (Temurin recomendado)
- Docker + Docker Compose v2
- Git

O `./mvnw` já vem no repositório. No Windows use `mvnw.cmd`; no Git Bash / Linux /
macOS use `./mvnw`.

---

## Comandos

### Build e teste

| Objetivo | Comando |
|---|---|
| Compilar sem testes | `./mvnw clean compile -DskipTests` |
| Rodar testes unitários e de controller | `./mvnw clean test` |
| Rodar um teste específico | `./mvnw -Dtest=ItemServiceTest test` |
| Rodar os testes de integração | `./mvnw -Dtest=ItemApiIT test` |
| Verificação completa + relatório de cobertura | `./mvnw clean verify` |

`./mvnw verify` roda tudo, gera o relatório JaCoCo e **falha o build se a
cobertura de linha for menor que 70%**.

### Runtime

| Objetivo | Comando |
|---|---|
| Subir a aplicação | `./mvnw spring-boot:run` |
| Subir a infraestrutura | `docker compose up -d` |
| Ver os containers | `docker compose ps` |
| Parar os containers | `docker compose down` |

---

## Endpoints de verificação

| URL | O que é |
|---|---|
| `http://localhost:8080/` | mural (frontend) |
| `http://localhost:8080/docs` | Swagger UI |
| `http://localhost:8080/v3/api-docs` | contrato OpenAPI em JSON |
| `http://localhost:8080/actuator/health` | saúde da aplicação (`{"status":"UP"}`) |
| `http://localhost:8081/` | mongo-express (inspeção das coleções) |

---

## Cobertura

- Relatório: `target/site/jacoco/index.html`
- Mínimo: **70% de linha** (o build quebra abaixo disso)
- Excluídos da contagem: `AgasalhaApplication`, pacotes `dto`, pacote `config`
- Passo a passo reproduzível: `docs/setup.md`

---

## Definition of Done

Uma task só está pronta quando:

- [ ] compila (`./mvnw clean compile`)
- [ ] os testes aplicáveis passam (`./mvnw verify`)
- [ ] há teste novo ou atualizado para todo comportamento novo ou corrigido
- [ ] cobertura de linha ≥ 70%
- [ ] nenhum import não usado
- [ ] **nenhum comentário no código**
- [ ] contrato HTTP preservado, ou `docs/http-api.md` estendido ao fim
- [ ] `docs/` continua coerente com o código
- [ ] limitações de ambiente estão explicadas no PR (ex: "não rodei X porque Y")
- [ ] a task tem **pelo menos 2 commits** em Conventional Commits
- [ ] a skill `revisao-arquitetura` foi executada
- [ ] outro membro revisou o PR e a CI está verde

Não declarar "pronto" com teste vermelho ou sem explicar uma limitação de
ambiente.
