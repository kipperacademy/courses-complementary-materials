# Java Intermediário | Estruturas de dados, Stream API, Generics, Packages e Records

Depois de dominar os fundamentos e a POO, o passo intermediário é conhecer as ferramentas que você vai usar todos os dias em código real: a organização em **packages**, as **estruturas de dados** do Collections Framework, os **generics** para reuso seguro, os **records** e a **Stream API**. São conceitos independentes da primeira aula: se você já está familiarizado com a sintaxe base do Java, pode acompanhar sem problema.

## Packages

> 🎥 No vídeo: [00:50](https://youtu.be/jLropHUgI1A?t=50s)

**Packages** servem para organizar suas classes de forma **hierárquica** dentro do projeto. A estrutura dos packages reflete a estrutura de pastas, e isso é importante para a manutenção do código: cada package representa uma entidade, funcionalidade ou responsabilidade, o que separa responsabilidades e ainda evita conflito de nomes (você pode ter duas classes com o mesmo nome em packages diferentes).

Quando você cria um projeto, a IDE já adiciona a declaração de `package` no topo da classe, respeitando o caminho das pastas a partir do **group** definido na criação do projeto. Pastas padrão como `src`, `main`, `java` e `resources` são ignoradas no nome do package, elas dizem respeito à estrutura geral do projeto (código-fonte, testes, recursos como JSON/imagens/HTML), não à hierarquia das suas classes.

```java
package com.kipper.javacourse.carro;

import java.util.List;                    // importando uma classe
import com.kipper.javacourse.carro.*;     // importando um pacote inteiro
```

### Modificadores de acesso e visibilidade entre packages

Esse é o ponto que a professora mais detalha. Por padrão as classes vêm com `public`, ou seja, são **visíveis fora do package** onde estão. Mas e quando um atributo ou método **não tem nenhum modificador**?

```java
package com.kipper.javacourse.carro;

public class Carro {
    String modelo;          // sem modificador = package-private
    public String cor;      // visível fora do package

    void teste() {          // package-private: só dentro do package carro
        System.out.println("teste");
    }
}
```

- **`public`**: visível para qualquer classe, dentro ou fora do package.
- **sem modificador (package-private)**: visível **só** para classes do **mesmo package**. Uma classe em outro package (ex.: `Pessoa` em `com.kipper.javacourse.pessoa`) não enxerga `modelo` nem chama `teste()` (erro: *"is not public in ... cannot be accessed from outside package"*).
- **`private`**: visível só dentro da própria classe; nem classes do mesmo package acessam.
- **`protected`**: visível no mesmo package **e** nas subclasses, mesmo que a subclasse esteja em outro package.

> 📌 **package-private vs `protected`:** olhando de fora parecem iguais (ambos visíveis dentro do package). A diferença está nas **subclasses**: `protected` continua visível em uma subclasse de outro package; package-private não.

## Estruturas de dados (Collections Framework)

> 🎥 No vídeo: [10:50](https://youtu.be/jLropHUgI1A?t=650s)

O **Java Collections Framework** é o framework que exporta um conjunto de **interfaces** e **classes** para armazenar, organizar e manipular grupos de objetos. Os quatro tipos principais são interfaces, a implementação concreta é uma classe à parte:

- **`Map`**: estrutura de **chave → valor** (como os objetos do JavaScript ou os dicionários do Python).
- **`Set`**: coleção que **não permite duplicatas**.
- **`List`**: lista **ordenada** que **permite elementos duplicados**.
- **`Queue`**: **fila**, usada para processamento em ordem (o primeiro que entra é o primeiro a sair).

### List: ordenada, aceita duplicados

`List.of(...)` cria uma lista já com valores fixos. Quando os valores vão chegar dinamicamente, use `new ArrayList<>()` e vá adicionando:

```java
List<String> lista = new ArrayList<>();
lista.add("Fernanda");
lista.add("Fernanda");      // permitido, lista aceita duplicados
lista.get(0);               // recupera pelo índice (ordem de inserção)
```

`ArrayList` é rápido para leitura por índice; `LinkedList` é melhor para muitas inserções/remoções no meio.

### Set: sem duplicados

`HashSet` é a implementação mais comum da interface `Set`. Internamente usa uma **hash table** (tabela de dispersão), técnica geral da computação que evita colisões e dá eficiência nas operações de adição, remoção e verificação de existência.

```java
Set<String> setStrings = new HashSet<>();
setStrings.add("Fernanda");
setStrings.add("Fernanda");          // ignorado: já existe (a IDE avisa "duplicate")
setStrings.contains("Fernanda");     // true, é assim que se verifica um valor no Set
```

`HashSet` não garante ordem; `LinkedHashSet` mantém a ordem de inserção; `TreeSet` mantém ordenado. Como `Set` é uma interface, você poderia criar sua própria implementação, mas o comum é usar `HashSet`.

### Map: pares chave → valor

No `Map` você precisa tipar **a chave e o valor** (diferente de `List`/`Set`, que têm um tipo só). `HashMap` é a implementação mais popular (também baseada em hash table):

```java
Map<String, String> map = new HashMap<>();
map.put("name", "Fernanda");      // put = chave, valor
map.put("surname", "Kipper");
map.get("name");                  // "Fernanda"
map.get("teste");                 // null, chave inexistente NÃO dá erro, retorna null
```

> 📌 Recuperar uma chave que não existe retorna `null` (não lança exceção). Cuidado ao reutilizar esse valor, verifique se não veio `null`.

### Queue: fila (FIFO)

Uma fila pode ser implementada com `LinkedList` (lista duplamente encadeada, em que cada posição aponta para a próxima e a anterior). A interface `Queue` expõe métodos de fila:

```java
Queue<String> fila = new LinkedList<>();
fila.add("Fernanda");
fila.add("Léo");

fila.poll();     // retorna E REMOVE o primeiro ("Fernanda"); retorna null se vazia
fila.peek();     // só ESPIA o primeiro, sem remover
fila.remove();   // remove o primeiro, mas LANÇA exceção se a fila estiver vazia
```

> 📌 `poll` vs `remove`: ambos pegam e removem a cabeça da fila. A diferença é só o caso da fila vazia, `poll` retorna `null`, `remove` lança exceção.

A mesma `LinkedList`, se tipada diretamente como `LinkedList<>` (em vez de `Queue<>`), expõe mais métodos de lista encadeada pura: `addFirst`, `addLast`, `getFirst`, `getLast`, `pollFirst`, `pollLast`.

### Quando usar cada uma

| Precisa de... | Use |
| --- | --- |
| Ordem e acesso por índice, aceita repetição | `List` / `ArrayList` |
| Garantir itens únicos | `Set` / `HashSet` |
| Associar uma chave a um valor | `Map` / `HashMap` |
| Processar elementos em ordem de chegada (FIFO) | `Queue` / `LinkedList` |
| Coleção sempre ordenada | `TreeSet` / `TreeMap` |

## Generics

> 🎥 No vídeo: [26:40](https://youtu.be/jLropHUgI1A?t=1600s)

Você já vinha usando generics sem saber: quando declarou `Queue<String>` ou `Map<String, String>`, passou o **tipo por parâmetro**. Generics são **tipos parametrizados** que permitem criar classes, interfaces e métodos reutilizáveis por diferentes tipos, mantendo a segurança em tempo de compilação.

A interface `Queue<E>` recebe um *type parameter* `E` (o tipo dos elementos da fila). Por isso você não precisa de uma fila para `String`, outra para `Integer`, outra para `Boolean`, o comportamento é o mesmo, só o tipo muda.

```java
public class Caixa<T> {
    private T conteudo;
    public void guardar(T item) { this.conteudo = item; }
    public T pegar() { return conteudo; }
}

Caixa<String> caixa = new Caixa<>();
caixa.guardar("livro");
String item = caixa.pegar(); // sem cast, sem risco
```

### Generics com restrição (bounded type)

Quando o tipo é totalmente livre (como em `Queue<E>`), você só armazena/recupera o elemento, sem chamar métodos dele. Mas se você precisa **chamar um método específico** do tipo genérico, ele tem que estender uma interface/classe conhecida:

```java
public interface Pintavel {
    void aplicarTinta();
    String getCor();
}

// só aceita tipos que implementem Pintavel
public class Pintura<T extends Pintavel> {
    private List<T> coisasQueVouPintar = new ArrayList<>();

    public void pintar(T coisa) {
        coisa.aplicarTinta();          // só funciona porque T é Pintavel
        coisasQueVouPintar.add(coisa);
    }
}
```

Assim `Pintura` aceita um `Carro`, uma `Parede`, uma `Bicicleta`, qualquer coisa que implemente `Pintavel`. A letra do parâmetro (`T`, `E`, etc.) é livre; por convenção usa-se um único caractere. Generics não são exclusivos do Java; quem já usou TypeScript vai reconhecer o conceito.

## Records

> 🎥 No vídeo: [34:10](https://youtu.be/jLropHUgI1A?t=2050s)

`record` é um recurso mais recente: uma nova forma de declarar uma **classe imutável**. Os dados são definidos na construção do objeto e **não podem mais ser alterados** depois. O compilador gera automaticamente construtor, getters, `equals`, `hashCode` e `toString`, eliminando o código boilerplate.

```java
public record Carro(String modelo, String cor, int ano, String placa) {}

Carro saveiro = new Carro("Saveiro", "Preto", 2020, "ABC1D23");
saveiro.ano();      // getter automático (sem "get"), retorna 2020
// saveiro.ano = 2021;  // ERRO: record é imutável
```

Não há setters (seria contra a imutabilidade), e os getters não levam o prefixo `get`, são o próprio nome do atributo.

**Para que servem, se não posso alterar os valores?** Records são muito usados para:

- **DTOs (Data Transfer Objects)**: dados que você **recebe uma vez** (ex.: o corpo de uma requisição HTTP no seu controller) e não vai alterar, só consultar/usar. Usar records para DTOs tem sido o padrão na comunidade.
- **POJOs** (Plain Old Java Objects) imutáveis e value objects em geral.

## Stream API

> 🎥 No vídeo: [39:35](https://youtu.be/jLropHUgI1A?t=2375s)

A **Stream API** (também recente) permite realizar **operações funcionais** sobre as collections de forma **declarativa**, encadeando filtros, transformações e reduções. A sintaxe tem uma pegada de programação funcional, o que pode confundir no começo.

A ideia central: você transforma a coleção em uma **stream**, aplica as operações, e **retransforma** o resultado de volta para a estrutura de dados que quer manipular.

```java
List<String> nomes = List.of(
    "Fernanda Kipper", "Fernanda Santos", "Fernanda Moreira",
    "Eduardo Moreira", "Ariel Souza"
);

// filtrar só as "Fernanda", em maiúsculas, de volta para uma List
List<String> fernandas = nomes.stream()        // vira uma stream
    .filter(nome -> nome.startsWith("Fernanda"))// operação intermediária
    .map(String::toUpperCase)                   // operação intermediária
    .toList();                                  // retransforma em List (terminal)
```

> 📌 Depois de virar `stream`, o objeto **é** uma stream. Para ter o resultado de volta como estrutura de dados, você **precisa** retransformar no final (`toList()`, `collect(...)`, etc.). Esquecer isso gera erro de tipo (*"required List but provided Stream"*).

Operações comuns:

- **`filter`**: mantém só os elementos que passam na condição.
- **`map`**: transforma cada elemento em outra coisa (ex.: `nome -> nome.toUpperCase()`, ou a forma mais concisa por *method reference* `String::toUpperCase`).
- **`reduce`**: combina todos os elementos em um único valor. Recebe um valor inicial (`0` para somar inteiros, `""` para concatenar strings) e já retorna o tipo reduzido, sem precisar de `toString` no final:

```java
String concatenado = nomes.stream()
    .map(nome -> nome.replace(" ", ""))
    .reduce("", (a, b) -> a + b);   // junta tudo numa única String
```

- **`collect`**: materializa em outras collections além de `List`:

```java
Set<String> set = nomes.stream()
    .filter(nome -> nome.startsWith("Fernanda"))
    .collect(Collectors.toSet());
```

A Stream API é **extremamente** usada em projetos Java reais: sem ela, seria preciso escrever `for` manuais e bem mais verbosos sobre listas, sets e maps. Com ela, a manipulação de estruturas de dados fica sucinta e legível.

## Optional

`Optional<T>` representa um valor que **pode ou não existir**, evitando `NullPointerException` e deixando a ausência explícita.

```java
Optional<String> achado = nomes.stream()
    .filter(nome -> nome.equals("Fernanda Kipper"))
    .findFirst();

achado.ifPresent(System.out::println);
String nome = achado.orElse("Padrão");
```

## Resumo

- **Packages** organizam o código de forma hierárquica e definem visibilidade. Atributo/método sem modificador é **package-private** (só dentro do package); a diferença para `protected` está nas **subclasses**.
- **Collections:** `List` (ordenada, duplica), `Set` (única), `Map` (chave/valor), `Queue` (fila FIFO). `HashSet`/`HashMap` usam hash table; `LinkedList` implementa a fila.
- **Generics** dão reuso com segurança de tipos; use `<T extends Interface>` quando precisar chamar métodos do tipo genérico.
- **Records** são classes imutáveis sem boilerplate, ideais para DTOs e POJOs.
- **Stream API** processa coleções de forma declarativa (`filter`/`map`/`reduce`/`collect`); lembre de retransformar a stream em coleção no final.
- **Optional** torna a ausência de valor explícita e segura.
