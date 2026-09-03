---
name: nova-feature
description: Ponto de entrada de toda tarefa no Agasalha. Reconhece o colaborador, identifica a fatia dele, e conduz do escopo ao Pull Request seguindo as regras do repositorio.
---

# nova-feature

Use esta skill no comeco de qualquer tarefa. Ela orquestra; as skills
`spring-crud`, `testing` e `revisao-arquitetura` fazem o trabalho especifico.

## Onboarding por prompt

Se o colaborador disser algo como "quero fazer a AEP, sou o Bruno":

1. Identifique a pessoa em `docs/divisao-tarefas.md`, secao "Integrantes".
2. Descubra a entrega atual: sem a tag `v1.0` no repositorio, e Entrega 1; com
   `v1.0` e sem `v2.0`, e Entrega 2. Em duvida, pergunte.
3. Abra a fatia dessa pessoa nessa entrega e liste o que ainda falta, cruzando
   com as issues do board quando houver.
4. Escolha uma tarefa com a pessoa e siga os passos abaixo.

## Passos

1. **Ler a issue.** Escopo fechado e criterio de pronto escrito. Sem isso, pedir
   para o colaborador fechar o escopo antes.
2. **Ler as regras.** `docs/arquitetura.md` e as `RN-NN` de
   `docs/regras-negocio.md` que a tarefa cita. `docs/padroes-codigo.md`.
3. **O colaborador cria a branch** (`git checkout -b <tipo>/<slug>` a partir de
   `develop`). Voce nao roda `git`.
4. **Implementar** com a skill certa:
   - recurso ou endpoint REST -> `spring-crud`
   - so testes -> `testing`
   Gerar o codigo na ordem que a skill define; o colaborador revisa cada arquivo.
5. **Revisar** com a skill `revisao-arquitetura` e `./mvnw verify` local.
6. **Versionar.** Entregar ao colaborador os comandos e as mensagens de commit
   prontas (Conventional Commits, enxutas, no minimo 2 na tarefa) e o texto do
   PR. O colaborador executa. Comandos de referencia em `docs/versionamento.md`.

## Limites

Nao decidir sozinho: trocar stack, criar colecao, mudar contrato de endpoint,
alterar regra de negocio, pular teste, editar `.md` ja commitado. Nessas
situacoes, parar e levar para os 3 (ver `docs/fluxo-trabalho-ia.md`).
