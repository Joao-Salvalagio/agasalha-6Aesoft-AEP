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
