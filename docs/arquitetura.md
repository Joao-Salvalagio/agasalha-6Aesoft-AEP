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

## Entrega 2 — modelo das coleções e arquitetura final

A Entrega 2 mantém as camadas da Entrega 1 e acrescenta três coleções ao lado de
`itens`: `abrigos` (com objetos aninhados), `doadores` e `doacoes` (com
referências).

### Coleções e relacionamentos

```
doadores               doacoes                    abrigos
--------               -------                    -------
_id  <---------------- doadorId                   _id  <---------------+
nome                   abrigoId ----------------->                     |
contato                dataDoacao                 nome                 |
                           ^                      endereco {logradouro, numero, bairro,
                           |                                cidade, estado}   (aninhado)
                       itens                      capacidadeEstoque
                       -----                      estoqueAtual
                       _id                        demandas [ {tipoPeca, tamanho,   (lista de
                       doacaoId ------+                      genero,              subdocumentos)
                       abrigoId ------+--------+             quantidadeNecessaria,
                       tipoPeca, tamanho,                    quantidadeAtendida} ]
                       genero, estadoConservacao
                       nomeDoador, contatoDoador (só itens sem doação)
                       status, dataCadastro
```

| Coleção | Conteúdo | Referências |
|---|---|---|
| `itens` | documento plano; `doacaoId` e `abrigoId` opcionais | `doacoes`, `abrigos` |
| `doadores` | `nome`, `contato` | — |
| `doacoes` | `doadorId`, `abrigoId`, `dataDoacao` | `doadores`, `abrigos` |
| `abrigos` | `endereco` aninhado e `demandas[]` | — |

Só identificadores cruzam coleções: nenhum dado de doador ou abrigo é copiado para
`doacao` (RN-15). O item de uma doação também não guarda dados do doador (ADR-016).

### Componentes da Entrega 2

```
AbrigoController, DoadorController, DoacaoController, MatchController
   |
Request DTO --> Service --> Repository --> MongoDB
                  |
                  +--> MatchingService (lógica pura, sem banco)

MongoDB --> Model --> Service --> Mapper --> Response DTO --> Controller --> JSON
```

| Serviço | Responsabilidade |
|---|---|
| `AbrigoService` | CRUD do abrigo e das demandas aninhadas, endereçadas por posição; recusa remover abrigo referenciado |
| `DoadorService` | CRUD do doador; recusa remover doador com doações |
| `DoacaoService` | cria a doação validando doador e abrigo; cria itens vinculados à doação |
| `MatchService` / `MatchingService` | sugere o abrigo e a demanda de maior déficit (RN-10 a RN-14) |

### Testes e cobertura

Unitários cobrem modelo, mapper e service; `@WebMvcTest` cobre o contrato HTTP de
cada controller; `AbrigoApiIT` e `RelacionamentosIT` (Testcontainers-MongoDB)
cobrem os documentos aninhados no banco e a navegação `item → doação → doador`.
Evidência de cobertura: `./mvnw clean verify` gera `target/site/jacoco/index.html`
e falha abaixo de 70% de linha.