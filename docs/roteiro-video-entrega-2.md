# Agasalha — Roteiro do Vídeo, Entrega 2

AEP Engenharia de Software · 6º Semestre · 2026.2 · Segunda Entrega
**Duração obrigatória: 3 a 5 minutos · Alvo: 4:15 · Vale 0,2 de 1,0 ponto**

Dois narradores: Bruno e Eric. Cada um narra a parte que construiu. Não é preciso
aparecer no vídeo — gravação de tela com narração resolve.

---

## 1. O que o edital cobra do vídeo da Entrega 2

A tabela de critérios (seção 6) é a que dá nota; a seção 10 detalha. As duas
listam coisas diferentes, e o roteiro abaixo cobre as duas.

| Origem | Texto literal do edital |
|---|---|
| Seção 6, tabela de critérios (0,2) | "Apresenta evolução, arquitetura final, execução da PoC, tecnologias, testes e principais resultados, dentro do tempo previsto." |
| Seção 10, detalhamento | "A evolução da solução. O projeto final. A demonstração da PoC. As tecnologias utilizadas. Os testes automatizados e a cobertura obtida. Os principais resultados alcançados." |

**A diferença crucial em relação à Entrega 1:** tecnologias, testes e cobertura
eram proibidos de gastar tempo no vídeo anterior, porque não valiam nota lá. Aqui
eles são **cobrados explicitamente nas duas listas**. Guardem o print do JaCoCo.

São seis itens em 3 a 5 minutos. O tempo é apertado — por isso o alvo é 4:15, e
não 3:10.

---

## 2. Antes de gravar

1. **Limpe os dados.** `docker compose down --volumes && docker compose up -d`
   para voltar ao seed com abrigos e demandas. Dado de teste sujo passa má
   impressão.
2. **Rode `./mvnw verify` e deixe o relatório do JaCoCo aberto** em
   `target/site/jacoco/index.html`. O número da cobertura precisa aparecer na
   tela, não só ser dito.
3. **Deixe o quadro de tarefas aberto numa aba.** Ele vale 0,1 num critério
   próprio, e mostrar por três segundos no vídeo não custa nada.
4. **Suba os serviços na ordem:** `docker compose up -d`, depois
   `./mvnw spring-boot:run`, depois `cd apps/web && npm run dev`.
5. **Abas prontas antes do REC:** mural em `localhost:5173`, diagrama de
   arquitetura final, relatório do JaCoCo, quadro de tarefas, Swagger em `/docs`.
6. **Grave em 1920×1080**, tela cheia, fonte do navegador no tamanho padrão.
   Feche notificações, abas pessoais e qualquer coisa com dado privado.
7. **Ensaie uma vez inteiro, cronometrando.** A demonstração é onde o tempo
   estoura.

---

## 3. Roteiro cronometrado

### Eric — Evolução e arquitetura final · `0:00 → 1:30`

**NA TELA**
Slide de abertura (o mesmo da Entrega 1, com o selo do ODS 1). Depois o diagrama
de arquitetura final de `docs/arquitetura.md`, mostrando as quatro coleções e os
relacionamentos.

**O QUE FALAR**

> "Agasalha é a nossa Prova de Conceito para a AEP de Engenharia de Software,
> alinhada ao ODS 1, Erradicação da Pobreza: um mural digital que conecta quem
> quer doar agasalho a quem precisa.
>
> Na primeira entrega tínhamos uma coleção, `itens`, com CRUD e o ciclo de vida do
> item. Esta segunda entrega evoluiu o modelo inteiro.
>
> Agora são quatro coleções. `abrigos` guarda cada abrigo com suas demandas
> **aninhadas** — uma lista de subdocumentos dentro do próprio documento do
> abrigo, com o endereço também aninhado. `doadores` e `doacoes` foram extraídas
> para coleções próprias, e é aí que estão os relacionamentos: uma doação
> referencia o doador e o abrigo, e cada item referencia a doação que o trouxe e,
> quando o matching casa, o abrigo de destino.
>
> A arquitetura em camadas da primeira entrega se manteve: React conversa por HTTP
> com a API; o Controller valida a entrada, o Service concentra as regras, e o
> Repository é a única camada que fala com o banco. Nenhum documento do banco é
> devolvido direto na resposta — tudo passa por um DTO."

**ORIENTAÇÃO**
Diga as palavras **"múltiplas coleções"**, **"relacionamento"** e **"documentos
aninhados"** com todas as letras. São os três requisitos da seção 8 do edital, e o
avaliador está procurando exatamente por eles. Mostre o documento de um abrigo no
mongo-express ou no Swagger enquanto fala de aninhamento — uma imagem do JSON com
o `demandas[]` dentro vale mais que a frase.

---

### Bruno — Demonstração da PoC e matching · `1:30 → 3:15`

**NA TELA**
O mural rodando em `localhost:5173`, seed limpo. Nada além do navegador.

**SEQUÊNCIA — DEIXE A TELA FALAR, NARRE POUCO**

1. Mural aberto com os itens e os badges de status
2. Abrir a tela de **abrigos**: cada abrigo com suas demandas e a barra de progresso
3. Abrir **doadores** e cadastrar um doador (ou usar um do seed)
4. Registrar uma doação escolhendo esse doador no dropdown, com uma peça que **casa**
   com uma demanda em aberto
5. Nos detalhes da peça, mostrar o **match sugerido**: "Abrigo X" — e que Reservar e
   Confirmar entrega estão desabilitados, porque a peça ainda não tem abrigo
6. Clicar em **Enviar ao abrigo** e abrir aquele abrigo para mostrar a barra de
   progresso subindo
7. Voltar à peça: agora Reservar e Confirmar entrega estão liberados. Reservar e entregar
8. Registrar uma peça que **não casa** (tamanho ou gênero incompatível) e mostrar que
   nenhum match é sugerido
9. Apontar que "Editar" desabilita sozinho depois da entrega

**O QUE FALAR (POR CIMA DA DEMONSTRAÇÃO)**

> "Esta é a solução funcionando. Além do mural da primeira entrega, agora existe a
> tela de abrigos: cada abrigo tem suas demandas, e a barra mostra o quanto de
> cada demanda já foi atendido — o déficit.
>
> Uma doação agora nasce de um doador cadastrado. Ao abrir a peça, o motor de
> matching compara tamanho e gênero, respeita o limite de estoque do abrigo, só
> considera demanda que ainda está em aberto, e entre os abrigos compatíveis
> prioriza o de **maior déficit** — quem está mais precisando recebe primeiro.
>
> O match é uma sugestão. Quando enviamos a peça ao abrigo, ela entra no estoque e
> a barra daquele abrigo sobe na hora. Só a partir daí a peça pode ser reservada e
> entregue.
>
> E aqui uma peça que não casa com nenhuma demanda: nenhuma sugestão. O matching
> não força.
>
> O ciclo de vida do item continua sendo uma máquina de estados validada no
> servidor: tentar reservar uma peça sem abrigo, ou já entregue, devolve 409 pela
> API, não só um aviso na tela."

**ORIENTAÇÃO**
Este é o bloco mais longo e o que cobre "execução da PoC" e "principais
resultados". Mova o mouse devagar e pause meio segundo depois de cada clique.

O passo 8 — a peça que **não** casa — parece desperdício de tempo, mas é o que
prova que existe regra de verdade e não um match sempre positivo. Vale os cinco
segundos.

**É aqui que o tempo estoura.** Ensaie este bloco isolado até fechar em 105
segundos.

---

### Eric — Tecnologias, testes e cobertura · `3:15 → 4:00`

**NA TELA**
Relatório do JaCoCo em `target/site/jacoco/index.html`, depois a aba de Actions do
GitHub com o CI verde, depois o quadro de tarefas.

**O QUE FALAR**

> "As tecnologias: a API é Java 21 com Spring Boot, build em Maven. O banco é
> MongoDB, rodando em Docker Compose. O mural é React com Vite e Tailwind. A
> documentação da API é gerada pelo springdoc, em `/docs`.
>
> Os testes são JUnit 5 com Mockito para a lógica pura, e Testcontainers para os
> testes de integração — eles sobem um MongoDB real em container, então o que é
> testado é o comportamento de verdade, não um mock do banco.
>
> A cobertura medida pelo JaCoCo está em [X]% de linha. O mínimo do edital é 70%,
> e o gate está configurado no build: se a cobertura cair abaixo disso, o
> `mvnw verify` quebra. O pipeline no GitHub Actions roda a cada push.
>
> O trabalho foi organizado num quadro de tarefas, com cada tarefa virando branch,
> Pull Request e revisão do outro integrante."

**ORIENTAÇÃO**
Troque `[X]%` pelo número real na hora da gravação — não chute. Deixe o número
**visível na tela** enquanto fala; o critério pede evidência reproduzível, e o
avaliador quer ver, não só ouvir.

Os três itens deste bloco (tecnologias, testes, cobertura) são os que **não
existiam** no vídeo da Entrega 1. Não corte este bloco para ganhar tempo — corte
texto dos outros.

---

### Bruno — Resultados e fechamento · `4:00 → 4:15`

**NA TELA**
Volta ao slide inicial, ou o repositório aberto no GitHub na tag `v2.0`.

**O QUE FALAR**

> "Fechando: a Prova de Conceito saiu de uma coleção com CRUD para quatro coleções
> com relacionamento e documentos aninhados, mais um motor de matching que decide
> para qual abrigo cada agasalho deve ir.
>
> O resultado é o que o problema pedia: quem doa passa a saber o que está
> faltando, e quem distribui passa a ter visibilidade do estoque.
>
> O código e a documentação estão no GitHub, na tag `v2.0`."

**ORIENTAÇÃO**
Curto e seco. Não introduza assunto novo no fechamento. Se o vídeo estiver perto
de 5:00, corte a frase do meio.

---

## 4. Checklist antes de enviar

- [ ] O vídeo dura **entre 3:00 e 5:00** — cronometrado, não estimado
- [ ] A **evolução** em relação à Entrega 1 foi dita explicitamente
- [ ] As palavras "múltiplas coleções", "relacionamento" e "aninhados" apareceram
- [ ] A **arquitetura final** foi mostrada, não só descrita
- [ ] A PoC foi **executada ao vivo**
- [ ] As **tecnologias** foram listadas
- [ ] Os **testes** foram explicados e a **cobertura** apareceu na tela com o número
- [ ] O **quadro de tarefas** apareceu
- [ ] Os **principais resultados** foram ditos no fechamento
- [ ] Áudio audível e sem ruído nos dois trechos, mesmo volume de microfone
- [ ] Nenhuma informação pessoal visível na tela
- [ ] Subido no YouTube como **"Não listado"** (não "Privado")
- [ ] Link testado em **janela anônima**
- [ ] Tag `v2.0` criada e visível no GitHub antes de gravar o fechamento
- [ ] Ficha de identificação preenchida com o link do vídeo

### O erro mais caro possível

Um vídeo excelente de 5 minutos e 20 segundos arrisca o critério inteiro, porque
"dentro do tempo previsto" está escrito na rubrica. São 0,2 de 1,0 ponto — o maior
critério isolado da Entrega 2. Se estourar, corte texto; nunca corte a
demonstração nem o bloco de testes e cobertura.

---

Agasalha · PoC ODS 1 — Erradicação da Pobreza
Repositório: github.com/Joao-Salvalagio/agasalha-6Aesoft-AEP
Equipe da Entrega 2: Bruno Koji Fujisaki · Eric Delefrati Rocha Leite
