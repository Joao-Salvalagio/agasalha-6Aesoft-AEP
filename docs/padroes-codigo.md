# Padrões de código

Convenções obrigatórias. A skill `revisao-arquitetura` verifica estas regras antes
de todo PR.

## Idioma

Identificadores (classes, métodos, variáveis, pacotes) e mensagens de erro em
**português**. Sem acento em nome de identificador.

## Zero comentário

**Nenhum comentário no código.** Nada de `//` nem `/* */` explicativo, em nenhum
arquivo — incluindo exemplos dentro de skills e docs.

Se um trecho parece precisar de comentário, o problema é outro: o nome está ruim,
o método é grande demais, ou a lógica devia estar num método com nome próprio.

```java
int p = q * 30;

int prazoEmDias(int quantidadeMeses) {
    return quantidadeMeses * DIAS_POR_MES;
}
```

O segundo bloco não precisa de comentário. O primeiro precisaria — e é por isso
que não é aceito.

## DTOs

- Todo DTO é um `record`.
- `Request` e `Response` são tipos diferentes. `POST` e `PUT` podem ter `Request`
  diferentes (`ItemCreateRequest`, `ItemUpdateRequest`).
- DTO não tem lógica além de validação declarativa (Bean Validation) e, no
  máximo, um construtor compacto.
- DTOs ficam em `controller/dto/` e são excluídos da cobertura.

## Lombok — uso contido

Permitido:

- `@Getter` / `@Setter` em classes `model`;
- `@RequiredArgsConstructor` em `service`, `controller`, `mapper` para injeção por
  construtor dos campos `final`;
- `@Builder` em DTO e `model` quando o construtor tiver muitos parâmetros;
- `@Slf4j` para logging.

Proibido:

- `@Data` em qualquer classe `@Document`;
- `@AllArgsConstructor` público em entidade;
- qualquer anotação Lombok que esconda regra de negócio (a regra fica num método
  com nome, não numa anotação).

## Injeção de dependência

Sempre por **construtor** (`@RequiredArgsConstructor` ou construtor explícito).
Nunca `@Autowired` em campo. Campos de dependência são `private final`.

## Organização

- Um tipo público por arquivo.
- Import sem wildcard (`import a.b.C;`, nunca `import a.b.*;`).
- Sem import não usado.
- Sem interface artificial: só extrair interface quando houver 2+ implementações
  reais em uso.
- Sem MapStruct, sem reflection para mapeamento — o `mapper` é escrito à mão.
- YAGNI: não criar abstração "por precaução".

## Testes

- Nome do método de teste: `metodo_situacao_resultadoEsperado`
  (`criar_itemInvalido_lancaExcecao`).
- Estrutura Arrange / Act / Assert.
- Cada teste é independente: sem estado compartilhado, sem `id` fixo de banco, sem
  depender de ordem de execução.
- O menor tipo de teste que dá confiança (ver `.agents/skills/testing`).

## Exceções

- Falhas de domínio são exceções próprias (`ItemNaoEncontradoException`,
  `TransicaoInvalidaException`, `DadosInvalidosException`).
- Tratamento centralizado no `@RestControllerAdvice`. Sem `try/catch` de fluxo
  normal no `controller`.
