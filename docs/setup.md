# Setup — do zero à API rodando

## Pré-requisitos

| Ferramenta | Versão | Verificar |
|---|---|---|
| Java | 21 | `java -version` |
| Docker | recente | `docker version` |
| Docker Compose | v2 | `docker compose version` |
| Git | recente | `git --version` |

Maven **não** precisa estar instalado — o repositório traz o Maven Wrapper
(`./mvnw`, ou `mvnw.cmd` no Windows), que baixa a versão certa na primeira
execução.

## 1. Clonar

```bash
git clone https://github.com/Joao-Salvalagio/agasalha-6Aesoft-AEP.git agasalha
cd agasalha
```

## 2. Variáveis de ambiente

```bash
cp .env.example .env
```

O `.env` define as portas e o nome do banco usados pelo `compose.yaml`. Os valores
padrão servem para desenvolvimento local.

## 3. Subir a infraestrutura

```bash
docker compose up -d
```

Isso sobe três serviços:

| Serviço | Porta | Para quê |
|---|---|---|
| `mongo` | `27017` | banco de dados |
| `mongo-express` | `8081` | inspeção visual das coleções |
| `mongo-seed` | — | carrega dados de exemplo em `itens` e encerra |

Conferir: `docker compose ps` (o `mongo` deve estar `healthy`; o `mongo-seed` sai
com código 0). Abrir `http://localhost:8081` mostra o banco `agasalha` com a
coleção `itens` populada.

## 4. Rodar a aplicação

```bash
./mvnw spring-boot:run
```

| URL | O que é |
|---|---|
| `http://localhost:8080/` | mural |
| `http://localhost:8080/docs` | Swagger UI |
| `http://localhost:8080/v3/api-docs` | contrato OpenAPI (JSON) |
| `http://localhost:8080/actuator/health` | `{"status":"UP"}` |

## 5. Rodar os testes e ver a cobertura

```bash
docker compose up -d
./mvnw clean verify
```

`verify` roda testes unitários e de integração (Testcontainers sobe um MongoDB
próprio, separado do Compose), gera o relatório JaCoCo e **falha o build se a
cobertura de linha for menor que 70%**.

Relatório: abrir `target/site/jacoco/index.html`. A linha **Total**, coluna
**Lines / Cov.**, é a evidência de cobertura.

## 6. Parar tudo

```bash
docker compose down
```

Os dados ficam no volume `mongo-data` e sobrevivem ao `down`. Para apagar também
os dados: `docker compose down --volumes`.

## Troubleshooting

| Sintoma | Causa provável | Solução |
|---|---|---|
| `port is already allocated` | 27017, 8080 ou 8081 em uso | mudar a porta no `.env` ou parar o processo que a ocupa |
| `Cannot connect to the Docker daemon` | Docker Desktop parado | abrir o Docker Desktop e esperar iniciar |
| primeira execução de teste muito lenta | Testcontainers baixando a imagem `mongo:7` | esperar; execuções seguintes usam o cache |
| `./mvnw` não executa no Windows | shell errado | usar `mvnw.cmd` no PowerShell/CMD, ou `./mvnw` no Git Bash |
| app sobe mas `/actuator/health` dá 404 | `application.yml` sem o `management.endpoints` | conferir a configuração do actuator |

## Frontend — `apps/web` (ADR-014)

Adendo às seções numeradas acima. O mural é uma aplicação React separada, com
runtime próprio. A API sobe pelos passos 1 a 4; o mural sobe por aqui.

### Pré-requisito adicional

| Ferramenta | Versão | Verificar |
|---|---|---|
| Node.js | 20 ou superior | `node --version` |
| npm | acompanha o Node | `npm --version` |

### Instalar e rodar

Com a API já rodando na porta 8080 (passo 4), em **outro terminal**:

```bash
cd apps/web
npm ci
npm run dev
```

Mural em `http://localhost:5173`.

`npm ci` instala exatamente as versões do `package-lock.json` — é o comando certo
para reproduzir o ambiente. Use `npm install` apenas quando for alterar
dependências de propósito.

O Vite encaminha as chamadas de `/api` para `http://localhost:8080`, configurado
em `apps/web/vite.config.ts`. Não é preciso configurar nada no navegador.

### Gerar o build de produção

```bash
cd apps/web
npm run build
```

Compila o TypeScript e gera `apps/web/dist/`. Serve como verificação de que o
frontend está íntegro, mesmo sem publicar em lugar nenhum.

### Fluxo completo para demonstração

Três terminais, nesta ordem:

```bash
docker compose up -d
./mvnw spring-boot:run
cd apps/web && npm run dev
```

| Endereço | O que é |
|---|---|
| `http://localhost:5173` | mural (React) |
| `http://localhost:8080/docs` | documentação interativa da API |
| `http://localhost:8080/` | landing da API |
| `http://localhost:8081` | mongo-express |

### Troubleshooting do frontend

| Sintoma | Causa provável | Solução |
|---|---|---|
| mural carrega mas lista fica vazia e o console acusa erro de rede | API não está rodando na 8080 | subir a API (passo 4) antes do `npm run dev` |
| `EJSONPARSE` ou erro no `npm ci` | `node_modules` corrompido ou Node antigo | apagar `apps/web/node_modules` e conferir `node --version` |
| porta 5173 ocupada | outra instância do Vite aberta | fechar a outra instância; o Vite oferece a 5174 automaticamente |
| erro de CORS no navegador | mural aberto por endereço fora da lista permitida | usar `http://localhost:5173`, ou incluir a origem em `cors.origens-permitidas` |
