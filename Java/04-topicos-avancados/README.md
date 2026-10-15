# LIVE: Java Intensivo

# 💻 Código base

[https://github.com/Fernanda-Kipper/java-language-examples](https://github.com/Fernanda-Kipper/java-language-examples)

### Para executar

Execute os seguintes comandos no terminal, lembrando de abrir  na pasta raiz do projeto:

```bash
$ javac Main.java
$ java Main
```

# 🔍 Expressões Lambda

As funções lambda, também conhecidas como expressões lambda, são uma maneira concisa de representar funções anônimas em Java. Elas nos permitem definir uma implementação de uma interface funcional em uma única linha de código.

```java
(parameters) -> expression or statement block
```

# 🛜 Streams API

A API de streams foi introduzida no Java na versão 8, e seu objetivo é facilitar trabalhar com conjuntos de dados. A ideia é iterar sobre as coleções de objetos, e a cada elemento, realizar alguma ação ou aplicar alguma função.

1. **Criando Streams**

```java
List<String> frutas = new ArrayList<String>();

items.add("maçã");
items.add("banana");

Stream<String> stream = frutas.stream();
```

1. **Iterando sob streams**

```java
List<String> frutas = new ArrayList<String>();

items.add("maçã");
items.add("banana");

frutas.stream().forEach(fruta -> System.out.println(fruta));
```

1. Métodos úteis

Normalmente, para realizar ações em uma lista, como filtros e operações matemáticas, é necessário efetuar um loop sobre seus itens. Com a Streams API esse tipo de tarefa também foi simplificado, bastando agora fazer chamadas a métodos específicos que, em conjunto com as expressões lambda recebidas como parâmetro, se responsabilizam por percorrer a coleção e retornar apenas o resultado esperado.

- filter()
- mapToInt()

**Método de Referência :: em Java**

O operador :: é conhecido como **método de referência** e é uma forma concisa de referenciar métodos diretamente. Em vez de usar uma expressão lambda para definir o comportamento de um método, você pode usar :: para referenciar um método existente. Isso torna o código mais limpo e legível.

```java
List<String> frutas = new ArrayList<>();
frutas.add("maçã");
frutas.add("banana");

// Usando referência de método para imprimir cada fruta
frutas.stream().forEach(System.out::println);
```

# 🔄 Programação Async

A programação assíncrona permite a execução de tarefas em segundo plano, sem bloquear o fluxo principal do programa, ou seja, **non-blocking code**. 

## Completable Future

CompletableFuture é uma evolução do Future, permitindo executar tarefas assíncronas e combinar várias tarefas de forma fluida. Ele implementa a interface CompletionStage, que permite adicionar callbacks após a conclusão da tarefa.

**1. Aguardando Resultados**

Para esperar o resultado de um CompletableFuture, usamos o método get(), que é **bloqueante** — o código vai aguardar o término da tarefa para continuar.

```java
CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> "Result");
String result = future.get(); // Bloqueia até o futuro ser completado
```

**2. Completando um CompletableFuture**

O método complete() permite completar manualmente um CompletableFuture.

```java
CompletableFuture<String> future = new CompletableFuture<>();
future.complete("Manually completed!");
System.out.println(future.get()); // Output: "Manually completed!"
```

**3. Valor Padrão com getNow()**

O método getNow() retorna o valor imediatamente se o futuro já estiver completo, ou um valor padrão caso contrário.

```java
CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> "Async Result");
String result = future.getNow("Default Value");
System.out.println(result); // Output: "Default Value" (se ainda não completado)
```

**4. Execução Assíncrona**

•	runAsync(): Executa uma tarefa assíncrona sem retorno (void).

```java
CompletableFuture.runAsync(() -> System.out.println("Task executed asynchronously"));
```

	•	supplyAsync(): Executa uma tarefa assíncrona que retorna um valor.

```java
CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> "Async Result");
System.out.println(future.join()); // Output: "Async Result"
```

**5. Callbacks**

Podemos adicionar callbacks que serão executados quando o CompletableFuture for completado.

Os callbacks são todos executados na mesma thread

•	thenAccept(): Executa um callback que aceita o resultado da tarefa.

```java
CompletableFuture.supplyAsync(() -> "Task Result")
    .thenAccept(result -> System.out.println("Received: " + result));
```

•	thenRun(): Executa um callback sem acessar o resultado.

```java
CompletableFuture.supplyAsync(() -> "Task Result")
    .thenRun(() -> System.out.println("Task finished"));
```

**6. Execução Assíncrona de Callbacks**

Para executar callbacks em uma thread separada, use as variantes thenAcceptAsync(), thenRunAsync(), etc.

```java
CompletableFuture.supplyAsync(() -> "Task Result")
    .thenAcceptAsync(result -> System.out.println("Async callback: " + result));
```

**7. Composição com thenCompose()**

thenCompose() é usado para encadear dois CompletableFutures, onde o segundo depende do resultado do primeiro.

Ou seja, ele é usado para  encadear duas operações assíncronas onde a **segunda operação depende do resultado da primeira**. Em outras palavras, o resultado cru do primeiro CompletableFuture é usado para iniciar o segundo CompletableFuture.

```java
CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> "Hello")
    .thenCompose(result -> CompletableFuture.supplyAsync(() -> result + " World"));
System.out.println(future.join()); // Output: "Hello World"
```

**8. Tratamento de Exceções com exceptionally()**

O método exceptionally() permite tratar exceções ocorridas durante a execução assíncrona.

```java
CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
    throw new RuntimeException("Error");
}).exceptionally(ex -> "Handled Error");
System.out.println(future.join()); // Output: "Handled Error"
```

**9. Combinação de Futuros com allOf()**

allOf() é usado para combinar vários CompletableFutures de forma não bloqueante. Permitindo executar vários de maneira paralela, e depois aguardar até que todos sejam completados.

```java
CompletableFuture<Void> future1 = CompletableFuture.runAsync(() -> System.out.println("Task 1"));
CompletableFuture<Void> future2 = CompletableFuture.runAsync(() -> System.out.println("Task 2"));

CompletableFuture<Void> allOfFutures = CompletableFuture.allOf(future1, future2);
allOfFutures.join(); // Espera ambos completarem
System.out.println("Both tasks completed");
```

**10. Combinação de Resultados com thenCombine()**

thenCombine() combina os resultados de dois CompletableFutures executados de maneira independentes.

Muito util para quando precisamos disparar duas chamadas async distintas e depois combinar o resultado delas para uma terceira chamada ou para retornar para o usuário. Ele espera que **ambos os futures sejam completados** e, em seguida, combina os resultados de ambos em uma nova operação. As operações são executadas em paralelo (independentemente).

```java
CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> "Hello");
CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> "World");

CompletableFuture<String> combinedFuture = future1.thenCombine(future2, (result1, result2) -> result1 + " " + result2);
System.out.println(combinedFuture.join()); // Output: "Hello World"
```

**11. Bloqueio com join()**

join() é similar ao get(), mas não exige lidar com exceções verificadas.

```java
CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> "Result");
String result = future.join(); // Bloqueia até o CompletableFuture ser completado
```

# Threads

As threads são unidades de execução que permitem a paralelização em programas Java. 

As threads em java são subdivisões do processo (programa em java) que trabalham paralelamente, mas na visão do processador, nós estamos executando somente um processo.

Elas podem ser mapeadas para diferentes núcleos da CPU, permitindo concorrência e, em alguns casos, paralelismo real.

Já se houver apenas um núcleo, o sistema alterna a execução entre as threads, gerando apenas concorrência, sem paralelismo real.

<aside>
💡

O Java utiliza **threads de nível de sistema operacional**, ou seja, as threads Java são geralmente mapeadas para threads nativas do sistema operacional (POSIX threads no Linux, por exemplo).

</aside>

Uma característica importante é que se uma thread lançar uma exceção, não irá afetar a execução das demais.

Outro fato, é que não há garantia de qual thread irá executar primeiro. Não temos controle sobre o escalonador dos processos.

**1. Execução de Threads**

Há duas formas comuns de criar threads em Java.

•	**Extendendo a classe** Thread.

```java
class MyThread extends Thread {
    public void run() {
        System.out.println("Thread is running");
    }
}

MyThread thread = new MyThread();
thread.start(); // Inicia a thread
```

•	**Implementando a interface** Runnable.

```java
class MyRunnable implements Runnable {
    public void run() {
        System.out.println("Thread is running");
    }
}

Thread thread = new Thread(new MyRunnable());
thread.start(); // Inicia a thread
```

•	isAlive(): Verifica se a thread ainda está em execução.

```java
Thread thread = new Thread(() -> {
    System.out.println("Running task");
});
thread.start();
System.out.println(thread.isAlive()); // Verifica se a thread está ativa
```

**4. Concorrência vs. Paralelismo**

•	**Concorrência**: As threads parecem ser executadas simultaneamente, mas na verdade estão compartilhando o tempo de execução no processador.

•	**Paralelismo**: Se o sistema tiver múltiplos núcleos, as threads podem ser executadas em núcleos diferentes ao mesmo tempo, oferecendo paralelismo real.