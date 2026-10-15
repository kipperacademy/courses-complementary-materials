# Java Tópicos Avançados | Se aprofundando na linguagem

Este guia cobre os temas que separam quem usa Java de quem entende Java: programação assíncrona e paralela com threads e lambdas, os recursos modernos da linguagem e como a JVM gerencia memória através do Garbage Collector.

## Programação assíncrona com Threads e Lambdas

> 🎥 Aula 1: [08:30](https://youtu.be/epGY4wPZ4I0?t=510s)

Esta aula começa pelas **expressões lambda** (porque elas são usadas o tempo todo no código assíncrono) e termina em **programação assíncrona** com `CompletableFuture` e threads.

### Lambdas e interfaces funcionais

> 🎥 Aula 1: [08:30](https://youtu.be/epGY4wPZ4I0?t=510s)

Uma **expressão lambda** (ou função anônima) é uma forma concisa de declarar uma função inline. A sintaxe é a mesma do JavaScript: parâmetros, a seta `->` e a expressão. Ela existe só naquele momento da execução, a não ser que você salve a referência em uma variável.

Por baixo, um lambda implementa uma **interface funcional** (interface com exatamente um método abstrato). É o que torna o código assíncrono e a Stream API concisos.

```java
Runnable tarefa = () -> System.out.println("oi");
Supplier<String> nome = () -> "Kipper";
Function<Integer, Integer> dobro = n -> n * 2;
Predicate<Integer> ehPar = n -> n % 2 == 0;
```

### Method reference (`::`)

> 🎥 Aula 1: [31:30](https://youtu.be/epGY4wPZ4I0?t=1890s)

Quando o lambda **só** chama um método já existente (sem somar, filtrar ou transformar nada), dá para encurtar ainda mais usando o operador `::`, a **referência de método**:

```java
// lambda que só repassa o item para outro método...
lista.forEach(item -> System.out.println(item));

// ...vira uma referência de método, mais concisa:
lista.forEach(System.out::println);
```

Também funciona para construtores. É só uma forma mais limpa de chamar um método existente.

### Threads

> 🎥 Aula 1: [50:00](https://youtu.be/epGY4wPZ4I0?t=3000s)

Uma **thread** é uma linha de execução. Por padrão seu programa roda em uma única thread (a `main`), mas você pode criar outras para executar tarefas em paralelo, sem bloquear o fluxo principal. Isso é útil para operações **assíncronas** (que não retornam o resultado na hora), como uma consulta ao banco ou uma chamada HTTP, que de outra forma seriam **bloqueantes**.

```java
// criando uma thread com lambda (Runnable é uma interface funcional)
Thread t = new Thread(() -> {
    System.out.println("Rodando em outra thread");
});
t.start();   // dispara a execução
t.join();    // espera a thread terminar
```

> 📌 `start()` cria uma nova thread; chamar `run()` diretamente executaria no mesmo fluxo, sem paralelismo.

> ⚠️ Use threads só onde fizer sentido (uma query lenta, processamento independente em segundo plano). Lançar threads para tudo pode deixar o programa **mais lento** do que se fosse síncrono.

### CompletableFuture

> 🎥 Aula 1: [62:00](https://youtu.be/epGY4wPZ4I0?t=3720s)

`CompletableFuture` é a API do Java (evolução dos `Future`) muito usada no mercado para executar tarefas assíncronas e encadear o que fazer com o resultado, sem ficar criando thread na mão:

```java
// runAsync: dispara uma tarefa que não retorna nada
CompletableFuture.runAsync(() -> buscarNoBanco(id));

// supplyAsync: dispara uma tarefa que retorna um valor
CompletableFuture
    .supplyAsync(() -> buscarUsuario(id))
    .thenApply(usuario -> usuario.getEmail()) // transforma o resultado
    .thenAccept(email -> enviar(email))       // consome o resultado
    .thenRun(() -> log("concluído"));         // callback sem resultado
```

> 📌 `get()` **bloqueia** a thread principal até o resultado chegar. Só chame `get()` depois de já ter disparado tudo que queria paralelizar, senão o código volta a ser síncrono.

Para combinar tarefas independentes lançadas em paralelo: `thenCombine` junta o resultado de **duas**, e `CompletableFuture.allOf(...)` espera **várias**. O caso de uso clássico é disparar várias chamadas de API independentes e só agregar os resultados no final.

## Programação paralela

> 🎥 Aula 2: [06:00](https://youtu.be/C7OyP5X3bUU?t=360s)

Threads não são um conceito exclusivo do Java: é como se paraleliza um programa. Vale distinguir dois termos:

- **Concorrência:** com **um único núcleo (core)**, a CPU não executa de fato ao mesmo tempo, ela fica alternando entre tarefas tão rápido que **parece** simultâneo.
- **Paralelismo:** com **vários núcleos**, tarefas rodam de verdade ao mesmo tempo, cada uma em um núcleo.

No Java, a thread `main` é a thread principal (criada pela JVM no início do programa) e é a originadora de todas as outras. Um detalhe importante: o Java usa **threads nativas do sistema operacional**, ou seja, cada thread Java é mapeada para uma thread do SO.

```java
// duas formas de criar uma thread: instanciando Thread...
Thread t = new Thread(() -> System.out.println("Hello thread"));
t.start();   // start() lança a execução; nunca chame run() diretamente

System.out.println("Hello world");
```

> 📌 A **ordem** de execução não é garantida. Quem decide qual thread roda em cada momento é o sistema operacional, não você. Rodando o exemplo acima várias vezes, ora "Hello thread" sai primeiro, ora "Hello world".

> 📌 Uma exceção lançada dentro de uma thread **não derruba** as outras threads nem a `main`.

### Escalonador de processos e prioridade

> 🎥 Aula 2: [14:00](https://youtu.be/C7OyP5X3bUU?t=840s)

O **escalonador** do sistema operacional decide a ordem das threads na fila. Estratégias simples têm problemas:

- **First in, first serve:** uma thread enorme que chega primeiro trava todas as outras (aplicação parece "congelada").
- **Menor primeiro:** threads grandes nunca executam, caindo em **starvation** (ficam "famintas", esperando para sempre).

Por isso os SOs usam **prioridade dinâmica**: combinam uma prioridade definida com o tempo de espera e o tamanho da thread, e alternam a execução em blocos de tempo. Você pode até sugerir uma prioridade no Java, mas não tem controle real, o cálculo é dinâmico:

```java
t.setPriority(Thread.MAX_PRIORITY); // só uma sugestão; o SO decide
```

### Sincronização e race conditions

> 🎥 Aula 2: [54:30](https://youtu.be/C7OyP5X3bUU?t=3270s)

Quando várias threads acessam o mesmo dado, surgem problemas de **concorrência de recursos**:

- **Race condition:** o resultado fica inconsistente (ex.: um contador somado por várias threads dá valores aleatórios).
- **Deadlock:** duas threads ficam travadas, cada uma esperando o recurso que a outra segura.

A aula mostra duas formas de resolver race condition:

```java
// 1) ThreadLocal: cada thread tem sua própria cópia da variável
ThreadLocal<Integer> contador = ThreadLocal.withInitial(() -> 0);

// 2) Lock explícito: só quem tem a "posse" pode ler/escrever
private static final ReentrantLock lock = new ReentrantLock();

lock.lock();
try {
    contador++; // seção crítica protegida
} finally {
    lock.unlock(); // sempre liberar
}
```

> 📌 Alternativas thread-safe da própria linguagem também resolvem casos simples, como `synchronized` em um método ou `AtomicInteger` para um contador:
> ```java
> public synchronized void incrementar() { contador++; }
> AtomicInteger c = new AtomicInteger(0);
> c.incrementAndGet();
> ```

> ⚠️ Bugs de concorrência enganam: o código pode parecer correto e quebrar só em produção. **Teste muitas vezes** (centenas), não apenas uma.

### Streams paralelas e pools de threads

Para processar grandes coleções dividindo o trabalho entre os núcleos, a Stream API oferece o `parallelStream()`:

```java
long pares = numeros.parallelStream()
    .filter(n -> n % 2 == 0)
    .count();
```

Em vez de criar e destruir threads na mão, também é comum submeter tarefas a um **pool** (`ExecutorService`) que as reaproveita:

```java
ExecutorService pool = Executors.newFixedThreadPool(4);
Future<Integer> futuro = pool.submit(() -> calcular());
Integer resultado = futuro.get(); // bloqueia até ficar pronto
pool.shutdown();
```

> ⚠️ Paralelismo só vale a pena com volume grande de dados e operações independentes. Para listas pequenas, o custo de coordenar as threads pode ser maior que o ganho.

## Recursos do Java moderno

> 🎥 Aula 3: [00:00](https://youtu.be/WpVF3S4kuK0?t=0s)

Conhecer a fundo a linguagem aumenta a produtividade e o nível de senioridade. A aula apresenta cinco recursos indispensáveis do Java moderno.

### Text Blocks

> 🎥 Aula 3: [00:30](https://youtu.be/WpVF3S4kuK0?t=30s)

Introduzido no Java 17, permite declarar blocos de texto multi-linha com três aspas, sem terminadores de string nem concatenação. A leitura fica muito melhor:

```java
String json = """
    {
        "nome": "Kipper",
        "ativo": true
    }
    """;
```

### Records

> 🎥 Aula 3: [01:30](https://youtu.be/WpVF3S4kuK0?t=90s)

Introduzido no Java 14 e estável no Java 17 (LTS). Muitas vezes precisamos de uma classe que **só representa dados** (ex.: um DTO), sem lógica. Antes, isso virava algo verboso: construtor, getters, `equals`, `hashCode`, `toString` na mão. O record gera tudo isso automaticamente e é **imutável**:

```java
public record Livro(String titulo, int paginas) {}
// já vem com construtor, getters, equals, hashCode e toString prontos
```

### Switch com seta (`->`)

> 🎥 Aula 3: [04:30](https://youtu.be/WpVF3S4kuK0?t=270s)

A sintaxe de seta deixa o `switch` mais conciso e direto, sem `break`. É ideal quando cada caso executa **uma única linha**; se precisar de vários comandos, o `switch` tradicional ainda é mais adequado. Como expressão, ele também pode retornar valor:

```java
String tipo = switch (codigo) {
    case 1, 2 -> "básico";
    case 3 -> "premium";
    default -> "desconhecido";
};
```

### Lambdas para reduzir verbosidade

> 🎥 Aula 3: [07:00](https://youtu.be/WpVF3S4kuK0?t=420s)

Expressões lambda permitem declarar funções inline, reduzindo bastante o código e melhorando a leitura. O exemplo da aula ordena uma lista passando o comparador como lambda em vez de uma classe anônima:

```java
Collections.sort(numeros, (a, b) -> a - b); // uma linha no lugar de várias
```

### Inicialização concisa de listas

> 🎥 Aula 3: [09:30](https://youtu.be/WpVF3S4kuK0?t=570s)

`ArrayList` é a estrutura usada quando precisamos de uma lista de tamanho dinâmico. Em vez de vários `add()`, dá para inicializar com valores já dentro usando um bloco de inicialização:

```java
var linguagens = new ArrayList<String>() {{
    add("JS"); add("Python"); add("Java"); add("C++");
}};
```

> 📌 A inferência de tipo local com **`var`** evita repetir o tipo na declaração; o compilador o infere a partir do lado direito.

## Garbage Collector (GC)

> 🎥 Aula 4: [00:00](https://youtu.be/cgUfurMJosE?t=0s)

Em Java você **não libera memória manualmente**. O **Garbage Collector** (coletor de lixo) é o processo da JVM que identifica objetos que não são mais referenciados e libera a memória deles, evitando que a aplicação trave por falta de memória.

### Stack e Heap

> 🎥 Aula 4: [01:30](https://youtu.be/cgUfurMJosE?t=90s)

Quando um programa Java roda, a JVM organiza a memória em áreas. Duas são centrais aqui:

- **Stack (pilha):** guarda a pilha de chamadas de métodos, com as **variáveis primitivas locais** de cada método e a **referência** para objetos. Quando o método termina, tudo isso é removido da stack. Recursão sem fim enche a stack: é o famoso `StackOverflowError`.
- **Heap:** é onde os **objetos** (instâncias) realmente vivem. A stack só guarda uma referência para o endereço do objeto na heap.

```java
Produto p = new Produto("Café", 25); // 'p' (referência) na stack, objeto na heap
p = null; // a referência sumiu -> o objeto na heap vira "lixo" para o GC
```

> 📌 Se o método termina e a referência some, mas o objeto continua na heap sem ninguém apontando para ele, ele vira lixo. É justamente esse objeto que o GC vai limpar.

### Como ele decide o que remover (Mark and Sweep)

> 🎥 Aula 4: [09:30](https://youtu.be/cgUfurMJosE?t=570s)

Um objeto é coletável quando **não há mais nenhuma referência alcançável** a ele. O algoritmo clássico é o **Mark and Sweep**: ele *marca* os objetos ainda referenciados (buscando referências na stack e em outras partes do programa) e considera o resto como espaço livre, reutilizável. Simples, porém eficiente, e é um conceito comum à maioria das linguagens.

### Gerações da memória (Heap)

A JVM divide a heap em gerações, partindo da observação de que a maioria dos objetos morre jovem:

- **Young Generation:** onde objetos novos nascem. A maioria é coletada rápido aqui (Minor GC, barato).
- **Old Generation:** objetos que sobreviveram a várias coletas "envelhecem" e vão para cá (Major GC, mais caro).

### Os coletores do Java

> 🎥 Aula 4: [11:00](https://youtu.be/cgUfurMJosE?t=660s)

A escolha de um coletor é sempre um **trade-off** entre throughput, latência e consumo de recursos:

- **Serial GC:** o mais simples, single-thread. Bom para aplicações pequenas.
- **Parallel GC:** usa várias threads, maximiza throughput. Ideal para processamento em *batch*, mas com pausas.
- **G1 (Garbage First):** o **padrão** atual. Coleta primeiro as regiões mais "lotadas" de lixo, sem varrer a heap inteira, equilibrando latência e throughput. Não é ideal para heaps muito pequenas.
- **ZGC:** mira **latência ultrabaixa**, fazendo marcação e até compactação da memória de forma concorrente, quase sem parar a aplicação. Em troca, consome bem mais CPU; é "uma bazuca" para aplicações pequenas.

### Pontos importantes

- O GC roda automaticamente; você não controla *quando*.
- `System.gc()` é apenas uma **sugestão**, não uma garantia, evite depender dele.
- Vazamentos de memória em Java acontecem quando você mantém referências vivas sem querer (ex.: coleções estáticas que só crescem).

## Resumo

- **Lambdas** implementam interfaces funcionais e deixam o código conciso; `::` (method reference) encurta quando o lambda só repassa para outro método.
- **Threads** executam tarefas de forma assíncrona/paralela; prefira `CompletableFuture` (`supplyAsync`, `thenApply`, `thenCombine`, `allOf`) e pools (`ExecutorService`) a criar threads na mão. `get()` bloqueia.
- A **ordem das threads é decidida pelo SO** (escalonador + prioridade dinâmica); cuidado com **race condition**, **deadlock** e **starvation**. Proteja estado compartilhado com `ThreadLocal`, `ReentrantLock`, `synchronized` ou classes `Atomic`, e teste muitas vezes.
- Java moderno: **Text Blocks, Records, switch com seta, lambdas e inicialização concisa de listas** (e `var`).
- A memória se divide em **stack** (primitivas e referências) e **heap** (objetos). O **Garbage Collector** usa **Mark and Sweep** e gerações (Young/Old) para liberar objetos sem referências. Coletores: **Serial, Parallel, G1 (padrão) e ZGC**, cada um um trade-off entre latência e throughput.
