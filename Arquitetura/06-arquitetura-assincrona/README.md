# Aula 6 — Arquitetura assíncrona

A aula explica comunicação entre aplicações distribuídas: aceitar um trabalho e concluí-lo depois, encaminhar mensagens por filas ou tópicos, confirmar recebimento e tratar reentregas. As demos Java são simulações em memória, não um broker de produção.

## Conceitos apresentados

### Trabalho assíncrono: 202, consulta e webhook

HTTP 202 significa que a tarefa foi aceita; o resultado ainda está pendente. A aplicação pode consultar depois por polling ou receber uma atualização por webhook.

~~~mermaid
sequenceDiagram
  participant Loja
  participant Estoque
  Loja->>Estoque: POST reserva
  Estoque-->>Loja: 202 + id da tarefa
  Loja->>Estoque: GET periódico (polling)
  Estoque-->>Loja: resultado final
  Estoque-->>Loja: ou POST webhook ao concluir
~~~

### Fila, produtor e consumidores

O produtor publica tarefas em uma fila mantida por um broker. Consumidores retiram e processam as mensagens; a fila preserva a ordem de entrada descrita na aula.

~~~mermaid
flowchart LR
  Produtor --> Broker[Broker: fila FIFO]
  Broker --> ConsumidorA[Consumidor A]
  Broker --> ConsumidorB[Consumidor B]
~~~

### Consumidores concorrentes e reentrega

Vários workers podem dividir mensagens. Se um worker falhar durante o processamento, o broker pode reentregar a mensagem.

~~~mermaid
flowchart LR
  Fila -->|próxima mensagem| Worker1
  Fila -->|próxima mensagem| Worker2
  Worker1 -->|falha antes da confirmação| Reentrega[Mensagem volta à fila]
  Reentrega --> Worker2
~~~

### Tópico e pub/sub

Um tópico publica um evento para cada serviço inscrito; por exemplo, pedido feito pode interessar a notificações, notas e entregas.

~~~mermaid
flowchart LR
  Pedido[Evento: pedido feito] --> Topico[Tópico]
  Topico --> Email[Inscrito: email/WhatsApp]
  Topico --> Nota[Inscrito: nota fiscal]
  Topico --> Entrega[Inscrito: entrega]
~~~

### ACK e processamento

ACK confirma que o broker recebeu a mensagem. Isso, sozinho, não prova que todos os serviços terminaram o trabalho.

~~~mermaid
sequenceDiagram
  participant Produtor
  participant Broker
  participant Consumidor
  Produtor->>Broker: publicar evento
  Broker-->>Produtor: ACK: mensagem recebida
  Broker->>Consumidor: entregar evento
  Note over Consumidor: processamento é uma etapa separada
~~~

### Retry e idempotência

Uma resposta perdida pode levar a um retry e a uma entrega duplicada. O consumidor usa o mesmo ID do evento para evitar repetir o efeito.

~~~mermaid
sequenceDiagram
  participant Broker
  participant Consumidor
  Broker->>Consumidor: evento E42
  Consumidor->>Consumidor: registra E42 e aplica efeito
  Note over Broker,Consumidor: resposta perdida; broker reenvia E42
  Broker->>Consumidor: retry E42
  Consumidor->>Consumidor: encontra E42; não repete efeito
~~~

### DLQ

Depois de novas tentativas sem sucesso, uma mensagem pode ir para uma dead-letter queue para análise e reprocessamento.

~~~mermaid
flowchart LR
  Fila --> Tentativa[Processar]
  Tentativa -->|sucesso| Fim[Concluída]
  Tentativa -->|falhas após retries| DLQ[Dead-letter queue]
~~~

### ID comum entre serviços

Um identificador compartilhado ajuda a relacionar pedido, reserva e entrega quando cada serviço mantém seus próprios dados.

~~~mermaid
flowchart LR
  Pedido[Pedido E42] --> Reserva[Reserva E42]
  Reserva --> Entrega[Entrega E42]
  Pedido --> Banco1[(Banco da loja)]
  Reserva --> Banco2[(Banco do estoque)]
  Entrega --> Banco3[(Banco da entrega)]
~~~

## Código de exemplo

Os arquivos Java em `pratica/` demonstram reentrega de mensagens e o efeito da idempotência no consumidor.

Para compilar e executar uma demonstração a partir da pasta `Arquitetura/`:

~~~bash
javac -d /tmp/aula6 06-arquitetura-assincrona/pratica/*.java
java -cp /tmp/aula6 DemoEntregaDuplicada
~~~
