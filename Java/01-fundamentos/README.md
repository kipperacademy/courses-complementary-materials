# Curso de Java | Fundamentos da Linguagem + Programação Orientada a Objetos

Antes de colocar a mão no código, vale entender quatro características que definem o Java, porque elas influenciam diretamente a forma como a gente pensa ao escrever um programa:

1. **Orientada a objetos**, toda a sintaxe gira em torno de **classes e objetos**. Diferente de linguagens multiparadigma como JavaScript (que permite procedural, funcional ou OO), o Java te **força** a seguir esse paradigma.
2. **Fortemente tipada**, toda variável, parâmetro e retorno tem um tipo declarado, e esse tipo **não muda** ao longo da execução. Em JavaScript você pode reatribuir uma string com um número; em Java isso é erro de compilação.
3. **Independente de plataforma**, um mesmo código compilado roda em diferentes sistemas operacionais e arquiteturas ("write once, run anywhere"), algo que o Java foi um dos precursores em popularizar.
4. **Compilada e interpretada ao mesmo tempo**, você compila o `.java` para **bytecode** (`.class`), e a **JVM** interpreta esse bytecode em tempo de execução.

```bash
javac Main.java   # compila para bytecode (.class)
java Main         # executa o bytecode na JVM
```

> 🎥 Aula 1: [00:25](https://youtu.be/nODe5lFcGpg?t=25s) · Aula 2: [00:50](https://youtu.be/EpXYPB1rv4w?t=50s)

## JVM, JDK e bytecode

A **JVM (Java Virtual Machine)** é o ambiente de execução: ela interpreta o **bytecode** (o "código de máquina da JVM"), converte para as instruções do sistema operacional/hardware onde está rodando, e ainda faz o **gerenciamento de memória**, a **coleta de lixo** e o isolamento/segurança do programa. É a JVM que torna o Java independente de plataforma. Diferente do C, que é compilado direto para código de máquina e só roda na arquitetura para a qual foi compilado.

Para executar Java você precisa da JVM. Mas para **desenvolver e compilar**, você precisa do **JDK (Java Development Kit)**, que inclui o compilador (`javac`) e ferramentas de build, **além** da própria JVM. Só a JVM não te deixa compilar.

> 📌 Na prática: procure por "Java download" / "Java Download Oracle", baixe o **JDK** (ex.: OpenJDK 21 ou 17) para o seu SO e arquitetura. Verifique com `java --version`. A instrutora usa o IntelliJ IDEA na versão gratuita **Community** (existe a Ultimate paga, mas a Community atende bem). Alternativas: VS Code e Eclipse (que tem até um compilador próprio).

> 🎥 Aula 1: [04:35](https://youtu.be/nODe5lFcGpg?t=275s) · Aula 2: [02:55](https://youtu.be/EpXYPB1rv4w?t=175s)

## Estrutura básica

Todo arquivo `.java` precisa declarar **pelo menos uma classe**, e a classe principal (pública) tem que ter **o mesmo nome do arquivo**. Você pode ter outras classes no mesmo arquivo, mas só **uma pode ser `public`**, e é ela que dá nome ao arquivo (parecido com o `export default` do JavaScript).

```java
// arquivo: Main.java
public class Main {
    public static void main(String[] args) {
        System.out.println("Olá, mundo!");
    }
}
```

O método `public static void main(String[] args)` é o **ponto de entrada** do programa: é o primeiro método que a JVM aciona ao executar. Ele se chama `main` por convenção (não por causa do nome da classe). Tudo que não for acionado a partir dele não roda.

> 🎥 Aula 1: [11:15](https://youtu.be/nODe5lFcGpg?t=675s) · Aula 2: [08:20](https://youtu.be/EpXYPB1rv4w?t=500s)

## Declaração de variáveis

Uma variável é um espaço na memória para guardar um valor. Existem **duas formas** de declarar no Java:

```java
// 1) explícita: tipo + nome + valor
int idade = 20;
idade = 21; // pode atualizar o valor, mas NUNCA o tipo

// 2) var: o Java infere o tipo a partir do valor (só em escopo local)
var nome = "Fernanda"; // inferido como String
```

> 📌 Pontos importantes: na forma `var`, a atribuição do valor é **obrigatória na declaração** (sem valor, o Java não tem como inferir o tipo). E, por ser fortemente tipada, `idade = "Fernanda"` dá erro de compilação, o tipo da variável não muda depois de declarada.

> 🎥 Aula 1: [13:20](https://youtu.be/nODe5lFcGpg?t=800s) · Aula 2: [09:10](https://youtu.be/EpXYPB1rv4w?t=550s)

## Tipos primitivos

Java tem **8 tipos primitivos** (guardam o valor diretamente). **4 deles são para números inteiros** e a única diferença entre eles é a **faixa de representação**, ou seja, quanta memória ocupam:

| Tipo | Bits | Faixa aproximada |
| --- | --- | --- |
| `byte` | 8 | -128 a 127 |
| `short` | 16 | -32.768 a 32.767 |
| `int` | 32 | ~ -2,1 bi a 2,1 bi |
| `long` | 64 | enorme (números muito grandes) |

```java
byte  pequeno = 100;
int   idade   = 25;     // o mais usado no dia a dia
long  grande  = 10_000_000L;  // long precisa do sufixo L

double preco  = 19.90;  // ponto flutuante, dupla precisão
float  taxa   = 5.5f;   // float precisa do sufixo f (precisão simples)
char   letra  = 'A';    // UM caractere, aspas SIMPLES
boolean ativo = true;
```

> 📌 Por que existem 4 inteiros? Para **economizar memória**: se o valor cabe num `byte`, usar `long` desperdiça bits. Na prática a galera usa `int` para quase tudo e `long` quando o número é muito grande. Para decimais, `double` (mais preciso) ou `float` (precisão simples). `char` usa **aspas simples**; já a `String` usa **aspas duplas** e **não é um tipo primitivo**, é uma **classe** auxiliar do Java.

> 🎥 Aula 1: [17:30](https://youtu.be/nODe5lFcGpg?t=1050s) · Aula 2: [12:05](https://youtu.be/EpXYPB1rv4w?t=725s)

> 📌 Para cada primitivo existe uma classe "wrapper" (`int` → `Integer`, `double` → `Double`...). Coleções e generics só trabalham com objetos, por isso os wrappers existem.

## Condicionais e laços

A sintaxe é em inglês e parecida com a de outras linguagens: `if` ("se"), `else` ("senão") e `else if` ("senão se"). A condição vai entre parênteses e deve resultar em um booleano.

```java
if (idade > 99) {
    System.out.println("maior");
} else if (idade == 99) {
    System.out.println("igual");
} else {
    System.out.println("menor");
}

// comparar Strings é com .equals(), não com ==
if (nome.equals("Fernanda")) { /* ... */ }
if (nome.isBlank()) { /* string vazia */ }
```

Para repetições, os dois laços principais são o `for` e o `while`:

```java
// for clássico: variável de iteração, condição de parada, incremento
for (int i = 0; i < nomes.size(); i++) {
    System.out.println(nomes.get(i));
}

// while: executa enquanto a condição for verdadeira
int contador = 0;
while (contador < 10) {
    System.out.println("estou no while");
    contador++; // ATENÇÃO: se esquecer de atualizar, vira loop infinito!
}

// for-each (enhanced for): itera direto sobre os elementos
for (String nome : nomes) {
    System.out.println(nome);
}
```

> 📌 No `for` clássico você controla o índice; no `for-each` o Java itera automaticamente por toda a coleção/vetor. No `while`, é **você** quem precisa atualizar a variável de controle dentro do corpo, senão o loop nunca para.

> 🎥 Aula 1: condicionais [25:00](https://youtu.be/nODe5lFcGpg?t=1500s), loops [39:10](https://youtu.be/nODe5lFcGpg?t=2350s) · Aula 2: condicionais [17:05](https://youtu.be/EpXYPB1rv4w?t=1025s), loops [25:25](https://youtu.be/EpXYPB1rv4w?t=1525s)

## Vetores (arrays) x ArrayList

Um **vetor (array)** armazena uma coleção de valores **do mesmo tipo**, mas tem **tamanho fixo**: você define o tamanho na criação e não pode mudar. Os índices começam em **zero**.

```java
int[] numeros = {1, 2, 3, 4, 5};   // inicialização direta
System.out.println(numeros[0]);    // 1 (primeira posição = índice 0)
System.out.println(numeros.length); // tamanho do array

int[] outros = new int[5]; // vazio, mas com tamanho 5 fixo
outros[5] = 6; // ERRO: ArrayIndexOutOfBoundsException
```

Quando você não sabe quantos elementos vão aparecer (entrada do usuário, banco de dados...), o array fixo é um problema. Para uma **lista dinâmica** que cresce e diminui, usamos o **`ArrayList`**, uma **classe** da biblioteca padrão `java.util`:

```java
import java.util.ArrayList;

ArrayList<String> nomes = new ArrayList<>();
nomes.add("Fernanda");      // adiciona
nomes.add("Léo");
System.out.println(nomes.get(0)); // acessa por get(índice)
nomes.remove(0);            // remove por índice (ou pelo objeto)
System.out.println(nomes.size()); // tamanho (size, não length!)
```

> 📌 Diferenças que costumam confundir: **array** usa `[]`, acesso por `vetor[i]` e tamanho por `.length`; **ArrayList** é uma classe, usa `.add()` / `.get(i)` / `.remove()` e tamanho por `.size()`. Como o `ArrayList` guarda **objetos**, o tipo entre `<>` é o wrapper: `ArrayList<Integer>`, não `int`.

> 🎥 Aula 1: vetores [29:35](https://youtu.be/nODe5lFcGpg?t=1775s), ArrayList [35:25](https://youtu.be/nODe5lFcGpg?t=2125s) · Aula 2: vetores [18:45](https://youtu.be/EpXYPB1rv4w?t=1125s), ArrayList [22:05](https://youtu.be/EpXYPB1rv4w?t=1325s)

## Casting (conversão de tipos)

Casting é converter um **valor** de um tipo para outro. Como o tipo de uma variável não muda, na prática você cria uma variável nova com o valor convertido. Há dois casos:

```java
// CASTING IMPLÍCITO: automático, quando "cabe" (int -> double)
int idade1 = 22;
double idade2 = idade1; // o Java só aumenta a faixa de representação

// CASTING EXPLÍCITO: você avisa que aceita "perder" representação
double resultado = 22.50;
int inteiro = (int) resultado; // vira 22 (corta o que vem após a vírgula)
```

Quando a conversão não é só ampliar/cortar a faixa (ex.: `String` ↔ número), usamos métodos auxiliares das classes:

```java
int n = Integer.parseInt("10");   // String -> int
String s = String.valueOf(10);    // int -> String
String texto = String.valueOf('A'); // char -> String
char c = "Java".charAt(0);        // String -> char
```

> 📌 Nem todo valor é convertível: transformar `"Fernanda"` em `int` não faz sentido e gera erro. Como a instrutora resume, "não dá para transformar uma banana em leite", primeiro avalie se a conversão faz sentido.

> 🎥 Aula 1: [46:15](https://youtu.be/nODe5lFcGpg?t=2775s) · Aula 2: [27:30](https://youtu.be/EpXYPB1rv4w?t=1650s)

## Programação Orientada a Objetos (POO)

A POO organiza o código em torno de **objetos**, que combinam dados (**atributos**, as variáveis da classe) e comportamento (**métodos**, as funções da classe). Uma **classe** é o **molde** que define a estrutura; o **objeto** é a **instância** criada com `new`.

```java
public class Carro {
    private String modelo; // atributo

    public void acelerar() { // método
        System.out.println("acelerando o carro " + this.modelo);
    }
}

Carro meuCarro = new Carro(); // instância (objeto) do tipo Carro
meuCarro.acelerar();
```

> 📌 Um método pode retornar um valor (`String`, `int`...) ou **nada**, e nesse caso o retorno é `void`. A palavra-chave `this` se refere à **instância atual** do objeto (`this.modelo` = o atributo `modelo` deste carro específico).

> 🎥 Aula 1: [57:05](https://youtu.be/nODe5lFcGpg?t=3425s) · Aula 2: [32:30](https://youtu.be/EpXYPB1rv4w?t=1950s)

### `static`: pertence à classe, não à instância

Um membro `static` pertence à **classe em si**, não aos objetos criados a partir dela. Por isso o `main` é `static`: a JVM o chama **sem precisar instanciar** a classe. Um método `static` **não** consegue acessar atributos de instância (só outros membros `static`).

```java
public class Main {
    String nome;           // atributo de instância
    static String titulo;  // atributo de classe (compartilhado)

    public static void main(String[] args) {
        // System.out.println(nome); // ERRO: nome não é estático
        System.out.println(titulo);  // ok
    }
}
```

> 🎥 Aula 2: [33:45](https://youtu.be/EpXYPB1rv4w?t=2025s)

### Os 4 pilares da POO

**1. Encapsulamento**
Esconder os detalhes internos e expor apenas o necessário. Atributos ficam `private` e o acesso é feito por métodos (`getters`/`setters`), o que protege o estado do objeto.

```java
public class Pessoa {
    private String nome;
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
}
```

**2. Herança**
Uma classe (**subclasse**) pode herdar atributos e métodos **não privados** de outra (**superclasse**) com `extends`, reaproveitando código. A relação é "**é um tipo de**": uma `Pessoa` **é um** `Ser`, mas nem todo `Ser` é uma `Pessoa`.

```java
public class Ser {
    protected String nome;
    protected int idade;

    public Ser(String nome, int idade) {
        this.nome = nome;
        this.idade = idade;
    }
}

public class Pessoa extends Ser {
    private String sobrenome;

    public Pessoa(String nome, int idade, String sobrenome) {
        super(nome, idade); // chama o construtor da superclasse
        this.sobrenome = sobrenome;
    }
}
```

> 📌 `super(...)` chama o construtor da superclasse. Se a superclasse tem um construtor com parâmetros, a subclasse é **obrigada** a chamá-lo. Como `Pessoa` é um `Ser`, ela pode ser usada onde se espera um `Ser` (`Ser s = new Pessoa(...)`), mas o contrário não vale.

**3. Polimorfismo**
Objetos de classes diferentes respondem à **mesma mensagem de formas diferentes**. Conseguimos isso **sobrescrevendo** métodos da superclasse com `@Override`.

```java
public class Ser {
    public String saudacao() { return ""; }
}

public class Cachorro extends Ser {
    @Override
    public String saudacao() { return "au au"; }
}

public class Pessoa extends Ser {
    @Override
    public String saudacao() { return "Olá, meu nome é " + this.nome; }
}

Ser animal = new Cachorro();
System.out.println(animal.saudacao()); // "au au"
animal = new Pessoa("Fernanda", 30, "Kipper");
System.out.println(animal.saudacao()); // "Olá, meu nome é Fernanda" - decidido em tempo de execução
```

**4. Abstração**
Expor *o que* um objeto faz, escondendo *como* ele faz. Conseguimos com **classes abstratas** e **interfaces**.

```java
// interface: um contrato. Só ASSINATURAS de métodos, sem implementação.
public interface Carro {
    void acelerar();
    void frear();
    void parar();
}

public class Fusca implements Carro {
    @Override public void acelerar() { System.out.println("acelerando"); }
    @Override public void frear()    { System.out.println("freando"); }
    @Override public void parar()    { System.out.println("parado"); }
}
```

> 📌 Na **interface** a gente só define a "casquinha", a **assinatura** dos métodos, sem corpo. A classe que `implements` a interface é obrigada a implementar esses métodos, seguindo o contrato.

### Classe abstrata x Interface

- **Classe abstrata (`abstract`):** pode ter métodos com e sem implementação e atributos de estado. Uma classe só pode estender **uma** classe abstrata. Use quando há uma relação "é um tipo de".
- **Interface:** define um contrato (o quê), e uma classe pode implementar **várias** interfaces. Use para definir capacidades ("é capaz de").

> 🎥 Aula 1: interfaces [80:00](https://youtu.be/nODe5lFcGpg?t=4800s) · Aula 2: herança [50:00](https://youtu.be/EpXYPB1rv4w?t=3000s), polimorfismo [57:30](https://youtu.be/EpXYPB1rv4w?t=3450s)

## Construtores

São métodos especiais, com **o mesmo nome da classe**, chamados automaticamente quando um objeto é criado (`new`), usados para inicializar o estado. Você pode ter **vários construtores** com parâmetros diferentes (**overload**), permitindo criar o objeto de formas distintas.

```java
public class Produto {
    private String nome;
    private double preco;

    public Produto(String nome) {        // só com nome
        this.nome = nome;
    }

    public Produto(String nome, double preco) { // nome e preço
        this.nome = nome;
        this.preco = preco;
    }
}

Produto p1 = new Produto("Café");
Produto p2 = new Produto("Café", 25.0);
```

> 🎥 Aula 1: [57:55](https://youtu.be/nODe5lFcGpg?t=3475s) · Aula 2: [37:05](https://youtu.be/EpXYPB1rv4w?t=2225s)

## Modificadores de acesso e pacotes

Os modificadores de acesso controlam a **visibilidade** de classes, atributos e métodos. Para entendê-los, é preciso entender **pacotes (`package`)**: eles agrupam classes em namespaces lógicos e **respeitam a estrutura de diretórios** (uma classe no pacote `util.redes` precisa estar na pasta `util/redes`).

```java
package javacurso; // declarado no topo do arquivo, espelha a pasta
```

| Modificador | Visível em |
| --- | --- |
| `public` | qualquer lugar |
| `protected` | mesmo pacote + subclasses |
| (padrão / *default*) | apenas no mesmo pacote |
| `private` | apenas na própria classe |

> 📌 Quando você **não** coloca modificador, vale o **default** (*package-private*): visível só no mesmo pacote, nem subpacotes enxergam. A classe pública de um arquivo **tem que** ter o mesmo nome do arquivo. Um membro `private` só é acessível dentro da própria classe, mesmo por outras classes do mesmo arquivo.

> 🎥 Aula 1: [67:55](https://youtu.be/nODe5lFcGpg?t=4075s) · Aula 2: [41:15](https://youtu.be/EpXYPB1rv4w?t=2475s)

## Resumo

- Java é orientada a objetos, fortemente tipada, independente de plataforma e compila para **bytecode** que roda na **JVM**.
- **JDK** = ferramentas de desenvolvimento + compilador (`javac`) + JVM; só a JVM executa, mas não compila.
- 8 tipos primitivos; os 4 inteiros (`byte`/`short`/`int`/`long`) diferem só na faixa de representação (memória). `String` é classe, não primitivo.
- `var` infere o tipo pelo valor (só em escopo local, exige atribuição imediata).
- **Array** tem tamanho fixo (`.length`, `[]`); **ArrayList** é lista dinâmica (`.add`/`.get`/`.size`).
- Casting pode ser **implícito** (automático) ou **explícito** (`(int) x`, `Integer.parseInt`, `String.valueOf`).
- POO: classe é o molde, objeto é a instância; `static` pertence à classe.
- Pilares: **encapsulamento, herança (`extends`/`super`), polimorfismo (`@Override`), abstração** (interfaces/classes abstratas).
- Construtores inicializam o objeto e podem ser sobrecarregados; modificadores de acesso + pacotes controlam a visibilidade.
