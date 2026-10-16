# Claude Code

## Sumário

- [CLAUDE.md](#claudemd)
  - [Hierarquia CLAUDE.md](#hierarquia-claudemd)
- [Modelos](#modelos)
- [Janela de Contexto](#janela-de-contexto)
  - [Compactação de contexto](#compactação-de-contexto)
- [Tools](#tools)
- [Skills](#skills)
- [Commands](#commands)
  - [Diferença para Skills](#diferença-para-skills)
- [Super Powers](#super-powers)
  - [Brainstorm](#brainstorm)
- [Plan Mode x Direct Mode](#plan-mode-x-direct-mode)
- [Path-Specific Rules](#path-specific-rules)
- [Effort](#effort)
- [Rodando multiplos agentes](#rodando-multiplos-agentes)
  - [Worktrees](#worktrees)
  - [Single Workspace](#single-workspace)
  - [Multiple Workspace](#multiple-workspace)
  - [Subagentes](#subagentes)
  - [Worktrees vs Subagentes — qual usar?](#worktrees-vs-subagentes--qual-usar)
- [Conductor](#conductor)
- [SEMANA FREE de CLAUDE CODE](#semana-free-de-claude-code)
- [Referências](#referências)

---

O Claude Code é uma ferramenta da Anthropic, segundo eles:

> é um assistente interativo que funciona no seu terminal. Embora seja excelente em programação, ele pode ajudar em tudo o que você pode fazer pela linha de comando: escrever documentação, executar compilações, pesquisar arquivos, pesquisar tópicos e muito mais.

Por padrão essa ferramenta é utilizada via terminal, sem interface gráfica, porém eles disponibilizaram recentemente uma nova aba dentro do aplicativo do Claude onde você acessa o Claude Code via interface.

## CLAUDE.md

O arquivo CLAUDE.md é um arquivo de contexto para o Claude, que irá informar para o modelo as principais regras e diretrizes que ele deve seguir. 

Sempre quando um novo chat é iniciado com o Claude, ele lê esse arquivo em busca desse contexto e regras. Esse arquivo informa ao Claude Code exatamente como sua base de código funcionam, suas convenções, seus padrões, suas regras e suas preferências.

Essas regras podem ser aplicadas a nível de usuário, projeto ou diretório.

### Hierarquia CLAUDE.md 

Existem três níveis e entender a diferença entre eles é importante.

- *User-level* fica em ~/.claude/CLAUDE.md. É pessoal e exclusivo seu. Suas preferências, seus atalhos, seu estilo. Não é versionado pelo git e seus colegas de equipe nunca vão ver esse arquivo.

- *Project-level* fica em .claude/CLAUDE.md na raiz do seu repositório. É compartilhado com toda a equipe através do git. Padrões do time, decisões de arquitetura e regras universais ficam aqui.

- *Directory-level* fica dentro de diretórios específicos. Essas regras se aplicam apenas quando o Claude Code está trabalhando em arquivos daquele diretório. Ideal para sub-projetos ou módulos com convenções diferentes.


Exemplo de [CLAUDE.MD](example.md)

## Modelos

o Claude Code usa os modelos da Anthropic como Claude Opus 4.7, Sonnet 4.6 e Haku.

Para trocar o model que o agente irá utilizar, é só usar o comando 

```
/model
```

## Janela de Contexto

A Janela de Contexto atual do Claude é de 200k tokens.

A janela de contexto do Claude armazena toda a sua conversa, incluindo todas as mensagens, todos os arquivos que o Claude lê e todas as saídas de comando. No entanto, ela pode se encher rapidamente. Uma única sessão de debug ou research do código-fonte pode gerar e consumir dezenas de milhares de tokens.

Isso é importante, pois o desempenho do LLM se degrada à medida que o contexto se enche. Quando a janela de contexto está ficando cheia, o Claude pode começar a "esquecer" instruções anteriores ou cometer mais erros. A janela de contexto é o recurso mais importante a ser gerenciado. 

Quando a janela fica cheia, o Claude força você a executar o comando ```/compat``` para compactar o contexto.

[Demonstração de como a janela de contexto se enche](https://code.claude.com/docs/en/context-window)

### Compactação de contexto

Como funciona o mecanismo de compactação:

- Arquivo CLAUDE.md raiz do projeto e regras sem escopo - São reinjetados do disco
- Memória automática - Reinjetada do disco
- Regras com caminhos de arquivo - Perdidas até que um arquivo correspondente seja lido novamente
- Arquivos CLAUDE.md aninhados em subdiretórios -  Perdidos até que um arquivo nesse subdiretório seja lido novamente
- Skills bodies que foram invocadas - Reinjetados, limitados a 5.000 tokens por habilidade e 25.000 tokens no total; os mais antigos são descartados primeiro
- Hooks - Não aplicável; hooks são executados como código, não como contexto


## Tools

As Tools são o que tornam o Claude Code agêntico e não só um chat de texto.

Resumindo, com as tools o Claude pode agir: ler seu código, editar arquivos, executar comandos, pesquisar na web e interagir com serviços externos. Cada uso de ferramenta retorna informações que retroalimentam o ciclo, orientando a próxima decisão de Claude.

## Skills

Skills são pastas de instruções reutilizáveis que o Claude carrega dinamicamente quando reconhece que a tarefa pede aquela expertise. Na prática, uma skill é só uma pasta com um arquivo SKILL.md na raiz, contendo:

- YAML frontmatter com name e description (obrigatórios)
- Instruções em markdown ensinando o Claude a executar a tarefa
- Opcionalmente: scripts/, references/, assets/, templates, exemplos

A boa prática é criar uma skill para cada tarefa repetitiva que você passa para o Claude. Então se tem alguma ação que você sempre pede pro Claudinho fazer da mesma forma, você pode criar uma skill.

Você pode criar novas skills com a ajuda do Claude, usando a skill de criar skills ```/skill-creator```

Para cada Prompt, o Claude já determina quais habilidades são relevantes e carrega as informações necessárias para concluir a tarefa, ajudando a evitar a sobrecarga da janela de contexto. Mas você pode invocar skills diretamenta, usando ```\NOME_DA_SKILL```

## Commands

Crie comandos para tarefas que você executa repetidamente.

Crie-os em ```.claude/commands/``` para comandos compartilhados da equipe ou em ```~/.claude/commands/``` para comandos pessoais.

Por exemplo, um comando ```/review``` que executa a lista de verificação de revisão de código da sua equipe. 

### Diferença para Skills

Toda skill já vira slash command automaticamente — o campo `name` do frontmatter define o `/slash-command`. Ou seja, ao escrever uma skill você ganha **ambos os modos de invocação no mesmo arquivo**: pode chamar manualmente com `/` quando quiser controle explícito, e o Claude também pode acionar sozinho quando o contexto pedir.

Se quiser desligar um dos modos na Skill, podemos fazer o seguinte:

```yaml
# Só manual (Claude nunca invoca sozinho — útil pra ações destrutivas tipo deploy)
disable-model-invocation: true

# Só automático (não aparece como slash command — útil pra "knowledge skills" 
user-invocable: false
```

## Super Powers

Superpowers é uma estrutura de skills abrangente que ensina a Claude metodologias estruturadas de desenvolvimento de software. Ela fornece habilidades ​​para desenvolvimento orientado a testes (TDD), debugging sistemático, brainstorming, desenvolvimento orientado a subagentes com revisão de código integrada e a capacidade de criar novas habilidades.

Para instalar podemos rodar o seguinte comando ou adicionar via a interface do Claude desktop, procurando o plugin do Superpowers

```claude plugin install superpowers@claude-plugins-official```

[Anthropic | SuperPowers](https://claude.com/plugins/superpowers)

### Brainstorm 

Essa é uma skill que eu uso muito para realizar brainstorm sobre ideias ou funcionalidades em um projeto.


## Plan Mode x Direct Mode

A execução direta é para tarefas com escopo claro e limitado.

Por exemplo: "Corrija este bug. Adicione esta validação. Renomeie esta variável em toda a base de código.""

> O Claude Code pode lidar com isso imediatamente, sem a necessidade de planejamento.

O modo de planejamento é para tudo que for maior. 

Exemplo: "Refatorar um módulo. Adicionar um novo recurso que afeta vários arquivos. Migrar de um padrão para outro. Reestruturar seu conjunto de testes.""

No modo de planejamento, o Claude Code primeiro cria um plano. Ele descreve quais arquivos serão afetados, quais alterações serão feitas e em que ordem. Você revisa o plano antes de escrever qualquer código.

O uso do modo plan pode aumentar signitificamente a efetividade e qualidade do resultado do Claude Code em tarefas complexas pois você pode revisar o plano antes dele executar, sugerir mudanças e corrigir mal entendidos.

Você pode iniciar um plano chamando ```/plan```

## Path-Specific Rules

São regras que podemos criar para serem seguidas somente em arquivos ou pastas específicas. 

Então nós criamos essers arquivos de regras em ```.claude/rules/``` com formato YAML que especifique padrões glob. 

Por exemplo:

```
---
paths:
  - "src/app/api/**/*.ts"
  - "app/api/**/*.ts"
---

# Regras para API Routes (Next.js App Router)

Aplicar em todas as route handlers (`route.ts`).

## Validação
- TODA entrada (body, query, params) deve ser validada com Zod antes de qualquer lógica
- Defina o schema no topo do arquivo, exporte como `<Action>Schema` para reuso em testes
- Use `schema.safeParse()` em vez de `parse()` — nunca lance ZodError pro cliente

## Autenticação
- Toda rota protegida começa com `const supabase = createServerClient(...)` e checa `getUser()`
- Se `user === null`, retorne 401 imediatamente, antes de qualquer query
- Nunca confie em headers customizados de auth — sempre valide via Supabase
```

## Effort

Effort é o controle explícito de **quanto o Claude vai "pensar" antes de responder** — quantos tokens ele aloca pra raciocínio interno (extended thinking) antes de produzir output. No Claude Code, você ajusta isso com `/effort low`, `/effort medium`, `/effort high` ou `/effort max`. 

É um setting persistente na sessão: define uma vez e vale até você trocar. Existe também o atalho via palavra-chave dentro do próprio prompt — basta escrever "ultrathink" em qualquer lugar da mensagem e o Claude eleva o thinking budget pra aquele turno específico, sem precisar mudar o setting global. 

>Quando você dispara ultrathink, o Claude ganha cerca de 32 mil tokens pra raciocinar sobre o problema antes de responder.

- A regra prática é casar nível de effort com complexidade da tarefa: edits triviais e tasks repetitivas vão bem em `low`
> Subir o effort não melhora respostas pra problemas simples — só deixa mais lento e gasta mais token à toa.

- debugs, decisões de arquitetura e refactors grandes pedem `high` ou `max`. 

Use `/effort` quando você sabe de antemão que vai entrar num bloco de tarefas com mesmo perfil (ex: "vou passar a próxima hora resolvendo um bug chato no factoring" → `/effort max`). Use o gatilho "ultrathink" inline quando estiver no fluxo e quiser intensificar o raciocínio só naquela mensagem específica, sem mexer na config da sessão.

## Rodando multiplos agentes

Podemos rodar múltiplos agente em paralelo de 2 formas:
- worktress
- subagentes

### Worktrees

Quando falamos de worktrees, estamos falando de **paralelismo no nível de filesystem**. Você cria diretórios separados (cada um com sua própria working directory git) e roda uma sessão de Claude Code em cada um, em terminais diferentes. Cada agente tem seu próprio "espaço físico" para mexer em arquivos sem ver o que o outro está fazendo.

Worktrees fazem sentido quando você tem **features independentes que vão virar commits/PRs separados**: uma feature de factoring rodando em paralelo com um fix de webhook do PayPal, ou um hotfix em main enquanto um refactor longo segue em outra branch. O resultado de cada worktree é mergeado individualmente, no seu próprio tempo.

Dentro do conceito de worktrees, ainda existem 2 abordagens possíveis:
- Em um único workspace
- Workspace separados

### Single Workspace

Nesse cenário os agentes estarão trabalhando em cima do mesmo workspace de arquivos, mexendo na mesma pasta ao mesmo tempo. 

Caso as funcionalidades que estão sendo implementadas em paralelo afetem o mesmo local, talvez seja legal evitar single workspace.

Mas no geral esse formato economiza tempo de setup, espaço em disco e mantém um único `node_modules`/`.next`/build cache. Você abre dois ou mais terminais apontando pra mesma pasta e roda `claude` em cada um.

#### Quando funciona bem
- Tarefas em **arquivos diferentes** (um agente em `app/api/influencers/`, outro em `components/dashboard/`)
- Um agente codando feature, outro escrevendo testes ou docs
- Um agente em código, outro fazendo análise/leitura sem escrever

#### Quando dá ruim
Os agentes não sabem o que o outro está fazendo. Se dois mexem no mesmo arquivo:
- Sobrescrevem edições um do outro
- Um deles lê o arquivo num estado intermediário do outro e propõe mudança em cima de algo que já mudou
- Você acaba com merges manuais frustrantes

> Se ambos os agentes trabalham no mesmo arquivo, o caos toma conta. Eles sobrescrevem as edições um do outro e manipulam o contexto um do outro.

A regra é: **single workspace só pra tarefas garantidamente disjuntas em nível de arquivo**.

---

### Multiple Workspace

Cada agente roda em um diretório próprio, isolado. A forma idiomática de fazer isso no Claude Code é com **git worktrees** — não precisa duplicar o repo, o git mantém múltiplas working directories apontando pro mesmo histórico.

Cada pasta é independente: arquivos próprios, índice próprio, branch próprio. Compartilham apenas o `.git` (histórico, objetos, config).

#### Setup manual (funciona em qualquer versão)

```bash
# A partir do repo principal:
git worktree add ../pora-feat-factoring -b feat/factoring
git worktree add ../pora-fix-paypal -b fix/paypal-webhook

# Em terminais separados:
cd ../pora-feat-factoring && claude
cd ../pora-fix-paypal && claude

# Quando terminar:
git worktree remove ../pora-feat-factoring
```

#### Setup nativo (Claude Code v2.1.50+)

A partir da v2.1.50, dá pra usar a flag `--worktree` direto:

```bash
claude --worktree feat-factoring
# Cria automaticamente .claude/worktrees/feat-factoring/
# E inicia sessão isolada com branch worktree-feat-factoring
```

Cleanup também é automático — ao sair da sessão, o Claude pergunta se quer manter ou remover o worktree.

> Adicione `.claude/worktrees/` ao seu `.gitignore`.

#### Quando vale a pena

- Features que **mexem nos mesmos arquivos** (refactor de schema Prisma vs nova feature que usa o mesmo schema)
- Trabalhos longos rodando em background enquanto você foca em outra coisa
- A/B parallel: rodar **N agentes resolvendo o mesmo problema** com prompts iguais e escolher a melhor implementação no final
- Hotfix em main enquanto feature longa segue rodando em outra branch

#### Trade-offs

- **Setup tem custo**: arquivos não-versionados (`.env.local`, uploads locais) precisam ser copiados pra cada worktree
- **Dependências**: cada worktree pode precisar do próprio `npm install` se você usa workspace-local node_modules
- **Banco**: se os agentes mexem em schema/migrations, considere bancos isolados por worktree (Supabase branch ou Postgres local com nome diferente)
- **Custo de tokens**: N agentes rodando = N vezes o consumo. Não saia ligando 5 sessões só porque pode.
- **Custo cognitivo**: revisar 3 PRs paralelos exige mais foco que acompanhar um agente. Trabalhar em paralelo só ajuda se você consegue revisar em paralelo.

#### TLRD:

Single workspace é como dois colegas codando na mesma máquina por terminal compartilhado — só funciona se vocês combinaram quem mexe em quê. Multiple workspaces (worktrees) é dar uma máquina pra cada um, com checkout do mesmo repo. Mais resiliente, mais flexível, mas exige merge no final.

A regra prática: **comece com single workspace, migre pra worktrees no momento que a fricção de "agentes pisando no pé um do outro" custar mais que o setup de worktree**. Pra features genuinamente independentes do Pora (ex: módulo de factoring vs sequência Brevo do TheVibeCRM), worktrees são o caminho desde o início.

### Subagentes

Subagentes são paralelismo de **contexto**, não de filesystem. Uma sessão principal do Claude delega trabalho pra agentes filhos via Task tool. Cada subagent tem janela de contexto própria, executa sua parte, e retorna um **resumo curto** pro pai. Tudo acontece no mesmo workspace, no mesmo terminal — você não fica fazendo juggling entre janelas.

A diferença prática vs worktrees: subagents **convergem**. O pai consolida os outputs dos filhos numa única síntese ou decisão. Worktrees **divergem**: cada um vira um PR separado.

#### Como usar

Existem dois modos de invocação: automática (você descreve a tarefa e o Claude principal decide criar subagentes) e explícita (você define agentes especializados com configuração própria, e o Claude usa eles quando o trigger bate).

#### Modo 1: Invocação automática (Task tool)

A forma mais simples. Você fala em linguagem natural pro agente principal, ele decide se vale paralelizar via Task tool. Não precisa configurar nada.

> "Pesquise em paralelo como Brevo, Resend e Postmark lidam com webhooks de bounce. Compare e me diga qual encaixa melhor nesse projeto."

O Claude principal vai:

1. Spawnar 3 subagents (um por provider)
2. Cada um faz a pesquisa isoladamente
3. Pai recebe os 3 resumos
4. Pai consolida e responde

#### Modo 2: Subagentes customizados (recomendado pro fluxo recorrente)

Aqui você define agentes especializados como arquivos markdown que ficam disponíveis pra qualquer sessão. É o que vale a pena investir tempo, porque vira ferramenta reutilizável.
Onde ficam:

```
.claude/agents/          # agentes do projeto (compartilhados via git)
~/.claude/agents/        # agentes pessoais (todos os projetos)
````

Precedência: projeto sobrescreve global se nome bater.

Estrutura de um agente:

```
---
name: code-reviewer
description: Revisa código recém-escrito procurando bugs, problemas de segurança e violações de convenção. Use proativamente após mudanças significativas.
tools: Read, Grep, Glob, Bash
model: sonnet
---

Você é um revisor de código sênior com foco em qualidade, segurança e manutenibilidade.

Quando invocado:
1. Rode `git diff` para ver mudanças recentes
2. Foque apenas nos arquivos modificados
3. Comece a revisão imediatamente

Checklist:
- Código legível e bem nomeado
- Sem código duplicado
- Tratamento de erros adequado
- Sem secrets ou credenciais expostas
- Validação de input em routes/actions
- Testes cobrem o caminho feliz e 2-3 cenários de erro

Output em três blocos:
- **Críticas (devem corrigir)**: bugs, vulnerabilidades, violações sérias
- **Sugestões (deveriam considerar)**: melhorias de design, performance
- **Nits (opcional)**: estilo, naming, micro-otimizações

Seja direto. Não elogie sem motivo. Aponte problemas com exemplo de código pra correção.
```

#### Como criar (3 caminhos)

1. Caminho 1 — comando interativo:
```/agents```
Abre menu pra criar, editar e listar agentes. Recomendado pra começar — o próprio Claude pode te ajudar a escrever a config.

2. Caminho 2 — arquivo manual:
```bash
bashmkdir -p .claude/agents
# cria .claude/agents/code-reviewer.md com o conteúdo acima
```

3. Caminho 3 — pedir pro Claude gerar:
```bash
"Crie um subagente em .claude/agents/ chamado supabase-migration-reviewer 
que valida migrations seguindo as regras em .claude/rules/supabase-migrations.md"
```

### Worktrees vs Subagentes — qual usar?

A pergunta certa não é qual é melhor, é **o que você está paralelizando**:

| Problema | Solução |
|---|---|
| "Preciso explorar/ler muita coisa sem entupir meu contexto" | Subagentes |
| "Preciso editar arquivos sem que outro agente sobrescreva" | Worktrees |
| "Quero N implementações diferentes pra escolher uma" | Worktrees |
| "Quero N pesquisas/análises pra consolidar em uma resposta" | Subagentes |
| "Tarefas independentes que viram PRs separados" | Worktrees |
| "Uma tarefa grande dividida em partes que se juntam no final" | Subagentes |


## Conductor

Podemos trabalhar com múltiplos agentes no Claude de forma mais fácil usando o Conductor. 

> O Conductor funciona como uma plataforma que permite criar e gerenciar agentes paralelos, como Claude Code e Codex, em espaços de trabalho isolados, facilitando a revisão e a fusão de alterações no código.

[Conductor | Parallel Agents](https://www.conductor.build/docs/core/parallel-agents)

# SEMANA FREE de CLAUDE CODE

 https://claude.ai/referral/SPpI0l418g

# Referências

[Anthropic | Claude Code Best Practices](https://code.claude.com/docs/en/best-practices)

[Twitter Article | How to Become a Claude Code Power User](https://x.com/eng_khairallah1/article/2047245243503050997)

[Anthropic | Orquestração de múltiplos agentes](https://code.claude.com/docs/en/agent-teams)

[Anthropic | Criando de sub agentes](https://code.claude.com/docs/en/sub-agents)