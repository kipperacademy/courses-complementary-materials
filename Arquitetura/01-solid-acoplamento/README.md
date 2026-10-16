# Aula 1 — SOLID e acoplamento

A aula apresenta os cinco princípios SOLID e usa matrícula, pagamento e notificação para mostrar como uma dependência concreta pode bloquear o acesso. A parte prática destaca acoplamento, troca de implementações por contrato e o tratamento da falha de notificação. Coesão não é explicada na gravação e por isso não faz parte deste material.

## Conceitos apresentados

### Acoplamento

Quando a matrícula conhece e instancia diretamente o e-mail, uma falha de envio interrompe a liberação do acesso. Uma dependência por contrato permite trocar o notificador sem reescrever a regra de matrícula.

~~~mermaid
flowchart LR
  Pedido --> MatriculaAcoplada
  MatriculaAcoplada --> Email
  Email -->|indisponível: exceção| AcessoBloqueado
  Matriculas --> ContratoNotificador
  Email2[Email] -. implementa .-> ContratoNotificador
  WhatsApp -. implementa .-> ContratoNotificador
~~~

### S — Responsabilidade única

Uma classe deve ter uma responsabilidade e uma razão principal para mudar. A aula contrasta uma classe de controle acadêmico que mistura matrícula, pagamento, aluno e e-mail com responsabilidades separadas.

~~~mermaid
flowchart LR
  ControleAcademico --> Matricula
  ControleAcademico --> Pagamento
  ControleAcademico --> Aluno
  ControleAcademico --> Notificacao
~~~

### O — Aberto/fechado

O comportamento pode ser estendido por uma nova implementação sem alterar a regra estável de matrícula.

~~~mermaid
flowchart LR
  Matriculas --> ContratoNotificador
  ContratoNotificador -. implementa .-> Email
  ContratoNotificador -. implementa .-> WhatsApp
~~~

### L — Substituição de Liskov

Uma implementação substituta deve preservar o contrato que a matrícula espera, sem quebrar o fluxo.

~~~mermaid
flowchart LR
  Matriculas --> Notificador[Contrato: enviar aluna]
  Email -. substitui .-> Notificador
  WhatsApp -. substitui .-> Notificador
~~~

### I — Segregação de interfaces

Cada cliente deve depender das operações de que precisa. A aula separa a consulta do status de pagamento da ação de notificar.

~~~mermaid
flowchart LR
  Matriculas --> ConsultaPagamento[consultar status]
  Matriculas --> Notificador[enviar aviso]
~~~

### D — Inversão de dependência

A regra de matrícula depende de contratos; as integrações concretas implementam esses contratos e são passadas de fora.

~~~mermaid
flowchart LR
  PagamentoLocal -. implementa .-> ConsultaPagamento
  Email -. implementa .-> Notificador
  WhatsApp -. implementa .-> Notificador
  Matriculas --> ConsultaPagamento
  Matriculas --> Notificador
  Main[Main: monta e injeta] --> Matriculas
~~~

## Código de exemplo

Os arquivos Java em `pratica/` demonstram acoplamento e desacoplamento no caso de matrícula e notificações.

Para compilar e executar uma demonstração a partir da pasta `Arquitetura/`:

~~~bash
javac -d /tmp/aula1 01-solid-acoplamento/pratica/*.java
java -cp /tmp/aula1 DemoAcoplamento
~~~
