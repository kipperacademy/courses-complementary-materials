# Aula 5 — Monólito versus microsserviços

A aula compara manter módulos em uma aplicação e separá-los em serviços. Os exemplos falados passam por uma escola, farmácia, e-commerce, busca de produtos da Amazon, perfil de usuário e cobrança. A gravação é conceitual; ela não apresenta uma demo de código.

## Conceitos apresentados

### Monólito e monólito modular

No monólito modular, os módulos permanecem separados dentro de uma aplicação. Eles compartilham o artefato de build e deploy e se comunicam localmente.

~~~mermaid
flowchart LR
  subgraph App[Uma aplicação]
    Matricula[Matrículas]
    Perfil[Perfil]
    Busca[Busca]
    Cobranca[Cobranças]
  end
  App --> Deploy[Um build e um deploy]
~~~

### Microsserviços

Serviços são processos e deploys separados e conversam por contratos de rede, como APIs HTTP.

~~~mermaid
flowchart LR
  Checkout[Checkout] -->|API HTTP| Cobranca[Cobrança]
  Busca[Busca] -->|API HTTP| Catalogo[Catálogo]
  Perfil[Perfil] -->|API HTTP| Usuario[Usuário]
  Cobranca --> Deploy1[Deploy independente]
  Busca --> Deploy2[Deploy independente]
  Perfil --> Deploy3[Deploy independente]
~~~

### Escala e implantação independentes

Quando a busca recebe mais tráfego, serviços separados permitem aumentar suas instâncias sem escalar todo o sistema.

~~~mermaid
flowchart LR
  Trafego[Mais buscas] --> Busca[Serviço de busca: mais instâncias]
  Perfil[Serviço de perfil: mantém instâncias]
~~~

### Trade-off de distribuição

Separar pode ajudar com necessidades de escala, equipes ou limites de domínio, mas chamadas de rede introduzem indisponibilidade e falhas de comunicação. A aula trata esses fatores como sinais para decidir, não como uma regra para sempre escolher microsserviços.

~~~mermaid
flowchart LR
  ServiçoA -->|rede: pode falhar| ServiçoB
  ServiçoB -->|indisponível ou resposta perdida| Decisao[Tratar falhas e consistência]
~~~

## Transcrição
