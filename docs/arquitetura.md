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
| `src/pages/` | uma página por rota: mural, cadastro de doação, detalhes e edição da peça, abrigos, detalhe do abrigo, doadores |
| `src/routes/` | definição das rotas do `react-router-dom` |
| `src/layouts/` | casca visual compartilhada: sidebar fixa (gaveta no celular) e área da página |
| `src/features/<domínio>/services/` | única fronteira de acesso HTTP à API, um serviço por domínio |
| `src/features/<domínio>/hooks/` | estado e chamadas por caso de uso |
| `src/features/<domínio>/components/` | componentes do domínio |
| `src/features/<domínio>/schemas/` | validação de formulário com `zod` |
| `src/features/<domínio>/interfaces/` e `constants/` | tipos dos contratos HTTP, rótulos e opções de seleção |
| `src/components/` | componentes globais: `PageHeader`, `SummaryStrip`, `ProgressBar`, `ErrorState`, `EmptyState`, `ConfirmDialog`, campos de formulário, `AppSidebar` |
| `src/components/ui/` | primitivas de interface (shadcn sobre `@base-ui/react`) |
| `src/lib/` | `http.ts` (tratamento de resposta e erro), `useRequisicao.ts` (carregamento com estado) e utilitários |

Domínios em `src/features/`: `itens`, `abrigos`, `doadores`, `doacoes` e `matches`.

**Regra de fronteira:** nenhum componente chama `fetch` direto. Todo acesso à API
passa pelos serviços de `features/<domínio>/services/`, que usam `lib/http.ts` para
traduzir erro da API no formato `ApiError` de `docs/http-api.md`. É o espelho, no
cliente, da regra de que nenhuma `@Document` cruza a fronteira HTTP.

### Rotas da aplicação

| Rota | Tela |
|---|---|
| `/` | mural: resumo, filtros e lista de peças |
| `/itens/novo` | registrar doação: escolhe o doador e descreve a peça; mostra a sugestão de match |
| `/itens/:id` | detalhe da peça: doador, destino, match e botão "Enviar ao abrigo" |
| `/itens/:id/editar` | edição de peça avulsa e não entregue |
| `/abrigos` e `/abrigos/:id` | abrigos, estoque, déficit e gestão de demandas |
| `/doadores` | lista e cadastro de doadores |

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
| `doacoes` | `doadorId`, `abrigoId` (opcional), `dataDoacao` | `doadores`, `abrigos` |
| `abrigos` | `endereco` aninhado e `demandas[]` | — |

Só identificadores cruzam coleções: nenhum dado de doador ou abrigo é copiado para
`doacao` (RN-15). O item de uma doação também não guarda dados do doador (ADR-016).

A doação nasce sem abrigo (ADR-017, RN-16). O `abrigoId` do **item** só é gravado
quando a peça é enviada ao abrigo (ADR-018, RN-17); até lá o item não tem destino.
Itens avulsos, vindos da Entrega 1, guardam `nomeDoador` e `contatoDoador` dentro do
próprio documento e não têm `doacaoId`.

### Exemplos de documentos

`abrigos` — objeto aninhado (`endereco`) e lista de subdocumentos (`demandas`):

```json
{
  "_id": "665f00000000000000000101",
  "nome": "Abrigo Esperanca",
  "endereco": { "logradouro": "Rua das Flores", "numero": "120", "bairro": "Centro",
                "cidade": "Maringa", "estado": "PR" },
  "capacidadeEstoque": 100,
  "estoqueAtual": 20,
  "demandas": [
    { "tipoPeca": "CASACO", "tamanho": "M", "genero": "UNISSEX",
      "quantidadeNecessaria": 30, "quantidadeAtendida": 10 }
  ]
}
```

`itens` — peça de uma doação, ainda sem abrigo:

```json
{
  "_id": "…",
  "tipoPeca": "CASACO", "tamanho": "M", "genero": "UNISSEX",
  "estadoConservacao": "USADO_BOM",
  "status": "DISPONIVEL",
  "dataCadastro": "2026-06-01T10:00:00Z",
  "doacaoId": "665f00000000000000000201"
}
```

`doacoes` — só referências (o `abrigoId` é opcional):

```json
{ "_id": "665f00000000000000000201", "doadorId": "665f00000000000000000001",
  "dataDoacao": "2026-06-01T10:00:00Z" }
```

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
| `DoacaoService` | cria a doação validando o doador e, se informado, o abrigo; cria itens vinculados à doação |
| `MatchService` | `sugerir`: devolve o melhor abrigo para a peça; `confirmar`: envia a peça ao abrigo (grava `abrigoId`, soma ao estoque, abate a demanda e reserva a peça) |
| `MatchingService` | lógica pura, sem banco: compatibilidade de tipo, tamanho e gênero, limite de estoque, demanda em aberto e maior déficit (RN-10 a RN-14 e RN-18) |
| `ItemStatusService` / `StatusTransitionService` | reserva e entrega do item; exigem que a peça já tenha abrigo (ADR-018) |

### Matching: critérios de escolha

`MatchingService.sugerir` percorre os abrigos com espaço no estoque (RN-12) e,
para cada demanda em aberto (RN-13), exige:

1. mesmo `tipoPeca` (RN-18);
2. mesmo `tamanho` (RN-10);
3. gênero compatível (RN-11): `UNISSEX` atende e aceita qualquer gênero.

Entre as candidatas, escolhe a de **maior déficit** e, no empate, o menor `id` de
abrigo (RN-14). Sem candidata, não há sugestão (`404`).

### Fluxo de doação e envio ao abrigo

```
1. Cadastrar doador            POST /api/doadores
2. Registrar a doação          POST /api/doacoes            { doadorId }
3. Criar a peça da doação      POST /api/doacoes/{id}/itens
4. Ver a sugestão              POST /api/matches            { itemId }      (só sugere)
5. Enviar ao abrigo            POST /api/matches/confirmacao { itemId }
6. Confirmar a entrega         POST /api/itens/{id}/entrega
```

No passo 5 o servidor recalcula o match e o aplica: grava o `abrigoId` no item, soma 1
ao `estoqueAtual` do abrigo e à `quantidadeAtendida` da demanda, e a peça passa de
`DISPONIVEL` a `RESERVADO`. O cliente não envia o abrigo. Peça que já tem abrigo
responde `409`.

Máquina de estados do item (RN-04), com o requisito do abrigo (ADR-018):

```
DISPONIVEL --enviar ao abrigo--> RESERVADO --entregar--> ENTREGUE
```

`reserva` e `entrega` respondem `409` quando a peça não tem `abrigoId`. Limites
conhecidos: o MongoDB não tem transação nesta PoC (o abrigo é salvo antes do item), e
o estoque e o déficit sobem no envio, não na entrega.

### Testes e cobertura

Unitários cobrem modelo, mapper e service; `@WebMvcTest` cobre o contrato HTTP de
cada controller; `AbrigoApiIT`, `MatchApiIT` e `RelacionamentosIT`
(Testcontainers-MongoDB) cobrem os documentos aninhados no banco, a navegação
`item → doação → doador`, a sugestão e o envio da peça ao abrigo e a liberação da
reserva e da entrega. O `MatchingServiceTest` tem a tabela de compatibilidade
(tipo, tamanho, gênero, estoque, demanda em aberto e prioridade por déficit).
O frontend é verificado pelo build (`npm run build`), fora da medição de cobertura.
Evidência de cobertura: `./mvnw clean verify` gera `target/site/jacoco/index.html`
e falha abaixo de 70% de linha.