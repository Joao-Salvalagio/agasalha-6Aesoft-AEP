---
name: testing
description: Escrever e ajustar testes automatizados no Agasalha com JUnit 5, Mockito, MockMvc e Testcontainers, escolhendo o menor tipo de teste que da confianca.
---

# testing

## Principio

Identificar o comportamento a garantir e escolher o **menor tipo de teste** que
da confianca. Cobrir caminho de sucesso, falhas relevantes e as bordas do
contrato. Nao testar implementacao trivial sem comportamento.

## Tipos

### Unitario (regra de dominio e de Service)

- Sem Spring, sem MongoDB. JUnit 5 + Mockito.
- Mockar apenas as dependencias diretas (`Repository`, `MongoTemplate`, outros
  Services).
- Estrutura Arrange / Act / Assert. Verificar retorno, excecao e interacoes
  relevantes.
- Alvo: `validar()` do model, `StatusTransitionService`, `ItemService`,
  `ItemStatusService`, `MatchingService`.

### Controller (contrato HTTP)

- `@WebMvcTest` + `MockMvc`, com o Service mockado.
- Verificar status, corpo JSON, serializacao, validacao (`@Valid`), tratamento de
  erro (`ApiError`).
- Nao repetir aqui regra ja coberta no teste unitario do Service.

### Integracao (fluxo completo)

- `@SpringBootTest` + `MockMvc` + Testcontainers-MongoDB (um container por
  suite, `@DynamicPropertySource`).
- Exercitar Controller -> Service -> Repository -> MongoDB de verdade.
- Independente de estado instalado, de dados persistidos e de ordem. Limpar a
  colecao no `@BeforeEach`. Sem `id` fixo. Sem perfil de dados de
  desenvolvimento.
- Nome de arquivo termina em `IT` (roda no `verify` pelo failsafe).

## Nomeacao

`metodo_situacao_resultadoEsperado`, por exemplo
`reservar_itemEntregue_lancaTransicaoInvalida`.

## Cobertura

`./mvnw verify` gera o JaCoCo e falha abaixo de 70% de linha. Cobrir sucesso,
ausencia (`404`), validacao (`400`) e transicao invalida (`409`) ja costuma
passar de 70% com folga.
