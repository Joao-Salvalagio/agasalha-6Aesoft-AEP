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
