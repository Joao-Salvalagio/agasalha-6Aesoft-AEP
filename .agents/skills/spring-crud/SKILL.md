---
name: spring-crud
description: Implementar ou estender um recurso REST no Agasalha (Spring Boot + MongoDB) seguindo a arquitetura em camadas do projeto.
---

# spring-crud

## Antes de codar

- Ler `docs/arquitetura.md` e as `RN-NN` da tarefa em `docs/regras-negocio.md`.
- Ler `docs/padroes-codigo.md`.
- Confirmar o pacote raiz: `br.com.cesumar.agasalha`.
- Construir apenas as abstracoes que o caso de uso atual exige.

## Ordem de implementacao (obrigatoria)

```
1. Model        classe @Document, campos, invariantes de dominio (validar())
2. Repository    interface MongoRepository; MongoTemplate so quando a query for dinamica
3. Request DTO   record por operacao (Create, Update), Bean Validation
4. Response DTO  record do formato publico (Response e, quando fizer sentido, Summary)
5. Mapper        @Component, converte Request->Model e Model->Response, preserva id no update
6. Service       casos de uso e TODAS as regras de negocio; lanca excecoes de dominio
7. Controller    so protocolo HTTP; delega ao Service; status corretos
8. Exception     excecao de dominio + tratamento no @RestControllerAdvice (ApiError)
9. Tests         ver skill testing
```

## Regras

- Nenhum `@Document` cruza a fronteira HTTP. Sempre `Response DTO` pelo `mapper`.
- Regra de negocio so no `service`. `controller` sem regra e sem `try/catch` de
  fluxo normal. `mapper` sem regra e sem acesso a banco.
- Injecao por construtor (`@RequiredArgsConstructor`), campos `private final`.
- Um `Request DTO` por operacao; `POST` e `PUT` podem ter contratos diferentes.
- Status HTTP: `201` + `Location` na criacao; `200` em leitura e atualizacao;
  `204` na remocao; `404` recurso ausente; `400` entrada invalida; `409`
  transicao de estado invalida.
- Lombok contido conforme `docs/padroes-codigo.md`.
- Zero comentario.

## Ao terminar

Rodar a skill `revisao-arquitetura` e `./mvnw verify`. Se o contrato HTTP mudou
ou cresceu, adicionar a descricao ao fim de `docs/http-api.md` (append-only).
