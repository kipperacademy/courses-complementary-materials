# Aula 2 — Design Patterns na prática

A gravação apresenta Factory, Singleton, Strategy e Adapter. A montagem explícita pelo main demonstra injeção de dependência (DI). O exemplo integrado é um checkout com política de preço, fornecedores de pagamento e um SDK legado.

## Conceitos apresentados

### Factory

Concentra a criação e escolhe a implementação; o checkout recebe o contrato Pagamento pronto.

~~~mermaid
flowchart LR
  Main --> Factory[PagamentoFactory.criar]
  Factory -->|LEGADO| Adapter[PagamentoLegadoAdapter]
  Factory -->|LOCAL| Local[PagamentoLocal]
  Adapter --> Contrato[Pagamento]
  Local --> Contrato
  Contrato --> Checkout
~~~

### Singleton

Fornece a mesma instância durante a execução do programa. A demonstração usa o registro de compras compartilhado por Ana e Bia; o estado volta ao reiniciar o processo.

~~~mermaid
flowchart LR
  Ana --> Registro[RegistroCompras: mesma instância]
  Bia --> Registro
  Registro --> Contador[contador compartilhado]
~~~

### Strategy

Permite selecionar uma regra de preço mantendo o mesmo contrato: desconto Pix ou preço sem desconto.

~~~mermaid
flowchart LR
  Checkout --> Politica[PoliticaPreco]
  Pix[DescontoPix] -. implementa .-> Politica
  Sem[SemDesconto] -. implementa .-> Politica
~~~

### Adapter

Traduz a chamada em centavos do checkout para a API legada que recebe valor em reais como texto e moeda BRL.

~~~mermaid
flowchart LR
  Checkout -->|centavos| Adapter[PagamentoLegadoAdapter]
  Adapter -->|reais em texto + BRL| SDK[SdkLegado]
  SDK -->|resposta convertida| Checkout
~~~

### Injeção de dependência

O main monta as peças e entrega suas referências ao checkout; o checkout usa os objetos recebidos.

~~~mermaid
flowchart LR
  Main -->|injeta| Checkout
  Main --> Strategy[PoliticaPreco]
  Main --> Factory[Factory cria Pagamento]
  Main --> Singleton[RegistroCompras]
~~~

## Código de exemplo

Os arquivos Java em `pratica/` demonstram os padrões apresentados na aula por meio do fluxo de compra e pagamento.

Para compilar e executar uma demonstração a partir da pasta `Arquitetura/`:

~~~bash
javac -d /tmp/aula2 02-design-patterns/pratica/*.java
java -cp /tmp/aula2 DemoCompra
~~~
