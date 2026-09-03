---
name: revisao-arquitetura
description: Revisar uma mudanca no Agasalha antes de abrir o Pull Request. Rodar apos implementar ou alterar Controller, DTO, Mapper, Service, Repository, Model, excecoes, configuracao ou testes.
---

# revisao-arquitetura

Percorrer o checklist. Corrigir o que falhar, rodar `./mvnw clean verify` e
revisar o diff final. Nao declarar pronto com teste vermelho nem sem explicar uma
limitacao de ambiente.

## Fluxo esperado

Entrada: `Controller -> Request DTO -> Service -> Repository -> MongoDB`
Saida: `MongoDB -> Model -> Service -> Mapper -> Response DTO -> Controller -> JSON`

## Checklist

### Controller
- sem regra de negocio, sem acesso direto a Repository, sem `try/catch` de fluxo
  normal;
- usa DTOs; status HTTP semanticamente corretos (`201`+`Location`, `200`, `204`,
  `404`, `400`, `409`).

### DTO e Model
- nenhum `@Document` exposto na resposta;
- cada DTO representa um caso de uso claro, nao e generico por conveniencia;
- validacao de entrada nos `Request DTO`;
- Model so com estado e invariantes de dominio.

### Service, Repository e Mapper
- Service concentra casos de uso e regras;
- Repository so persistencia e consultas;
- Mapper sem regra e sem acesso a banco, preserva `id` no update;
- sem acoplamento desnecessario, sem metodo gigante, sem duplicacao.

### Excecoes
- falhas relevantes tratadas;
- resposta de erro consistente e centralizada (`ApiError` no `@RestControllerAdvice`).

### Dependencias e codigo
- sem dependencia ou abstracao desnecessaria;
- sem interface artificial, sem Lombok fora do escopo de `docs/padroes-codigo.md`,
  sem MapStruct;
- **nenhum comentario**;
- sem import nao usado, sem nome inconsistente.

### Testes
- ha teste para o comportamento novo ou corrigido;
- cobre sucesso, ausencia, validacao e contrato HTTP aplicavel;
- independente de ordem, de estado externo e do Compose de desenvolvimento;
- sem teste que verifica implementacao trivial sem comportamento.

### Documentacao
- `README.md`, `HARNESS.md`, `docs/arquitetura.md`, `docs/decisoes.md`,
  `docs/regras-negocio.md`, `docs/http-api.md` coerentes com o codigo;
- contrato novo adicionado ao fim de `docs/http-api.md` (append-only);
- limitacoes reais registradas no PR.

## Conclusao

`./mvnw clean verify` verde, cobertura >= 70%, diff revisado.
