# Contrato HTTP

Base path: `/api`. Documentação interativa em `/docs` (Swagger UI); contrato
OpenAPI em JSON em `/v3/api-docs`.

Este documento é **append-only**: o contrato da Entrega 2 (`/abrigos`,
`/abrigos/{id}/demandas`, `/doadores`, `/matches`) é **adicionado ao fim** quando
a Entrega 2 começar. Não reescrever o que já está aqui.

## Convenções

- Recurso ausente: `404`.
- Corpo inválido ou parâmetro inválido: `400`.
- Transição de estado não permitida: `409`.
- Criação: `201` com cabeçalho `Location` apontando o recurso criado.
- Remoção: `204` sem corpo.
- O campo `id` nunca vem no corpo; em atualização, vem só do path.

## Formato de erro

Todas as falhas usam a mesma forma, produzida pelo `@RestControllerAdvice`:

```json
{
  "timestamp": "2026-09-02T18:30:00Z",
  "status": 400,
  "erro": "descricao curta do problema",
  "detalhes": ["campo nome: nao pode ser vazio"]
}
```

`detalhes` é uma lista; fica vazia quando não há detalhamento por campo.

---

## Entrega 1 — recurso `itens`

| Método | Caminho | Entrada | Sucesso | Erros |
|---|---|---|---|---|
| `POST` | `/api/itens` | `ItemCreateRequest` | `201` + `Location` + `ItemResponse` | `400` |
| `GET` | `/api/itens` | query: `tamanho`, `tipoPeca`, `genero`, `status` (todos opcionais) | `200` + lista de `ItemSummaryResponse` | `400` (enum inválido no parâmetro) |
| `GET` | `/api/itens/{id}` | — | `200` + `ItemResponse` | `404` |
| `PUT` | `/api/itens/{id}` | `ItemUpdateRequest` | `200` + `ItemResponse` | `400`, `404` |
| `DELETE` | `/api/itens/{id}` | — | `204` | `404` |
| `POST` | `/api/itens/{id}/reserva` | — | `200` + `ItemResponse` | `404`, `409` |
| `POST` | `/api/itens/{id}/entrega` | — | `200` + `ItemResponse` | `404`, `409` |

### `ItemCreateRequest` / `ItemUpdateRequest`

Mesma forma nas duas operações:

```json
{
  "tipoPeca": "CASACO",
  "tamanho": "M",
  "genero": "UNISSEX",
  "estadoConservacao": "USADO_BOM",
  "nomeDoador": "Ana Souza",
  "contatoDoador": "ana@exemplo.com"
}
```

Validação (RN-02): `tipoPeca`, `tamanho`, `genero`, `estadoConservacao`
obrigatórios e dentro do enum; `nomeDoador` e `contatoDoador` obrigatórios e não
em branco. Violação → `400`.

### `ItemResponse`

```json
{
  "id": "66b0c1e2f4a1b23c45d6e7f8",
  "tipoPeca": "CASACO",
  "tamanho": "M",
  "genero": "UNISSEX",
  "estadoConservacao": "USADO_BOM",
  "nomeDoador": "Ana Souza",
  "contatoDoador": "ana@exemplo.com",
  "status": "DISPONIVEL",
  "dataCadastro": "2026-06-01T10:00:00Z"
}
```

### `ItemSummaryResponse` (usado na listagem)

```json
{
  "id": "66b0c1e2f4a1b23c45d6e7f8",
  "tipoPeca": "CASACO",
  "tamanho": "M",
  "genero": "UNISSEX",
  "status": "DISPONIVEL"
}
```

### Reserva e entrega

`POST /api/itens/{id}/reserva` aplica `DISPONIVEL → RESERVADO`.
`POST /api/itens/{id}/entrega` aplica `RESERVADO → ENTREGUE`.
Fora dessas transições: `409` (RN-04).

---

## Entrega 2 — abrigos, doadores e doações

Mesmo formato de erro da Entrega 1. `409` também para remoção de registro
referenciado (ADR-016).

### Abrigos e demandas

| Método | Caminho | Entrada | Sucesso | Erros |
|---|---|---|---|---|
| `POST` | `/api/abrigos` | `AbrigoCreateRequest` | `201` + `Location` + `AbrigoResponse` | `400` |
| `GET` | `/api/abrigos` | — | `200` + lista de `AbrigoResponse` | — |
| `GET` | `/api/abrigos/{id}` | — | `200` + `AbrigoResponse` | `404` |
| `PUT` | `/api/abrigos/{id}` | `AbrigoUpdateRequest` | `200` + `AbrigoResponse` | `400`, `404` |
| `DELETE` | `/api/abrigos/{id}` | — | `204` | `404`, `409` |
| `GET` | `/api/abrigos/{id}/demandas` | — | `200` + lista de `DemandaResponse` | `404` |
| `POST` | `/api/abrigos/{id}/demandas` | `DemandaCreateRequest` | `201` + `Location` + `DemandaResponse` | `400`, `404` |
| `PUT` | `/api/abrigos/{id}/demandas/{posicao}` | `DemandaUpdateRequest` | `200` + `DemandaResponse` | `400`, `404` |
| `DELETE` | `/api/abrigos/{id}/demandas/{posicao}` | — | `204` | `404` |

`{posicao}` é o índice da demanda na lista do abrigo, começando em `0`. Posição
inexistente → `404`; valor não numérico → `400`.

### `AbrigoCreateRequest`

```json
{
  "nome": "Abrigo Esperanca",
  "endereco": {
    "logradouro": "Rua das Flores",
    "numero": "120",
    "bairro": "Centro",
    "cidade": "Maringa",
    "estado": "PR"
  },
  "capacidadeEstoque": 100,
  "estoqueAtual": 20,
  "demandas": [
    { "tipoPeca": "CASACO", "tamanho": "M", "genero": "UNISSEX", "quantidadeNecessaria": 30, "quantidadeAtendida": 10 }
  ]
}
```

`estoqueAtual`, `demandas` e `quantidadeAtendida` são opcionais (padrão `0`, lista
vazia e `0`). `numero` e `bairro` são opcionais. Validação de RN-08:
`capacidadeEstoque > 0`, `estoqueAtual >= 0`, `quantidadeNecessaria > 0` e
`quantidadeAtendida <= quantidadeNecessaria`. Violação → `400`.

`AbrigoUpdateRequest` tem `nome`, `endereco`, `capacidadeEstoque` e `estoqueAtual`,
todos obrigatórios; as demandas só mudam pelos endpoints de demanda.
`DemandaUpdateRequest` tem os mesmos campos de `DemandaCreateRequest`, com
`quantidadeAtendida` obrigatório.

### `AbrigoResponse`

```json
{
  "id": "66b0c1e2f4a1b23c45d6e7a1",
  "nome": "Abrigo Esperanca",
  "endereco": {
    "logradouro": "Rua das Flores",
    "numero": "120",
    "bairro": "Centro",
    "cidade": "Maringa",
    "estado": "PR"
  },
  "capacidadeEstoque": 100,
  "estoqueAtual": 20,
  "demandas": [
    {
      "posicao": 0,
      "tipoPeca": "CASACO",
      "tamanho": "M",
      "genero": "UNISSEX",
      "quantidadeNecessaria": 30,
      "quantidadeAtendida": 10,
      "deficit": 20
    }
  ]
}
```

### Doadores

| Método | Caminho | Entrada | Sucesso | Erros |
|---|---|---|---|---|
| `POST` | `/api/doadores` | `DoadorCreateRequest` | `201` + `Location` + `DoadorResponse` | `400` |
| `GET` | `/api/doadores` | — | `200` + lista de `DoadorResponse` | — |
| `GET` | `/api/doadores/{id}` | — | `200` + `DoadorResponse` | `404` |
| `PUT` | `/api/doadores/{id}` | `DoadorUpdateRequest` | `200` + `DoadorResponse` | `400`, `404` |
| `DELETE` | `/api/doadores/{id}` | — | `204` | `404`, `409` |

```json
{ "nome": "Ana Souza", "contato": "ana@exemplo.com" }
```

`nome` e `contato` obrigatórios e não em branco. `409` quando o doador tem doações.

### Doações

| Método | Caminho | Entrada | Sucesso | Erros |
|---|---|---|---|---|
| `POST` | `/api/doacoes` | `DoacaoCreateRequest` | `201` + `Location` + `DoacaoResponse` | `400`, `404` (doador ou abrigo) |
| `GET` | `/api/doacoes/{id}` | — | `200` + `DoacaoResponse` | `404` |
| `POST` | `/api/doacoes/{id}/itens` | `ItemDoacaoRequest` | `201` + `Location` (`/api/itens/{id}`) + `ItemResponse` | `400`, `404` |

```json
{ "doadorId": "66b0c1e2f4a1b23c45d6e7b1", "abrigoId": "66b0c1e2f4a1b23c45d6e7a1" }
```

`DoacaoResponse` devolve só os identificadores e a data (RN-15):

```json
{
  "id": "66b0c1e2f4a1b23c45d6e7c1",
  "doadorId": "66b0c1e2f4a1b23c45d6e7b1",
  "abrigoId": "66b0c1e2f4a1b23c45d6e7a1",
  "dataDoacao": "2026-06-05T10:00:00Z"
}
```

`ItemDoacaoRequest` tem `tipoPeca`, `tamanho`, `genero` e `estadoConservacao`,
todos obrigatórios. O item criado não guarda dados do doador (ADR-016).
`ItemResponse` ganha `doacaoId` e `abrigoId`, ambos opcionais e nulos para itens
da Entrega 1.