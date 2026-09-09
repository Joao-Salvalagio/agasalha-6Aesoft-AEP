# Agasalha

Mural de doação e *matching* de agasalhos. Prova de conceito interdisciplinar —
Engenharia de Software 6S (2026.2).

## O problema e o ODS

**ODS 1 — Erradicação da Pobreza.** No inverno, população em situação de rua e
famílias em vulnerabilidade precisam de agasalho: uma necessidade básica de
proteção contra o frio. Quem quer doar não sabe o que falta; quem distribui não
tem visibilidade do que existe. O **Agasalha** registra cada peça disponível com
seus atributos (tipo, tamanho, gênero, estado de conservação) e acompanha o
ciclo de vida do item até a entrega.

## Escopo

O desenvolvimento é incremental, em duas entregas:

- **Entrega 1 (`v1.0`)** — uma coleção MongoDB (`itens`), documento homogêneo,
  CRUD REST com filtro, máquina de estados do item
  (`DISPONIVEL → RESERVADO → ENTREGUE`), frontend mínimo do mural.
- **Entrega 2 (`v2.0`)** — múltiplas coleções com relacionamento, `abrigos` com
  `demandas` aninhadas, `doadores`, `doacoes`, e o motor de *matching* que sugere
  para qual abrigo cada agasalho deve ir.

O que é de cada entrega está em `docs/regras-negocio.md`. O que está fora de
escopo está em `TODO.md`.

## Stack

Java 21 · Spring Boot 3.5.x · MongoDB · Maven (`./mvnw`) · springdoc-openapi ·
JUnit 5 + Mockito + Testcontainers · JaCoCo · Docker Compose.

## Como subir

Passo a passo completo em [`docs/setup.md`](docs/setup.md). Em resumo:

```bash
cp .env.example .env
docker compose up -d
./mvnw spring-boot:run
```

Mural em `http://localhost:8080/` · documentação da API em
`http://localhost:8080/docs`.

## Endpoints

Contrato completo em [`docs/http-api.md`](docs/http-api.md) e, de forma
interativa, em `/docs` (Swagger UI).

## Testes e cobertura

Comandos e Definition of Done em [`HARNESS.md`](HARNESS.md).

```bash
docker compose up -d
./mvnw verify
```

`./mvnw verify` roda testes unitários e de integração, gera o relatório JaCoCo em
`target/site/jacoco/index.html` e falha o build se a cobertura de linha for menor
que **70%**.

## Documentação

| Arquivo | Conteúdo |
|---|---|
| `AGENTS.md` | contrato de IA — regras que todo assistente segue |
| `CONTRIBUTING.md` | gitflow, commits, PR, regras de trabalho |
| `HARNESS.md` | comandos e Definition of Done |
| `docs/arquitetura.md` | camadas e fluxo de dados |
| `docs/decisoes.md` | registros de decisão de arquitetura (ADRs) |
| `docs/regras-negocio.md` | regras do domínio (`RN-NN`), marcadas por entrega |
| `docs/http-api.md` | contrato HTTP |
| `docs/padroes-codigo.md` | convenções de código |
| `docs/glossario.md` | termos do domínio |
| `docs/setup.md` | instalação passo a passo |
| `docs/fluxo-trabalho-ia.md` | como conduzir a IA pelas regras |

## Equipe

| Nome | RA |
|---|---|
| João Miguel Silva Salvalagio | 26014105-2 |
| Bruno Koji Fujisaki | 24094977-2 |
| Eric Delefrati Rocha Leite | 24023654-2 |

## Versão

Entrega 1 = tag `v1.0` · Entrega 2 = tag `v2.0`.

## Frontend (ADR-014)

O mural é uma aplicação **React 19 + Vite** em `apps/web`, separada da API.

> Esta seção corrige a seção **Como subir** acima: o endereço
> `http://localhost:8080/` serve a landing da API e a documentação em `/docs`.
> **O mural fica em `http://localhost:5173`.**

Com a API já rodando, em outro terminal:

```bash
cd apps/web
npm ci
npm run dev
```

Requer Node.js 20 ou superior. O Vite encaminha `/api` para a porta 8080, então
não há configuração extra no navegador. Passo a passo e troubleshooting em
[`docs/setup.md`](docs/setup.md); a arquitetura da camada web está em
[`docs/arquitetura.md`](docs/arquitetura.md).

Telas: mural com filtros (tamanho, tipo, gênero, status), cadastro, detalhes,
edição e as transições `DISPONIVEL → RESERVADO → ENTREGUE`.

## Escopo da medição de cobertura

O gate de 70% do JaCoCo mede o **código Java da API**, onde estão as regras de
negócio, a validação de domínio e a máquina de estados. É o comando reproduzível
exigido pelo edital:

```bash
./mvnw verify
```

O frontend é cliente da API e não entra nessa medição. Sua verificação de
integridade é o build:

```bash
cd apps/web && npm run build
```
