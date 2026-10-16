# Claude Code na VPS

## Por que usar o Claude Code em uma VPS?

Rodar o Claude Code no seu computador local tem limitações: quando você fecha o terminal, tudo para. Na VPS, o Claude fica rodando 24/7, sem depender da sua máquina.

Casos de uso práticos:
- Agentes que monitoram repositórios, issues ou deploys de forma contínua
- Tarefas agendadas que executam de madrugada ou em horários específicos
- Automações longas sem travar seu computador pessoal
- Pipelines que precisam estar sempre disponíveis (CI, scraping, notificações)

---

## Como instalar

### 1. Criar a VPS com o template

A Hostinger tem um template pronto com tudo configurado para o Claude Code.

1. Crie uma VPS nova e selecione o template **Claude Code**
2. O template já instala Node.js, Claude Code CLI e dependências necessárias

### 2. Configurar o acesso SSH

Após criar a VPS, você precisa configurar sua chave SSH para conectar sem senha:

1. Cria a chave SSH
```bash
# Gerar uma chave SSH (se ainda não tiver)
ssh-keygen -t ed25519 -C "seu@email.com"
```

2. Adicione a chave pública SSH no painel da Hostinger ao criar a VPS.

### 3. Conectar no terminal

```bash
ssh root@IP_DA_SUA_VPS
```

Substitua `IP_DA_SUA_VPS` pelo IP que aparece no painel da Hostinger.

### 4. Fazer login no Claude

Dentro da VPS, autentique sua conta Anthropic:

```bash
claude login
```

Siga o link que aparecer no terminal para autorizar o acesso. Depois disso, o Claude está pronto para usar.

---

> ✨ Desconto na VPS da Hostinger para rodar o Claude ✨
https://hostinger.com/kipperclaude

---

# `/loop` vs `/schedule` — qual usar?

### `/loop` — repetição contínua na sessão atual

O `/loop` roda um comando repetidamente **enquanto a sessão está aberta**. Você define o intervalo e ele fica executando.

```
/loop 5m /algum-comando
```

**Quando usar na VPS:**
- Monitorar o status de um deploy em andamento
- Checar logs de uma aplicação a cada X minutos
- Polling de uma API externa enquanto aguarda um resultado
- Tarefas que precisam rodar por algumas horas, mas não para sempre

> O `/loop` para quando você fecha o terminal ou encerra a sessão. A VPS é útil aqui porque mantém a sessão viva com `tmux` ou `screen`.

---

### `/schedule` — agente remoto na infraestrutura da Anthropic

O `/schedule` cria uma **rotina agendada que roda na nuvem da Anthropic** (não na sua VPS). Cada execução spawna um agente isolado com seu próprio checkout do repositório Git, ferramentas e conexões MCP.

```
/schedule
```

Importante: o agente remoto **não tem acesso à sua máquina local nem à sua VPS**. Ele só consegue acessar o que está no repositório Git e nos serviços conectados via MCP (Slack, Notion, Linear, etc.).

**Quando usar:**
- Gerar relatório toda segunda-feira de manhã
- Verificar PRs pendentes no GitHub todo dia às 9h
- Enviar resumo de erros de produção toda noite via Slack
- Qualquer automação recorrente que precise rodar sem você estar presente

**Por que usar na VPS então?** Você não usa o `/schedule` *na* VPS — ele roda na Anthropic. A VPS serve para o `/loop` e para sessões longas com `tmux`.

> O `/schedule` persiste entre sessões e independe de qualquer máquina sua — é gerenciado em https://claude.ai/code/routines

---

## Resumo rápido

| | `/loop` | `/schedule` |
|---|---|---|
| Onde roda? | Na VPS (sessão aberta) | Na nuvem da Anthropic |
| Roda sem você estar conectado? | Não (precisa de sessão ativa) | Sim |
| Persiste após fechar o terminal? | Não | Sim |
| Acessa arquivos locais da VPS? | Sim | Não (só Git + MCP) |
| Bom para... | Monitoramento pontual | Automações recorrentes |
| Configuração | Simples (intervalo + comando) | Cron schedule + repositório Git |

---

> ✨ Desconto na VPS da Hostinger para rodar o Claude ✨
https://hostinger.com/kipperclaude

---

## Remote Control — controlar o Claude na VPS de fora

O **remote control** permite acionar ou controlar uma sessão do Claude rodando na VPS sem precisar estar conectado no terminal. Útil para disparar tarefas pontualmente de qualquer lugar.

### Pré-requisito: manter a sessão viva com tmux

Antes de qualquer coisa, instale o `tmux` para que a sessão não morra quando você fechar o terminal:

```bash
apt install tmux

# Criar uma sessão nomeada
tmux new -s claude

# Dentro da sessão, inicie o Claude
claude

# Para sair sem matar a sessão: Ctrl+B, depois D
```

Para reconectar depois:
```bash
tmux attach -t claude
```

### Habilitar o remote control no Claude Code

O Claude Code tem suporte nativo a **remote control via SSH**. Com ele, você consegue enviar mensagens para uma sessão rodando na VPS direto do seu terminal local.

**Na VPS**, inicie o Claude em modo que aceita conexões remotas:

```bash
claude --remote-control
```

**No seu computador local**, conecte ao Claude da VPS:

```bash
claude --ssh root@IP_DA_SUA_VPS
```

Isso abre uma sessão local que está, na verdade, controlando o Claude rodando na VPS — com todos os arquivos e contexto dela.

### Para que serve na prática?

- Disparar uma tarefa no Claude da VPS sem precisar abrir SSH manualmente
- Usar a interface local (ou IDE) enquanto o processamento pesado acontece na VPS
- Integrar com webhooks: um endpoint recebe uma chamada e dispara um comando no Claude via SSH

---

> ✨ Aproveite nosso desconto na VPS da Hostinger para rodar o Claude ✨
https://hostinger.com/kipperclaude

---