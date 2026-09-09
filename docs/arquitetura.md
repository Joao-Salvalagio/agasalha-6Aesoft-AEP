# Arquitetura

Arquitetura em camadas simples, com responsabilidades visíveis. As páginas
estáticas são clientes da API e não acessam persistência.

```
Navegador (HTML/CSS/JavaScript) --fetch--> Controller
```

`index.html` é o mural. O JavaScript faz as chamadas HTTP para a API; nunca fala
com o MongoDB direto.

## Fluxo de entrada

```
HTTP
  |
Controller
  |
Request DTO
  |
Service
  |
Repository
  |
MongoDB
```

O `Controller` recebe a requisição, valida o `Request DTO` (`@Valid`) e delega o
caso de uso ao `Service`. O `Service` coordena regras e persistência. O
`Repository` é a fronteira de acesso ao MongoDB.

## Fluxo de saída

```
MongoDB
  |
Model
  |
Service
  |
Mapper
  |
Response DTO
  |
Controller
  |
JSON
```

O MongoDB devolve um `Model` de persistência. O `Service` seleciona o resultado do
caso de uso e o `Mapper` converte em um `Response DTO`. O `Controller` transforma
esse contrato em JSON e atribui o status HTTP adequado.

## Responsabilidades

| Camada | Faz | Não faz |
|---|---|---|
| `controller` | protocolo HTTP, validação da entrada, códigos de resposta | regra de negócio, acesso a `Repository`, `try/catch` de fluxo normal |
| `dto` (record) | contrato de um caso de uso (Request) / formato público (Response) | lógica, persistência |
| `mapper` (`@Component`) | conversão explícita Model↔DTO, preserva `id` no update | acesso a banco, regra de negócio |
| `service` | casos de uso e **todas** as regras de negócio | protocolo HTTP |
| `repository` | persistência e consultas | regra de negócio |
| `model` (`@Document`) | estrutura persistida no MongoDB + invariantes de domínio | cruzar a fronteira HTTP |
| `exception` | falhas de domínio + representação consistente de erro (`ApiError`) | — |
| `config` | integração com infraestrutura, carga de dados de ambiente | regra de negócio |
| páginas estáticas | apresentação e cliente web, limitados aos contratos HTTP públicos | acesso a persistência |

**Regra absoluta:** nenhuma classe anotada com `@Document` é serializada numa
resposta HTTP. Sempre passa por um `Response DTO` construído pelo `mapper`.

## Acesso a dados

- `MongoRepository` para o CRUD simples (`save`, `findById`, `findAll`,
  `deleteById`, `existsById`).
- `MongoTemplate` para consultas dinâmicas — o filtro de listagem de itens e, na
  Entrega 2, as consultas do motor de *matching*. Mostra fluência real em NoSQL.

## Estrutura de pacotes

Raiz: `br.com.cesumar.agasalha`.

```
agasalha/
├── AgasalhaApplication.java
├── config/
├── controller/
│   └── dto/
├── exception/
├── mapper/
├── model/
├── repository/
└── service/
```

## Evolução Entrega 1 → Entrega 2

- **Entrega 1:** uma coleção (`itens`), documento plano e homogêneo, CRUD.
- **Entrega 2:** múltiplas coleções (`itens`, `abrigos`, `doadores`, `doacoes`),
  relacionamento entre elas, e `abrigos` com lista aninhada de subdocumentos
  `demandas`. Detalhe em `docs/regras-negocio.md`.

## Camada web — `apps/web` (ADR-014)

Esta seção substitui, para a Entrega 1 em diante, a descrição de páginas
estáticas do início deste documento. O mural não é mais `index.html` servido pelo
Spring: é uma aplicação React em `apps/web`, com build próprio.

```
Navegador
  |
React (apps/web, Vite :5173)
  |
proxy /api --> http://localhost:8080
  |
Controller
```

A aplicação é servida pelo Vite em desenvolvimento. O `vite.config.ts` encaminha
tudo sob `/api` para a API na porta 8080, então o navegador enxerga uma origem
só. Em produção o `npm run build` gera `dist/` estático, que pode ser servido por
qualquer servidor HTTP; nesse cenário o CORS da API entra em ação pela
propriedade `cors.origens-permitidas`.

Organização interna, por funcionalidade e não por tipo de arquivo:

| Diretório | Responsabilidade |
|---|---|
| `src/pages/` | uma página por rota (mural, cadastro, detalhes, edição) |
| `src/routes/` | definição das rotas do `react-router-dom` |
| `src/layouts/` | casca visual compartilhada entre as páginas |
| `src/features/itens/services/` | única fronteira de acesso HTTP à API |
| `src/features/itens/hooks/` | estado e chamadas por caso de uso |
| `src/features/itens/components/` | componentes do domínio de itens |
| `src/features/itens/schemas/` | validação de formulário com `zod` |
| `src/components/ui/` | componentes de interface genéricos |

**Regra de fronteira:** nenhum componente chama `fetch` direto. Todo acesso à API
passa por `features/itens/services/item.service.ts`, que traduz erro da API no
formato `ApiError` de `docs/http-api.md`. É o espelho, no cliente, da regra de que
nenhuma `@Document` cruza a fronteira HTTP.

`src/main/resources/static/index.html` permanece no repositório como landing da
API, apontando para `/docs`. Não é mais o mural.
