# GIT

**Git** é um sistema de controle de versão distribuído, criado por Linus Torvalds em 2005. Ele permite rastrear alterações no código-fonte ao longo do tempo, facilitando a colaboração entre desenvolvedores e o gerenciamento do histórico de um projeto.

### Por que usar Git?

- **Histórico completo:** Cada alteração feita no projeto é registrada, permitindo voltar a qualquer ponto no tempo
- **Trabalho em equipe:** Vários desenvolvedores podem trabalhar no mesmo projeto simultaneamente sem conflitos
- **Ramificações (branches):** Permite criar linhas de desenvolvimento independentes para features, correções, etc.
- **Distribuído:** Cada desenvolvedor possui uma cópia completa do repositório, não dependendo de um servidor central

# Conceitos base do GIT

### Repositório

Um repositório Git é a pasta do seu projeto que está sendo rastreada pelo Git. Ele contém todos os arquivos do projeto e o histórico completo de alterações (armazenado na pasta oculta `.git`).

### Os três estados

O Git possui três estados principais nos quais os arquivos podem estar:

```
Working Directory  →  Staging Area  →  Repository
  (Modificado)        (Preparado)      (Commitado)
```

1. **Working Directory (Diretório de Trabalho):** Onde você edita seus arquivos normalmente
2. **Staging Area (Área de Preparação):** Onde você prepara as alterações que serão salvas no próximo commit
3. **Repository (Repositório):** Onde o histórico de commits é armazenado permanentemente

### Commit

Um commit é um "snapshot" (foto) do estado dos seus arquivos em um determinado momento. Cada commit possui:

- Um identificador único (hash SHA-1)
- Uma mensagem descritiva
- O autor e a data da alteração
- Um ponteiro para o commit anterior (parent)

### Branch

Uma branch é uma ramificação independente do desenvolvimento. A branch padrão é chamada de `main` (ou `master` em repositórios mais antigos).

# Iniciando projeto com GIT

### Instalação

Baixe o Git em: https://git-scm.com/downloads

Após instalar, configure seu nome e email:

```bash
git config --global user.name "Seu Nome"
git config --global user.email "seu@email.com"
```

### Criando um repositório

Para iniciar um novo repositório Git em um projeto existente:

```bash
git init
```

Esse comando cria a pasta oculta `.git` dentro do diretório, que armazena todo o histórico e configurações do repositório.

Para verificar o estado atual do repositório:

```bash
git status
```

# Salvando alterações

### Adicionando arquivos à Staging Area

Para preparar arquivos para o commit, usamos o `git add`:

```bash
# Adicionar um arquivo específico
git add arquivo.txt

# Adicionar todos os arquivos modificados
git add .
```

### Criando um commit

Após adicionar os arquivos à staging area, criamos um commit:

```bash
git commit -m "mensagem descrevendo a alteração"
```

### Fluxo completo

```bash
# 1. Verificar o que foi alterado
git status

# 2. Adicionar alterações à staging area
git add .

# 3. Criar o commit
git commit -m "feat: adiciona página de login"
```

### Boas práticas para mensagens de commit

- Seja descritivo e conciso
- Use o imperativo: "adiciona feature" em vez de "adicionei feature"
- Prefixos comuns (Conventional Commits):
  - `feat:` nova funcionalidade
  - `fix:` correção de bug
  - `docs:` alterações na documentação
  - `style:` formatação, ponto e vírgula, etc.
  - `refactor:` refatoração de código
  - `test:` adição ou correção de testes

# Manipulando histórico de alterações

### Visualizando o histórico

```bash
# Ver o log completo de commits
git log

# Ver o log resumido (uma linha por commit)
git log --oneline

# Ver o log com gráfico de branches
git log --oneline --graph
```

### Desfazendo alterações

```bash
# Descartar alterações no working directory (arquivo específico)
git checkout -- arquivo.txt

# Remover arquivo da staging area (sem perder as alterações)
git reset arquivo.txt

# Voltar para um commit anterior (mantendo alterações)
git reset --soft HEAD~1

# Voltar para um commit anterior (descartando alterações)
git reset --hard HEAD~1
```

### Visualizando diferenças

```bash
# Ver diferenças no working directory
git diff

# Ver diferenças na staging area
git diff --staged
```

# Ramificações

Branches permitem criar linhas de desenvolvimento independentes. Isso é essencial para trabalhar em novas features sem afetar o código principal.

### Comandos de branch

```bash
# Listar branches
git branch

# Criar uma nova branch
git branch nome-da-branch

# Trocar para outra branch
git checkout nome-da-branch

# Criar e trocar para uma nova branch (atalho)
git checkout -b nome-da-branch
```

### Merge (Mesclando branches)

Quando uma feature está pronta, mesclamos a branch de volta na branch principal:

```bash
# Primeiro, vá para a branch que receberá as alterações
git checkout main

# Depois, faça o merge da branch desejada
git merge nome-da-branch
```

### Resolvendo conflitos

Conflitos acontecem quando duas branches alteram a mesma parte de um arquivo. O Git marca os conflitos assim:

```
<<<<<<< HEAD
código da branch atual
=======
código da branch que está sendo mesclada
>>>>>>> nome-da-branch
```

Para resolver: edite o arquivo mantendo o código correto, remova os marcadores de conflito, e faça um novo commit.

### Fluxo de trabalho com branches

```
main ─────●─────●─────────────●──────
           \                 /
feature     ●─────●─────●──
```

1. Crie uma branch a partir da `main`
2. Desenvolva a feature na nova branch
3. Faça o merge de volta para a `main`

# Repositório remoto - GITHUB

**GitHub** é uma plataforma de hospedagem de repositórios Git na nuvem. Ele permite compartilhar código, colaborar com outros desenvolvedores e gerenciar projetos.

### Conectando a um repositório remoto

```bash
# Adicionar um repositório remoto
git remote add origin https://github.com/usuario/repositorio.git

# Verificar repositórios remotos configurados
git remote -v
```

### Enviando alterações (Push)

```bash
# Enviar commits para o repositório remoto
git push origin main

# Enviar uma nova branch para o remoto
git push origin nome-da-branch

# Configurar upstream e enviar (primeira vez)
git push -u origin main
```

### Baixando alterações (Pull)

```bash
# Baixar e mesclar alterações do remoto
git pull origin main
```

### Clonando um repositório

Para baixar um repositório existente do GitHub:

```bash
git clone https://github.com/usuario/repositorio.git
```

### Pull Request (PR)

O Pull Request é um mecanismo do GitHub para propor alterações. O fluxo é:

1. Crie uma branch e faça suas alterações
2. Envie a branch para o GitHub (`git push origin nome-da-branch`)
3. No GitHub, abra um Pull Request da sua branch para a `main`
4. Outros desenvolvedores revisam o código
5. Após aprovação, o merge é feito pelo GitHub

### Fork

Fork é uma cópia de um repositório de outra pessoa para a sua conta do GitHub. É usado para contribuir com projetos open source:

1. Faça o fork do repositório no GitHub
2. Clone o fork para sua máquina
3. Crie uma branch e faça suas alterações
4. Envie para o seu fork e abra um Pull Request para o repositório original

# Recapitulando - Comandos essenciais

| Comando | Descrição |
|---|---|
| `git init` | Inicializa um novo repositório |
| `git status` | Mostra o estado atual dos arquivos |
| `git add .` | Adiciona todos os arquivos à staging area |
| `git commit -m "msg"` | Cria um commit com uma mensagem |
| `git log --oneline` | Mostra o histórico resumido de commits |
| `git branch` | Lista as branches |
| `git checkout -b nome` | Cria e muda para uma nova branch |
| `git merge nome` | Mescla uma branch na branch atual |
| `git remote add origin url` | Conecta a um repositório remoto |
| `git push origin main` | Envia commits para o remoto |
| `git pull origin main` | Baixa alterações do remoto |
| `git clone url` | Clona um repositório existente |

### Fluxo diário resumido

```bash
# Começar uma nova feature
git checkout -b minha-feature

# Trabalhar nos arquivos...

# Salvar alterações
git add .
git commit -m "feat: descrição da alteração"

# Enviar para o remoto
git push origin minha-feature

# Após aprovação, voltar para main e fazer merge
git checkout main
git pull origin main
git merge minha-feature
git push origin main
```
