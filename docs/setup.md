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
