# AGENTS.md — Frontend (apps/web)

Este documento estabelece o **padrão arquitetural obrigatório**, o **catálogo completo de rotas (frontend e backend)** e as **regras de desenvolvimento** para o frontend do projeto **Agasalha**. Todo assistente de IA ou desenvolvedor deve seguir rigorosamente esta estrutura antes de criar ou modificar qualquer código no frontend.

---

## 1. Contexto do Projeto

O **Agasalha** é uma plataforma de mural de doações e *matching* de agasalhos, alinhada ao **ODS 1 — Erradicação da Pobreza**.
O frontend comunica-se diretamente com o backend Spring Boot através da API REST exposta sob o prefixo `/api` (detalhada em `docs/http-api.md`).

### Entregas do Projeto
- **Entrega 1 (`v1.0`)**: Mural de itens/agasalhos, cadastro de doações, filtros dinâmicos e controle de ciclo de vida do item (`DISPONIVEL` → `RESERVADO` → `ENTREGUE`).
- **Entrega 2 (`v2.0`)**: Gestão de abrigos, demandas, doadores e motor de *matching*. A peça é enviada a um abrigo a partir do match; reservar e entregar só valem depois disso.

---

## 2. Stack Tecnológica Fixa

| Tecnologia | Finalidade | Regras de Uso |
|---|---|---|
| **React (TypeScript)** | Biblioteca base de UI | Componentes funcionais, tipagem estrita, sem `any`. |
| **Tailwind CSS** | Estilização utilitária | Classes utilitárias, design responsivo e tokens consistentes. |
| **shadcn/ui** | Componentes primitivos de UI | Instalados em `components/ui/` (Radix UI + Tailwind). |
| **Zod** | Validação de esquemas | Schemas em `schemas/` nas features, inferindo tipos com `z.infer`. |
| **react-hook-form** | Gerenciamento de formulários | Integrado ao Zod via `@hookform/resolvers/zod`. |
| **React Router (v6/v7)** | Roteamento SPA | Configuração central em `routes/` e páginas em `pages/`. |
| **Fetch API nativa** | Comunicação HTTP | Encapsulada exclusivamente dentro da camada `services/`. |

---

## 3. Estrutura de Pastas

A aplicação segue rigorosamente a arquitetura modular orientada a **Features**, separando componentes primitivos, componentes globais, páginas e rotas:

```
apps/web/src/
├── assets/                    # Imagens, ícones estáticos, fontes
├── components/
│   ├── ui/                    # Primitivas puras do shadcn/ui (button, input, card, dialog, badge, etc.)
│   └── ...                    # Componentes reutilizáveis globais (Header, Navbar, Footer, StatusBadge, EmptyState, etc.)
├── features/                  # Módulos verticais por domínio de negócio
│   └── [feature-name]/        # Ex.: itens, mural, doacoes, abrigos
│       ├── services/          # Funções de requisição HTTP (API fetch tipado)
│       ├── hooks/             # Custom hooks React consumindo a service (loading, error, mutações)
│       ├── components/        # Componentes visuais específicos desta feature (cards, filtros, formulários)
│       ├── schemas/           # Schemas de validação Zod (em features com formulários)
│       ├── interfaces/        # Interfaces e tipos TypeScript (Request/Response DTOs, entidades)
│       └── constants/         # Constantes, enums formatados para UI, opções de select e labels
├── pages/                     # Páginas vinculadas às rotas (orquestradoras, fora de features)
├── routes/                    # Configuração central de rotas do React Router
├── layouts/                   # Layouts compartilhados (ex.: MainLayout com Header e Outlet)
├── lib/                       # Utilitários globais (cn helper do Tailwind, cliente fetch base)
├── App.tsx                    # Componente raiz com roteador e providers
└── main.tsx                   # Ponto de entrada da aplicação
```

---

## 4. Catálogo de Rotas do Frontend (React Router)

As rotas são declaradas centralizadamente em `src/routes/` e renderizam as views localizadas em `src/pages/`.

### 4.1. Tabela de Rotas da SPA

| Rota Frontend | Componente de Página | Layout | Descrição e Ações |
|---|---|---|---|
| `/` | `MuralPage` | `MainLayout` | Mural público com resumo, filtros e listagem de agasalhos. |
| `/itens/novo` | `CadastrarItemPage` | `MainLayout` | Registra uma doação: escolhe um doador cadastrado (dropdown) e descreve a peça; ao final mostra a sugestão de match. |
| `/itens/:id` | `DetalhesItemPage` | `MainLayout` | Detalhe da peça, doador, destino, match sugerido, botão "Enviar ao abrigo" e transições de status (liberadas só com abrigo). |
| `/itens/:id/editar` | `EditarItemPage` | `MainLayout` | Formulário para edição dos dados descritivos da peça (RN-05). Só para peça avulsa e não entregue. |
| `/abrigos` | `AbrigosPage` | `MainLayout` | Lista de abrigos com estoque, demandas em aberto e resumo de déficit. |
| `/abrigos/:id` | `DetalhesAbrigoPage` | `MainLayout` | Endereço, estoque e demandas com barra de progresso; criar, editar e remover demanda. |
| `/doadores` | `DoadoresPage` | `MainLayout` | Lista de doadores e cadastro de novo doador. |
| `*` | `NotFoundPage` | `MainLayout` | Tela de página não encontrada (404) com link de retorno ao mural. |

### 4.2. Estrutura do Roteador (`src/routes/index.tsx`)
```tsx
import { createBrowserRouter, Navigate } from 'react-router-dom';
import { MainLayout } from '@/layouts/MainLayout';
import { MuralPage } from '@/pages/MuralPage';
import { CadastrarItemPage } from '@/pages/CadastrarItemPage';
import { DetalhesItemPage } from '@/pages/DetalhesItemPage';
import { EditarItemPage } from '@/pages/EditarItemPage';
import { AbrigosPage } from '@/pages/AbrigosPage';
import { DetalhesAbrigoPage } from '@/pages/DetalhesAbrigoPage';
import { DoadoresPage } from '@/pages/DoadoresPage';
import { NotFoundPage } from '@/pages/NotFoundPage';

export const router = createBrowserRouter([
  {
    path: '/',
    element: <MainLayout />,
    children: [
      { index: true, element: <MuralPage /> },
      { path: 'itens/novo', element: <CadastrarItemPage /> },
      { path: 'itens/:id', element: <DetalhesItemPage /> },
      { path: 'itens/:id/editar', element: <EditarItemPage /> },
      { path: 'abrigos', element: <AbrigosPage /> },
      { path: 'abrigos/:id', element: <DetalhesAbrigoPage /> },
      { path: 'doadores', element: <DoadoresPage /> },
      { path: '404', element: <NotFoundPage /> },
      { path: '*', element: <Navigate to="/404" replace /> },
    ],
  },
]);
```

---

## 5. Catálogo de Endpoints da API REST (Backend Spring Boot)

Base path da API: `/api`.  
Todos os endpoints utilizam JSON no cabeçalho `Content-Type: application/json` quando há envio de corpo.

### 5.1. Tabela de Endpoints

| Método | Caminho | Parâmetros / Entrada | Status Sucesso | Retorno Sucesso | Erros Possíveis |
|---|---|---|---|---|---|
| `GET` | `/api/itens` | Query opcional: `tamanho`, `tipoPeca`, `genero`, `status` | `200 OK` | `ItemSummaryResponse[]` | `400 Bad Request` (enum inválido) |
| `GET` | `/api/itens/{id}` | Path: `id` (string) | `200 OK` | `ItemResponse` | `404 Not Found` |
| `POST` | `/api/itens` | Body: `ItemCreateRequest` | `201 Created` (+ header `Location`) | `ItemResponse` | `400 Bad Request` |
| `PUT` | `/api/itens/{id}` | Path: `id`, Body: `ItemUpdateRequest` | `200 OK` | `ItemResponse` | `400 Bad Request`, `404 Not Found` |
| `DELETE` | `/api/itens/{id}` | Path: `id` (string) | `204 No Content` | *(sem corpo)* | `404 Not Found` |
| `POST` | `/api/itens/{id}/reserva` | Path: `id` (string) | `200 OK` | `ItemResponse` | `404 Not Found`, `409 Conflict` (transição inválida ou item sem abrigo) |
| `POST` | `/api/itens/{id}/entrega` | Path: `id` (string) | `200 OK` | `ItemResponse` | `404 Not Found`, `409 Conflict` (transição inválida ou item sem abrigo) |
| `GET` | `/api/abrigos` | — | `200 OK` | `AbrigoResponse[]` | — |
| `GET` | `/api/abrigos/{id}` | Path: `id` | `200 OK` | `AbrigoResponse` | `404 Not Found` |
| `POST` | `/api/abrigos/{id}/demandas` | Body: `DemandaRequest` | `201 Created` | `DemandaResponse` | `400 Bad Request`, `404 Not Found` |
| `PUT` | `/api/abrigos/{id}/demandas/{posicao}` | Body: `DemandaRequest` | `200 OK` | `DemandaResponse` | `400 Bad Request`, `404 Not Found` |
| `DELETE` | `/api/abrigos/{id}/demandas/{posicao}` | — | `204 No Content` | *(sem corpo)* | `404 Not Found` |
| `GET` | `/api/doadores` | — | `200 OK` | `DoadorResponse[]` | — |
| `GET` | `/api/doadores/{id}` | Path: `id` | `200 OK` | `DoadorResponse` | `404 Not Found` |
| `POST` | `/api/doadores` | Body: `DoadorCreateRequest` | `201 Created` | `DoadorResponse` | `400 Bad Request` |
| `GET` | `/api/doacoes/{id}` | Path: `id` | `200 OK` | `DoacaoResponse` | `404 Not Found` |
| `POST` | `/api/doacoes` | Body: `DoacaoCreateRequest` (`abrigoId` opcional) | `201 Created` | `DoacaoResponse` | `400 Bad Request`, `404 Not Found` |
| `POST` | `/api/doacoes/{id}/itens` | Body: `ItemDoacaoRequest` | `201 Created` | `ItemResponse` | `400 Bad Request`, `404 Not Found` |
| `POST` | `/api/matches` | Body: `{ itemId }` | `200 OK` | `MatchResponse` | `400 Bad Request`, `404 Not Found` (item ou abrigo compatível) |
| `POST` | `/api/matches/confirmacao` | Body: `{ itemId }` | `200 OK` | `ItemResponse` | `400 Bad Request`, `404 Not Found`, `409 Conflict` (item já em um abrigo) |

---

## 6. Contratos de Request e Response (Payloads Exatos)

### 6.1. Enums do Domínio

```typescript
export type TipoPeca = 'CASACO' | 'BLUSA' | 'CALCA' | 'COBERTOR' | 'MEIA' | 'OUTRO';
export type Tamanho = 'PP' | 'P' | 'M' | 'G' | 'GG';
export type Genero = 'MASCULINO' | 'FEMININO' | 'UNISSEX';
export type EstadoConservacao = 'NOVO' | 'USADO_BOM' | 'USADO_REGULAR';
export type StatusItem = 'DISPONIVEL' | 'RESERVADO' | 'ENTREGUE';
```

### 6.2. Criação (`POST /api/itens`) e Atualização (`PUT /api/itens/{id}`)

#### Contrato TypeScript:
```typescript
export interface ItemCreateRequest {
  tipoPeca: TipoPeca;
  tamanho: Tamanho;
  genero: Genero;
  estadoConservacao: EstadoConservacao;
  nomeDoador: string;
  contatoDoador: string;
}

export interface ItemUpdateRequest extends ItemCreateRequest {}
```

#### JSON de Exemplo de Request:
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

> **Atenção (RN-03 e RN-05):** `id`, `status` e `dataCadastro` **NUNCA** são enviados no corpo da requisição.

---

### 6.3. Listagem com Filtros (`GET /api/itens`)

#### Parâmetros de Query Opcionais:
```
GET /api/itens?tipoPeca=CASACO&tamanho=M&genero=UNISSEX&status=DISPONIVEL
```

#### Contrato TypeScript do Resumo (`ItemSummaryResponse`):
```typescript
export interface ItemSummaryResponse {
  id: string;
  tipoPeca: TipoPeca;
  tamanho: Tamanho;
  genero: Genero;
  status: StatusItem;
}
```

#### JSON de Exemplo de Response (200 OK):
```json
[
  {
    "id": "66b0c1e2f4a1b23c45d6e7f8",
    "tipoPeca": "CASACO",
    "tamanho": "M",
    "genero": "UNISSEX",
    "status": "DISPONIVEL"
  },
  {
    "id": "66b0c1e2f4a1b23c45d6e7f9",
    "tipoPeca": "COBERTOR",
    "tamanho": "G",
    "genero": "UNISSEX",
    "status": "RESERVADO"
  }
]
```

---

### 6.4. Detalhes e Retorno de Mutações (`ItemResponse`)

Retornado em `GET /api/itens/{id}`, `POST /api/itens`, `PUT /api/itens/{id}`, `POST /api/itens/{id}/reserva` e `POST /api/itens/{id}/entrega`.

#### Contrato TypeScript:
```typescript
export interface ItemResponse {
  id: string;
  tipoPeca: TipoPeca;
  tamanho: Tamanho;
  genero: Genero;
  estadoConservacao: EstadoConservacao;
  nomeDoador: string | null;
  contatoDoador: string | null;
  status: StatusItem;
  dataCadastro: string; // ISO-8601 UTC ex.: "2026-06-01T10:00:00Z"
  doacaoId: string | null;
  abrigoId: string | null;
}
```

#### JSON de Exemplo de Response (200 OK / 201 Created):
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
  "dataCadastro": "2026-06-01T10:00:00Z",
  "doacaoId": null,
  "abrigoId": null
}
```

> Itens criados por uma doação (`POST /api/doacoes/{id}/itens`) vêm com `nomeDoador` e `contatoDoador` nulos; o doador é resolvido por `doacaoId`.

---

### 6.5. Máquina de Estados e Ações de Ciclo de Vida (RN-04)

- **Reservar:** `POST /api/itens/{id}/reserva`
  - Transição válida: `DISPONIVEL` → `RESERVADO`
  - Retorno: `ItemResponse` com `status: "RESERVADO"`
- **Entregar:** `POST /api/itens/{id}/entrega`
  - Transição válida: `RESERVADO` → `ENTREGUE`
  - Retorno: `ItemResponse` com `status: "ENTREGUE"`
- **Tentativa de transição inválida:** retorna status `409 Conflict`.
- **Item sem abrigo:** reservar e entregar também retornam `409 Conflict`. O abrigo é gravado por `POST /api/matches/confirmacao` ("Enviar ao abrigo"), que não altera o status (ADR-018).

---

### 6.6. Formato Padrão de Erro (`ApiError`)

Todas as exceções do backend (`400`, `404`, `409`) retornam a estrutura:

```typescript
export interface ApiError {
  timestamp: string;
  status: number;
  erro: string;
  detalhes: string[];
}
```

#### Cenário 1: Erro de Validação (400 Bad Request)
```json
{
  "timestamp": "2026-09-04T16:30:00Z",
  "status": 400,
  "erro": "Dados inválidos",
  "detalhes": [
    "campo nomeDoador: não pode estar em branco",
    "campo tamanho: não pode ser nulo"
  ]
}
```

#### Cenário 2: Recurso Não Encontrado (404 Not Found)
```json
{
  "timestamp": "2026-09-04T16:30:00Z",
  "status": 404,
  "erro": "Item não encontrado com ID: 66b0c1e2f4a1b23c45d6e7f8",
  "detalhes": []
}
```

#### Cenário 3: Transição de Estado Inválida (409 Conflict - RN-04)
```json
{
  "timestamp": "2026-09-04T16:30:00Z",
  "status": 409,
  "erro": "Transição inválida: não é possível executar RESERVAR em item no estado ENTREGUE",
  "detalhes": []
}
```

---

## 7. Matriz de Vinculação: Rota Frontend ➔ Endpoint Backend ➔ Hook

Para a IA nunca se perder, esta matriz define qual tela utiliza quais hooks e endpoints:

| Tela (Rota) | Hook da Feature | Método do Service | Endpoint Consumido | Ação do Usuário |
|---|---|---|---|---|
| `/` (`MuralPage`) | `useItens(filtros)` | `itemService.listar` | `GET /api/itens` | Filtrar e visualizar lista de agasalhos |
| `/itens/:id` (`DetalhesItemPage`) | `useItem(id)` | `itemService.buscarPorId` | `GET /api/itens/{id}` | Carregar dados completos da peça |
| `/itens/:id` (`DetalhesItemPage`) | `useItemActions()` | `itemService.reservar` / `entregar` | `POST .../reserva` ou `.../entrega` | Atualizar ciclo de vida do item |
| `/itens/:id` (`DetalhesItemPage`) | `useItemActions()` | `itemService.excluir` | `DELETE /api/itens/{id}` | Botão "Excluir Doação" |
| `/itens/:id/editar` (`EditarItemPage`) | `useItem(id)` + `useItemActions()` | `itemService.buscarPorId` e `itemService.atualizar` | `GET /api/itens/{id}` e `PUT /api/itens/{id}` | Pré-popular e salvar alterações descritivas |
| `/itens/novo` (`CadastrarItemPage`) | `useDoadores()` + `useRegistrarDoacao()` | `doadorService.listar`, `doacaoService.criar` e `doacaoService.adicionarItem` | `GET /api/doadores`, `POST /api/doacoes` e `POST /api/doacoes/{id}/itens` | Escolher o doador e registrar a doação |
| `/itens/novo` (`CadastrarItemPage`) | `useMatchSugerido()` | `matchService.sugerir` | `POST /api/matches` | Ver a sugestão de abrigo depois de salvar |
| `/itens/:id` (`DetalhesItemPage`) | `useOrigemItem()` | `doacaoService.buscarPorId`, `doadorService.buscarPorId` e `abrigoService.buscarPorId` | `GET /api/doacoes/{id}`, `GET /api/doadores/{id}` e `GET /api/abrigos/{id}` | Mostrar doador e destino da peça |
| `/itens/:id` (`DetalhesItemPage`) | `useMatchSugerido()` + `useConfirmarMatch()` | `matchService.sugerir` e `matchService.confirmar` | `POST /api/matches` e `POST /api/matches/confirmacao` | Botão "Enviar ao abrigo" |
| `/abrigos` (`AbrigosPage`) | `useAbrigos()` | `abrigoService.listar` | `GET /api/abrigos` | Listar abrigos, estoque e déficit |
| `/abrigos/:id` (`DetalhesAbrigoPage`) | `useAbrigo(id)` + `useDemandaActions()` | `abrigoService.buscarPorId`, `criarDemanda`, `atualizarDemanda` e `removerDemanda` | `GET /api/abrigos/{id}` e `/api/abrigos/{id}/demandas` | Gerir demandas do abrigo |
| `/doadores` (`DoadoresPage`) | `useDoadores()` + `useCriarDoador()` | `doadorService.listar` e `doadorService.criar` | `GET /api/doadores` e `POST /api/doadores` | Listar e cadastrar doadores |

---

## 8. Diretrizes e Limites para a IA

1. **Nunca misture camadas:** `fetch` vive apenas em `services/`, lógica de estado em `hooks/`, validação de formulários em `schemas/`, e views em `pages/`.
2. **TypeScript Estrito:** Declare interfaces para cada requisição e resposta. Nunca use `any`.
3. **Consistência de Enums e Nomes:** Respeite rigorosamente a nomenclatura dos enums e campos do backend (`tipoPeca`, `nomeDoador`, `estadoConservacao`).
4. **Sem Lógica de Negócio nos Componentes UI:** Primitivas de `components/ui/` devem permanecer puras e acessíveis.
5. **Zero comentários desnecessários:** Siga a regra do projeto: código limpo, autoexplicativo, sem comentários redundantes.
6. **Integridade de Estado:** Ações de reserva e entrega devem respeitar a máquina de estados; desabilite ou oculte botões para estados inválidos (ex.: não exibir "Reservar" em itens já reservados ou entregues). Reservar e entregar também ficam desabilitados enquanto a peça não tem abrigo (`abrigoId` nulo).
