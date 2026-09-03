# Regras de negócio

Documento **append-only**. Cada regra tem um identificador `RN-NN` e uma marca de
entrega: `[E1]` (Entrega 1) ou `[E2]` (Entrega 2). Os testes automatizados
referenciam a `RN` que cobrem. Regras novas da Entrega 2 entram ao fim.

---

## Glossário de enums

| Enum | Valores |
|---|---|
| `TipoPeca` | `CASACO`, `BLUSA`, `CALCA`, `COBERTOR`, `MEIA`, `OUTRO` |
| `Tamanho` | `PP`, `P`, `M`, `G`, `GG` |
| `Genero` | `MASCULINO`, `FEMININO`, `UNISSEX` |
| `EstadoConservacao` | `NOVO`, `USADO_BOM`, `USADO_REGULAR` |
| `StatusItem` | `DISPONIVEL`, `RESERVADO`, `ENTREGUE` |
| `AcaoStatus` | `RESERVAR`, `ENTREGAR` |

---

## Item / agasalho

### RN-01 `[E1]` — Estrutura do documento `itens`

Cada item tem: `id`, `tipoPeca`, `tamanho`, `genero`, `estadoConservacao`,
`nomeDoador`, `contatoDoador`, `status`, `dataCadastro`. Documento plano e
homogêneo — todos os itens têm a mesma forma na Entrega 1.

### RN-02 `[E1]` — Validação do item

Um item é inválido, e a operação é rejeitada com `400`, se qualquer uma:

- `tipoPeca`, `tamanho`, `genero` ou `estadoConservacao` nulo;
- `nomeDoador` nulo ou só espaços;
- `contatoDoador` nulo ou só espaços;
- `status` ou `dataCadastro` nulo.

A validação vive no método de domínio `ItemAgasalho.validar()` e também nas
anotações Bean Validation do `Request DTO`.

### RN-03 `[E1]` — Estado inicial

Ao criar um item: `status = DISPONIVEL` e `dataCadastro = instante atual`. O
cliente não envia esses dois campos na criação.

### RN-04 `[E1]` — Máquina de estados do item

Transições válidas, e **somente** elas:

| Status atual | Ação | Novo status |
|---|---|---|
| `DISPONIVEL` | `RESERVAR` | `RESERVADO` |
| `RESERVADO` | `ENTREGAR` | `ENTREGUE` |

Qualquer outra combinação (pular etapa, retroceder, agir sobre item `ENTREGUE`,
`status` nulo) lança `TransicaoInvalidaException` e responde `409`. A lógica vive
em `StatusTransitionService.aplicar(StatusItem, AcaoStatus)`, sem estado e sem
dependência de banco.

### RN-05 `[E1]` — Atualização não mexe no ciclo de vida

`PUT /api/itens/{id}` altera apenas os dados descritivos do item (`tipoPeca`,
`tamanho`, `genero`, `estadoConservacao`, `nomeDoador`, `contatoDoador`). **Não**
altera `status` nem `dataCadastro`. Mudança de status é só pelos endpoints de
reserva e entrega.

### RN-06 `[E1]` — Filtro de listagem

`GET /api/itens` aceita os parâmetros opcionais `tamanho`, `tipoPeca`, `genero` e
`status`, em qualquer combinação. Sem parâmetro, lista todos. Cada parâmetro
presente vira um critério de igualdade (E lógico entre eles). Implementado com
`MongoTemplate` + `Query`/`Criteria`.

---

## Abrigo e demanda `[E2]`

### RN-07 `[E2]` — Estrutura aninhada de `abrigos`

Cada abrigo tem `id`, `nome`, `endereco` (objeto aninhado), `capacidadeEstoque`,
`estoqueAtual` e `demandas` — uma **lista de subdocumentos**. Cada demanda tem
`tipoPeca`, `tamanho`, `genero`, `quantidadeNecessaria`, `quantidadeAtendida`.

### RN-08 `[E2]` — Invariantes do agregado

- `capacidadeEstoque > 0`;
- para toda demanda, `quantidadeAtendida <= quantidadeNecessaria`;
- `estoqueAtual >= 0`.

Violação ao criar ou alterar abrigo/demanda é rejeitada com `400`.

### RN-09 `[E2]` — Déficit de uma demanda

`deficit = quantidadeNecessaria - quantidadeAtendida`. Uma demanda está "em
aberto" enquanto `deficit > 0`.

---

## Matching `[E2]`

### RN-10 `[E2]` — Compatibilidade de tamanho

Item casa com demanda apenas se `item.tamanho == demanda.tamanho` (match exato).

### RN-11 `[E2]` — Compatibilidade de gênero

- item `UNISSEX` atende demanda `MASCULINO`, `FEMININO` ou `UNISSEX`;
- demanda `UNISSEX` aceita item de qualquer gênero;
- caso contrário, os gêneros devem ser iguais.

### RN-12 `[E2]` — Limite de estoque do abrigo

O match é bloqueado se `estoqueAtual + 1 > capacidadeEstoque`.

### RN-13 `[E2]` — Demanda precisa estar em aberto

Só há match se `quantidadeAtendida < quantidadeNecessaria` (RN-09).

### RN-14 `[E2]` — Priorização por déficit

Quando mais de um abrigo/demanda é compatível, escolhe o de **maior déficit**
(RN-09). Empate: escolha estável (menor `id` de abrigo).

---

## Relacionamentos `[E2]`

### RN-15 `[E2]` — Referências entre coleções

- `doacao` referencia `doadorId` e `abrigoId`;
- `item` referencia `doacaoId` e, quando casado por matching, `abrigoId`.

Não há duplicação de dados do doador ou do abrigo dentro de `item` ou `doacao` —
apenas os identificadores.
