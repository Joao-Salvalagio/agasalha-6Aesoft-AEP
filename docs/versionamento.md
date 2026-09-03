# Versionamento — Playbook de Git para o time Agasalha

Guia prático de como versionar neste repositório. Escrito para quem ainda não
trabalhou com **gitflow + Conventional Commits + `main`/`develop` protegidas por
Pull Request**. Serve também de referência para a IA de cada colaborador ajudar no
versionamento.

Complementa `CONTRIBUTING.md` (as regras) e `docs/fluxo-trabalho-ia.md` (como
conduzir a IA). Aqui é o **passo a passo dos comandos**.

> Regra de ouro: **a IA nunca roda `git` ou `gh`.** Ela te entrega os comandos e a
> mensagem de commit pronta. Quem executa é você.

---

## 1. Modelo mental

```
main      ← só releases. Recebe tag v1.0 / v2.0. NINGUÉM commita aqui direto.
develop   ← integração do time. NINGUÉM commita aqui direto.
<tipo>/<slug>  ← sua branch de trabalho. Sai de develop, volta por Pull Request.
```

- `main` e `develop` estão **bloqueadas**: o GitHub recusa `git push` direto.
  A única forma de mudar essas branches é abrir um **Pull Request (PR)** e outro
  membro aprovar, com a **CI verde**.
- Você **sempre** trabalha numa branch própria. Uma branch por tarefa (issue).
- Quando a tarefa termina: PR da sua branch → `develop`.

---

## 2. Uma vez só (primeira máquina)

```bash
git clone https://github.com/Joao-Salvalagio/agasalha-6Aesoft-AEP.git agasalha
cd agasalha
git config user.name "Seu Nome"
git config user.email "seu-email@exemplo.com"
```

Confirme que enxerga as duas branches remotas:

```bash
git fetch origin
git branch -avv
```

Deve listar `origin/main` e `origin/develop`.

---

## 3. O ciclo de uma tarefa (do início ao merge)

### 3.1 Atualizar o `develop` local

Sempre comece daqui, para sua branch nascer do estado mais novo:

```bash
git checkout develop
git pull origin develop
```

### 3.2 Criar a branch da tarefa

O `<tipo>` é o mesmo dos Conventional Commits. `<slug>` em kebab-case, curto.

| `<tipo>` | Quando usar |
|---|---|
| `feat` | funcionalidade nova |
| `fix` | correção de bug |
| `docs` | só documentação |
| `test` | só testes |
| `refactor` | reorganizar código sem mudar comportamento |
| `build` | dependências, Maven, empacotamento |
| `ci` | GitHub Actions |
| `chore` | infra, configuração, tarefa de repositório |

```bash
git checkout -b feat/item-crud
```

### 3.3 Trabalhar e commitar (mínimo 2 commits por tarefa)

Faça o trabalho em pedaços. **Commite conforme avança**, não um "commitão" no fim.

Ver o que mudou:

```bash
git status
git diff
```

Adicionar só o que é daquele commit (evite `git add .` no automático):

```bash
git add src/main/java/br/com/cesumar/agasalha/model/ItemAgasalho.java
git commit -m "feat(item): adiciona entidade ItemAgasalho com validacao"
```

Próximo pedaço:

```bash
git add src/test/java/br/com/cesumar/agasalha/model/ItemAgasalhoTest.java
git commit -m "test(item): cobre validacao de campos obrigatorios"
```

**Formato da mensagem** (Conventional Commits, enxuta):

```
<tipo>(escopo opcional): resumo no imperativo, sem ponto final
```

- título com no máximo ~72 caracteres
- imperativo: "adiciona", "corrige", "remove" — não "adicionado", "adicionei"
- escopo é opcional: `feat(item):`, `fix(matching):`, ou só `docs:`
- corpo só quando explica um "porquê" que o título não cabe

Bons exemplos:

```
feat(item): adiciona maquina de estados do agasalho
fix: rejeita contato vazio no cadastro de item
test(status): cobre transicoes invalidas
docs: registra ADR-013 sobre indice de tamanho
build: sobe cobertura minima do jacoco para 75
```

### 3.4 Antes de publicar: rodar a verificação

```bash
docker compose up -d
./mvnw clean verify
```

`verify` tem que passar e a cobertura de linha tem que ser ≥ 70%. Rode também a
skill `revisao-arquitetura` (ver `.agents/skills/`).

### 3.5 Sincronizar com o `develop` (se demorou ou outros mergearam)

Traga o que entrou em `develop` para dentro da sua branch **antes** do PR:

```bash
git checkout develop
git pull origin develop
git checkout feat/item-crud
git merge develop
```

Se aparecer conflito: o git marca os arquivos. Abra cada um, resolva os trechos
`<<<<<<< / ======= / >>>>>>>`, depois:

```bash
git add <arquivo-resolvido>
git commit
```

(commit de merge pode manter a mensagem padrão).

### 3.6 Publicar a branch

```bash
git push -u origin feat/item-crud
```

O `-u` só na primeira vez. Depois é só `git push`.

### 3.7 Abrir o Pull Request

Pelo navegador (o GitHub mostra um botão "Compare & pull request" após o push) ou
pelo terminal:

```bash
gh pr create --base develop --title "feat(item): CRUD do agasalho" --body "Fecha #12"
```

- **base** é sempre `develop` (nunca `main`, exceto os PRs de release).
- Preencha o template que aparece (checklist).
- `Fecha #12` / `Closes #12` no corpo liga o PR à issue e fecha ela no merge.

### 3.8 Revisão

- **Outro** membro revisa. O autor não aprova o próprio PR.
- A CI roda sozinha. Se ficar vermelha, abra a aba **Actions**, leia o erro,
  corrija, `git push` de novo (o PR atualiza sozinho).
- Ajustes pedidos na revisão: faça novos commits na mesma branch e `git push`.

### 3.9 Merge

Depois de 1 aprovação + CI verde: quem revisou (ou o autor) clica **Merge pull
request** no GitHub. Use **"Squash and merge"** ou **"Merge commit"** — combine com
o time e mantenha o mesmo para todos os PRs.

### 3.10 Limpeza

```bash
git checkout develop
git pull origin develop
git branch -d feat/item-crud
git push origin --delete feat/item-crud
```

---

## 4. Revisar o PR de outra pessoa

```bash
git fetch origin
gh pr checkout 15          # baixa a branch do PR #15
./mvnw clean verify        # roda os testes localmente
```

No GitHub: aba **Files changed** → comentar linha a linha → **Review changes** →
`Approve` ou `Request changes`. Aprovar significa "eu rodei/li isto e confio".

---

## 5. Erros comuns e como sair deles

| Mensagem / situação | O que aconteceu | Solução |
|---|---|---|
| `! [remote rejected] ... protected branch` | você tentou `push` direto em `main` ou `develop` | crie uma branch: `git checkout -b fix/algo` e faça o PR |
| `! [rejected] ... (non-fast-forward)` | alguém empurrou antes de você na mesma branch | `git pull` (ou `git pull --rebase`) e `push` de novo |
| PR diz "This branch has conflicts" | `develop` andou e bateu com seus arquivos | seção 3.5 (merge do develop na sua branch, resolve, push) |
| commitei na branch errada (`develop`) | esqueceu de criar a branch | `git branch feat/algo` → `git reset --hard origin/develop` (na develop) → `git checkout feat/algo` |
| commitei arquivo que não devia (`target/`, `.env`) | `git add .` pegou tudo | `git rm --cached <arquivo>` → commit; confira o `.gitignore` |
| mensagem de commit errada (último commit, **ainda não deu push**) | — | `git commit --amend -m "nova mensagem"` |
| quero desfazer o último commit mantendo os arquivos (**ainda não deu push**) | — | `git reset --soft HEAD~1` |
| CI vermelha no PR | teste quebrou ou cobertura < 70% | aba **Actions**, ler o log, corrigir, `git push` |

**Nunca** faça `git push --force` numa branch que outra pessoa também usa. Em
branch sua e só sua, e só se souber o que está fazendo, `git push --force-with-lease`.

---

## 6. O que nunca fazer

- commitar direto em `main` ou `develop`
- `git add .` sem olhar o `git status` antes
- commitar `target/`, `.env`, credenciais, `docs/superpowers/`, o edital
- abrir PR sem rodar `./mvnw verify`
- aprovar o próprio PR
- `git push --force` em branch compartilhada
- reescrever/apagar conteúdo de um `.md` já commitado (ver `CONTRIBUTING.md` —
  documentos são append-only)

---

## 7. Glossário rápido

| Termo | O que é |
|---|---|
| **branch** | uma linha de trabalho independente |
| **commit** | um ponto salvo no histórico, com mensagem |
| **push** | enviar seus commits para o GitHub |
| **pull** | trazer commits do GitHub para o seu repositório local |
| **fetch** | baixar as referências do GitHub sem mexer no seu trabalho |
| **merge** | juntar o histórico de uma branch em outra |
| **PR (Pull Request)** | pedido de merge revisável no GitHub |
| **CI** | GitHub Actions rodando `./mvnw verify` a cada push no PR |
| **fast-forward** | merge sem commit de junção, quando não houve divergência |
| **HEAD** | onde você está agora no histórico |
| **origin** | o apelido do repositório remoto no GitHub |
