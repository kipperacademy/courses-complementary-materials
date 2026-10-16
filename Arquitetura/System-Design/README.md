# 🔍 O que é System Design?

**System Design** é a definição da **arquitetura, componentes e interfaces** de um sistema com o objetivo de cumprir especificações técnicas e de negócio.

Um bom System Design garante que o sistema:
- Lida com **grandes volumes de dados**
- **Escala** de acordo com a demanda
- **Se mantém estável** em diversas condições

> 💡 **Quando você precisa pensar nisso?** System Design se torna essencial quando há escala ou complexidade técnica envolvida — seja em volume de dados, número de usuários simultâneos ou necessidade de alta disponibilidade. Para um Micro-SaaS simples ou um CRUD básico com baixa demanda, você não precisa se preocupar tanto com isso.

![oque-e.png](oque-e.png)


---

## 🏛️ Os Fundamentos

| Conceito | O que resolve |
|---|---|
| Escalabilidade | Crescimento da carga |
| Disponibilidade | Tempo de serviço ativo |
| Consistência | Dados íntegros para todos |
| Teorema CAP | Trade-off entre os três acima |
| Load Balancing | Distribuição de tráfego |
| Cache | Leituras rápidas |
| CDNs | Entrega de conteúdo com baixa latência |
| Replicação | Cópias dos dados para segurança |
| Failover | Recuperação automática de falhas |
| Pooling / WebSockets | Conexões eficientes e em tempo real |
| Banco de Dados | Schema eficiente + escolha certa |

![conceitos.png](conceitos.png)


---

## 📐 Os 4 Conceitos Essenciais

### 1. Escalabilidade *(Scalability)*
Capacidade do sistema de lidar com o aumento de carga adicionando recursos.

| Tipo | Como funciona | Limitação |
|---|---|---|
| **Vertical** | Mais CPU/RAM na mesma máquina | Tem limite físico e de custo |
| **Horizontal** | Adicionar mais máquinas | Crescimento teoricamente infinito ✅ |

> 🎯 **Padrão ouro:** escalabilidade horizontal é o modelo preferido em sistemas modernos.

---

### 2. Disponibilidade *(Availability)*
Tempo em que o sistema permanece funcional e acessível. Medida pelos famosos **"9s" de uptime**.

| SLA | Uptime | Downtime por ano |
|---|---|---|
| 99% | Dois 9s | ~3,6 dias |
| 99.9% | Três 9s | ~8,7 horas |
| 99.99% | Quatro 9s | ~52 minutos |
| 99.999% | Cinco 9s | ~5 minutos |

> 💡 Alta disponibilidade significa que, mesmo que um servidor falhe, o usuário **não percebe interrupção**.

---

### 3. Consistência *(Consistency)*
Garante que **todos os usuários vejam os mesmos dados ao mesmo tempo**, independentemente do servidor que acessem.

**Exemplo:** Se você atualiza seu perfil, a consistência garante que qualquer pessoa que acessar seu perfil verá a informação nova imediatamente.

---

### 4. Teorema CAP
Em um sistema distribuído, é **impossível garantir** os três ao mesmo tempo:

```
C — Consistency     (Consistência)
A — Availability    (Disponibilidade)
P — Partition Tolerance (Tolerância a falhas de rede)
```

Como falhas de rede (Partição) são **inevitáveis**, você sempre terá que escolher entre:

| Prioridade | Tipo | Exemplo |
|---|---|---|
| **CP** | Consistência + Partição | Sistema bancário (PIX — o saldo tem que estar certo) |
| **AP** | Disponibilidade + Partição | Rede social (curtidas no Instagram podem atrasar) |

> ✍️ **Dica de prova/entrevista:** Sempre justifique sua escolha com base no domínio do sistema.

---

## ⚖️ Trade-offs

Ao desenhar um sistema, você precisará tomar **decisões entre diferentes abordagens**. Isso se chama **trade-off**.

**Como analisar um trade-off:**
1. Liste as opções disponíveis
2. Avalie os prós e contras de cada uma
3. Decida com base nos **requisitos mais importantes** do sistema

**Exemplo prático — Adicionar Redis como cache:**

| Fator | Com Cache (Redis) | Sem Cache |
|---|---|---|
| Latência de leitura | ~60ms ✅ | ~100ms |
| Custo de infra | Maior ❌ | Menor |
| Complexidade | Maior ❌ | Menor |

> Se o budget for apertado ou a otimização de 40ms não for crítica para o negócio, talvez não valha a pena adicionar. **Tudo é uma balança.**

![tradeoffs.png](tradeoffs.png)

---

## 📋 Requisitos Funcionais vs. Não Funcionais

Antes de desenhar qualquer diagrama ou escolher um banco de dados, separe:

### Requisitos Funcionais — *"O Quê"*
São as **funcionalidades diretas**, o comportamento do sistema diante de ações do usuário. Basicamente as **regras de negócio**.

**Exemplos:**
- O usuário deve conseguir realizar login via OAuth
- O sistema deve enviar e-mail de confirmação após a compra
- O criador deve conseguir visualizar o dashboard de ganhos

### Requisitos Não Funcionais — *"Como"*
Definem os **critérios de operação** — performance, segurança, confiabilidade. É aqui que o System Design brilha.

**Exemplos:**
- **Escalabilidade:** suportar 100 mil usuários simultâneos
- **Latência:** busca de produtos em menos de 200ms
- **Disponibilidade:** 99.9% de uptime (SLA)
- **Segurança:** dados sensíveis criptografados em repouso

---

## 🛠️ Estudo de Caso — *NotiFly*: Sistema de Alertas em Tempo Real

> **Problema:** Criar um sistema que avisa seguidores toda vez que um criador posta um vídeo novo ou abre uma live.

### Requisitos Funcionais
- Envio de notificações via **Push, E-mail e SMS**
- Seguidor pode configurar **preferências de alerta**
- Usuário acessa **histórico de notificações** no app

### Requisitos Não Funcionais
- **Escalabilidade:** picos repentinos (1 milhão de notificações de uma vez)
- **Baixa latência:** notificação em menos de 5 segundos
- **Confiabilidade:** nenhuma notificação pode ser perdida
- **Disponibilidade:** serviço de envio não pode cair mesmo que o banco de histórico esteja instável

[]()

---

### 🚩 O Desafio: *The Fan-out Problem*

Um único evento (1 vídeo publicado) se transforma em 500.000 ações de envio simultâneas. Tentar processar isso em um único loop vai causar **timeout** ou **estourar a memória do servidor**.

---

### 🏗️ A Solução: Arquitetura em Camadas

```
[YouTube WebSub]
      │
      ▼
┌─────────────────────────┐
│  1. Webhook Handler     │  ← Responde 200 OK em < 100ms
│     (Ingestão)          │
└────────────┬────────────┘
             │ coloca mensagem na fila
             ▼
┌─────────────────────────┐
│  2. Fan-out Service     │  ← Busca seguidores do canal
│     (Descoberta)        │    e divide em chunks de 500
└────────────┬────────────┘
             │ chunks para a fila
             ▼
┌─────────────────────────┐
│  3. Message Queue       │  ← RabbitMQ / Kafka / SQS
│     (Mensageria)        │    Garante que nada se perde
└────────────┬────────────┘
             │ distribui para workers
             ▼
┌─────────────────────────┐
│  4. Notification Workers│  ← Processamento paralelo
│     (Execução)          │    Checa Redis → chama API
└─────────────────────────┘
             │ falha?
             ▼
        [Dead Letter Queue]  ← Tenta novamente depois
```

### Resumo de Componentes

| Camada | Tecnologia | Responsabilidade |
|---|---|---|
| Ingestão | API REST | Receber webhook e enfileirar |
| Descoberta | DynamoDB / MongoDB | Buscar seguidores por canal |
| Mensageria | Kafka / SQS / RabbitMQ | Garantir entrega confiável |
| Cache | Redis | Preferências do usuário em memória |
| Execução | Workers paralelos | Enviar via Firebase / SendGrid |
| Resiliência | Dead Letter Queue (DLQ) | Reprocessar falhas sem perder dados |

---

## 🏦 Caso Real AWS — *Temenos: Banking Software at Scale*

> 🎥 **Vídeo de referência:** [Temenos: Building Serverless Banking Software at Scale](https://www.youtube.com/watch?v=mtZvA7ARepM) — Amazon Web Services

A **Temenos** é uma das maiores fornecedoras de software bancário do mundo. Este caso mostra como eles aplicaram os fundamentos de System Design na prática, usando AWS para construir o sistema **T24 Transact** — um core banking de alto desempenho e altíssima disponibilidade.

---

### 🏗️ A Arquitetura

O sistema foi construído com serviços **gerenciados e serverless** da AWS, o que elimina grande parte da necessidade de gerenciar servidores manualmente.

```
         [Cliente / Aplicação Bancária]
                      │
                      ▼
            ┌─────────────────┐
            │  API Gateway    │  ← Ponto único de entrada
            └────────┬────────┘
                     │
          ┌──────────┴──────────┐
          ▼                     ▼
   ┌─────────────┐      ┌──────────────┐
   │  AWS Lambda │      │ AWS Fargate  │  ← Containers sem servidor
   │  (Funções)  │      │ (Workloads)  │
   └──────┬──────┘      └──────┬───────┘
          │                    │
          ▼                    ▼
   ┌──────────────────────────────────┐
   │         ELB (Load Balancer)      │
   └──────────────────────────────────┘
          │                    │
          ▼                    ▼
   ┌────────────┐       ┌────────────┐
   │  DynamoDB  │       │    RDS     │  ← Dois bancos para fins distintos
   └────────────┘       └────────────┘
          │
          ▼
   ┌────────────┐
   │  Kinesis   │  ← Streaming de eventos em tempo real
   └────────────┘
```

---

### 🗄️ Estratégia de Banco de Dados — O Trade-off na Prática

Aqui está um dos trade-offs mais interessantes do caso: **eles usam dois bancos de dados com propósitos diferentes**.

| Banco | Tipo | Para que serve |
|---|---|---|
| **DynamoDB** | NoSQL (chave-valor) | Transações de alta velocidade, acesso por chave, escala automática |
| **RDS** | SQL Relacional | Relatórios, consultas complexas, dados relacionais |

> 💡 **Por que dois bancos?** DynamoDB é otimizado para leituras e escritas ultra-rápidas por chave (perfeito para operações bancárias em tempo real). Já RDS suporta queries SQL complexas, necessárias para relatórios regulatórios e auditoria — um requisito crítico no setor financeiro. Cada um foi escolhido para o que faz melhor.

---

### 📡 Kinesis — Processamento de Eventos em Tempo Real

O **Amazon Kinesis** é o serviço de streaming de dados da AWS, similar ao Kafka. No sistema Temenos ele é usado para:

- Capturar **eventos de transações** em tempo real
- Alimentar pipelines de **auditoria e compliance**
- Permitir processamento assíncrono sem bloquear a transação principal

> 🔗 Isso conecta diretamente com o conceito de **mensageria e desacoplamento** que vimos no caso NotiFly — mesma ideia, contexto financeiro real.

---

### 🚀 Escalabilidade Serverless na Prática

O grande diferencial do design da Temenos é usar **Lambda + Fargate** para garantir **escala automática e elástica**:

| Componente | Comportamento |
|---|---|
| **AWS Lambda** | Executa funções sob demanda — escala de 0 a milhares de execuções paralelas automaticamente |
| **AWS Fargate** | Containers que sobem e descem conforme a demanda — sem gerenciar servidores |
| **API Gateway** | Absorve picos de tráfego e roteia para os serviços certos |
| **ELB** | Distribui carga entre as instâncias ativas |

> ✅ **Resultado:** A Temenos conseguiu um sistema **altamente elástico** — paga apenas pelo que usa — com **baixo custo de manutenção** e sem equipe dedicada a gerenciar servidores.

---

### 🔁 Conectando com o Teorema CAP

O sistema bancário da Temenos é um exemplo clássico de escolha **CP (Consistency + Partition Tolerance)**:

- Uma transação financeira **nunca pode mostrar dados inconsistentes** — o saldo tem que estar correto
- O sistema prefere **rejeitar uma operação** a processar com dados potencialmente inconsistentes
- DynamoDB, quando configurado com leituras fortemente consistentes, garante isso

---

### 💬 O que aprender com este caso

- Serviços **gerenciados e serverless** reduzem drasticamente a complexidade operacional
- A escolha do banco de dados deve ser guiada pelo **padrão de acesso**, não pela familiaridade da equipe
- **Streaming de eventos** (Kinesis/Kafka) é fundamental em sistemas financeiros para auditoria e rastreabilidade
- O domínio do negócio (banco) ditou a escolha **CP** no teorema CAP — sempre deixe os requisitos guiarem a arquitetura

---

## 🔗 Referências para Aprofundar

- [▶️ Temenos: Building Serverless Banking at Scale (AWS)](https://www.youtube.com/watch?v=mtZvA7ARepM)
- [Exemplos de System Design no GitHub](https://github.com/spatnaik77/system-design-examples?tab=readme-ov-file#google-drive)
- [10 lições da arquitetura da Netflix](https://medium.com/javarevisited/10-system-design-lessons-from-netflixs-architecture-19ece2191a03)
- [Arquitetura de baixo custo na AWS](https://aws.amazon.com/pt/blogs/architecture/architecting-a-low-cost-web-content-publishing-system/)
