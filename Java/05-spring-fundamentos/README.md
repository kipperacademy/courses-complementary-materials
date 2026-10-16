# Curso de Java Spring | Fundamentos do Framework

Spring é o framework mais usado para construir aplicações backend em Java. Antes de tudo, vale entender uma distinção: o **Spring Framework** é a base; o **Spring Boot** é um framework construído sobre ele que facilita a criação de aplicações Spring-based, abstraindo configurações extensas e habilitando o modo *"Just Run"*, você inicia o projeto e ele já está pronto e funcionando, sem configuração manual.

> 🎥 No vídeo: [00:30](https://youtu.be/YY_hf0FOIcU?t=30s)

## Criando um projeto com o Spring Initializr

A forma mais simples de começar é pelo [Spring Initializr](https://start.spring.io/), uma ferramenta online que gera rapidamente um projeto Spring Boot pronto para baixar. No site você define:

- **Gestor de dependências**: Maven (usado no curso) ou Gradle.
- **Versão do Spring Boot**: prefira a versão estável marcada (no vídeo, `3.3.2`).
- **Linguagem**: Java (também é possível Kotlin ou Groovy).
- **Group**: o domínio do projeto/empresa (ex.: `com.kipper`). Vira o pacote raiz dentro da estrutura.
- **Artifact**: o nome do projeto (ex.: `first-spring-app`).

Dependências adicionadas no vídeo:

- **Spring Web**: módulo para construir aplicações web, incluindo APIs RESTful.
- **Spring Boot DevTools**: ferramentas de desenvolvimento como Live Reload e fast restart.
- **Lombok**: biblioteca para gerar código boilerplate (getters, setters, construtores) com anotações.

> 📌 O `spring-boot-starter-test` já vem por padrão, mesmo sem você adicioná-lo.

Depois é só clicar em **Generate**, descompactar o `.zip` e abrir na IDE (no vídeo, IntelliJ).

```bash
./mvnw spring-boot:run   # roda a aplicação
```

> 🎥 No vídeo: [01:00](https://youtu.be/YY_hf0FOIcU?t=60s)

## Estrutura de um projeto Spring

Entender as pastas evita o susto inicial:

- **`pom.xml`**: arquivo do Maven onde ficam listadas as dependências (cada uma com sua versão).
- **`.mvn/`**: configurações do Maven; só é mexida em projetos mais avançados.
- **`src/main/java`**: onde fica a lógica da aplicação (suas classes).
- **`src/main/resources`**: arquivos de configuração (`application.properties`), templates, arquivos estáticos (CSS, JS) e scripts SQL (migrations).
- **`src/test/java`**: testes unitários. Já vem um teste padrão que só verifica se o contexto da aplicação carrega (`contextLoads`).

> 🎥 No vídeo: [05:30](https://youtu.be/YY_hf0FOIcU?t=330s)

### A classe principal

Sempre tem o nome do projeto seguido do sufixo `Application`. É o **ponto de entrada**: chama `SpringApplication.run`, que carrega todo o contexto e inicializa o resto da aplicação. Assim como qualquer aplicação Java, parte de um `public static void main`.

```java
@SpringBootApplication
public class FirstSpringAppApplication {
    public static void main(String[] args) {
        SpringApplication.run(FirstSpringAppApplication.class, args);
    }
}
```

A anotação **`@SpringBootApplication`** define a porta de entrada do Spring Boot e é, na verdade, uma combinação de três anotações:

| Anotação combinada | O que faz |
| --- | --- |
| `@Configuration` | indica que a classe pode definir beans |
| `@EnableAutoConfiguration` | ativa a autoconfiguração do Spring Boot (os defaults que ele aplica por você) |
| `@ComponentScan` | escaneia o pacote atrás de componentes, services, controllers e classes de configuração para gerenciá-los |

> 📌 Todo o Spring funciona em cima de **anotações** (os `@` em cima de classes, métodos e parâmetros). Elas abstraem configurações que o Spring aplica por baixo dos panos, é um *design pattern* que se popularizou muito por causa do framework.

> 🎥 No vídeo: [09:00](https://youtu.be/YY_hf0FOIcU?t=540s)

## Controllers e a API REST

A primeira classe a criar é um **Controller**, a classe que recebe requisições HTTP e monta as respostas. Marque-a com **`@RestController`**, que combina `@Controller` + `@ResponseBody`.

Por que `@ResponseBody`? Um `@Controller` puro poderia renderizar uma página HTML (modelo mais antigo, com front e back juntos). Como aqui construímos uma **API REST** que só devolve dados (JSON/XML) no corpo da resposta, usamos `@RestController`.

> 📌 **Stateless vs stateful**: APIs REST são *stateless*, o servidor não guarda o estado do cliente, então cada requisição precisa enviar tudo que a API precisa (ex.: um token de autenticação). Numa API *stateful*, o servidor mantém o estado de cada cliente entre requisições. A instrutora frisa: entenda o *porquê*, não só replique anotações.

```java
@RestController
@RequestMapping("/hello-world")
public class HelloWorldController {

    @GetMapping
    public String helloWorld() {
        return "Hello World";
    }
}
```

- **`@RequestMapping("/hello-world")`** define o caminho base que esse controller escuta.
- **`@GetMapping`** mapeia o método para o verbo HTTP GET. Sem indicar o verbo, o Spring não sabe se o método responde a GET, POST, DELETE etc.

Ao executar a aplicação (botão Run no IntelliJ), o servidor embutido **Tomcat** sobe na porta **8080**. Acessando `http://localhost:8080/hello-world` no navegador, você vê a string retornada. Um caminho inexistente devolve a página de erro genérica **Whitelabel** (404), que expõe a stack trace e idealmente deve ser tratada depois.

> 🎥 No vídeo: [13:00](https://youtu.be/YY_hf0FOIcU?t=780s)

## Configuração com `application.properties`

No `src/main/resources/application.properties` ficam as configurações gerais da aplicação. Exemplo: trocar a porta padrão.

```properties
server.port=3000
```

Valores podem ser fixos ou vir de **variáveis de ambiente**, com fallback:

```properties
spring.datasource.url=${DB_HOST:jdbc:postgresql://localhost:5432/meudb}
spring.datasource.username=${DB_USER:postgres}
spring.datasource.password=${DB_PASSWORD:senha}
```

### Perfis de configuração (profiles)

O Spring permite ter configurações diferentes por ambiente (produção, teste, dev). Crie arquivos como `application-dev.properties` e indique o ativo:

```properties
# application.properties
spring.profiles.active=${ACTIVE_PROFILE:dev}
```

```properties
# application-dev.properties
server.port=8080
```

Assim, com o profile `dev` ativo, a aplicação usa a porta `8080` definida no arquivo do perfil. O valor do profile também pode vir de variável de ambiente, com `dev` como fallback.

> 🎥 No vídeo: [25:00](https://youtu.be/YY_hf0FOIcU?t=1500s)

## Services e Injeção de Dependência

A lógica de negócio (as regras definidas pelo time/produto) fica nas classes de **Service**. O Controller só recebe a requisição e delega o trabalho pesado ao Service.

```java
@Service
public class HelloWorldService {
    public String helloWorld(String name) {
        return "Hello World " + name;
    }
}
```

A anotação **`@Service`** indica ao Spring que essa classe é gerenciada por ele. Assim, qualquer classe que precisar dela recebe a instância automaticamente, esse é o coração do Spring: o **IoC Container** cria e **injeta** os beans, você nunca dá `new` nele.

Há duas formas de injetar a dependência no Controller:

**1. Injeção por construtor** (recomendada):

```java
@RestController
@RequestMapping("/hello-world")
public class HelloWorldController {

    private final HelloWorldService helloWorldService;

    public HelloWorldController(HelloWorldService helloWorldService) {
        this.helloWorldService = helloWorldService;
    }

    @GetMapping
    public String helloWorld() {
        return helloWorldService.helloWorld("Kipper");
    }
}
```

**2. Injeção por campo com `@Autowired`** (mais curta e muito comum no código que você vai encontrar):

```java
@Autowired
private HelloWorldService helloWorldService;
```

> 📌 As duas funcionam. A injeção por construtor deixa as dependências explícitas, facilita testes e permite campos `final`.

> 🎥 No vídeo: [31:00](https://youtu.be/YY_hf0FOIcU?t=1860s)

## Classes de Configuração e `@Bean`

O Spring gerencia automaticamente as classes *suas* (anotadas como component/service/controller). Mas às vezes você precisa que ele injete uma classe **externa**, de uma biblioteca de terceiros (ex.: SDK da AWS ou Oracle) ou uma implementação específica de uma interface. Nesses casos, use uma classe **`@Configuration`** com métodos anotados com **`@Bean`**:

```java
@Configuration
public class HelloConfiguration {

    @Bean
    public S3Client s3Client() {
        return S3Client.builder().build();
    }
}
```

O `@Bean` diz ao Spring para gerenciar o **retorno** do método como um bean. Ao escanear a classe `@Configuration`, o Spring monta um mapa: se alguém pedir uma dependência daquele tipo, ele sabe pegar dessa fábrica.

Outro uso clássico é escolher qual implementação de uma interface injetar (ex.: toda vez que pedirem `Transporte`, retornar um `Carro`).

> 📌 **Escopo singleton**: por padrão, o `@Bean` gera uma **única instância** reutilizada por todas as classes que a pedirem. Se você alterar o estado desse objeto em um lugar, todas veem a mesma instância. Esse comportamento pode ser alterado, mas é o padrão.

> 🎥 No vídeo: [36:00](https://youtu.be/YY_hf0FOIcU?t=2160s)

## Recebendo dados no Controller

Esses são os mecanismos mais importantes para construir APIs.

### `@RequestBody`: corpo da requisição

Usado em endpoints `POST` (criação de recursos), onde o cliente envia um JSON no corpo. O Spring injeta e mapeia o corpo para uma classe do seu domínio.

```java
public class User {
    private String name;
    private String email;
    // getters/setters/construtor gerados pelo Lombok
}
```

Com o **Lombok**, dispense o boilerplate:

```java
@Getter
@Setter
@AllArgsConstructor
public class User {
    private String name;
    private String email;
}
```

No controller:

```java
@PostMapping
public String helloWorldPost(@RequestBody User body) {
    return "Hello World " + body.getName();
}
```

### `@PathVariable`: valores na URL

Para extrair um valor do próprio caminho, ex.: `/hello-world/{id}`:

```java
@PostMapping("/{id}")
public String helloWorldPost(@PathVariable String id, @RequestBody User body) {
    return "Hello World " + body.getName() + " " + id;
}
```

### `@RequestParam`: parâmetros de consulta (query params)

Para extrair os valores depois do `?` na URL, ex.: `?filter=video`:

```java
@PostMapping
public String helloWorldPost(@RequestParam(value = "filter", defaultValue = "") String filter,
                             @RequestBody User body) {
    return "Hello World " + filter;
}
```

> 📌 Use `value` para nomear o parâmetro mapeado e `defaultValue` para um valor padrão quando ele não vier. Se chegar um query param com outro nome, o padrão é usado.

> 🎥 No vídeo: [42:00](https://youtu.be/YY_hf0FOIcU?t=2520s)

## Anotações e camadas: visão geral

| Anotação | Papel |
| --- | --- |
| `@RestController` | recebe requisições HTTP e devolve dados (JSON) |
| `@Service` | contém a lógica de negócio |
| `@Repository` | acesso a dados |
| `@Component` | bean genérico gerenciado pelo Spring |
| `@Configuration` | classe que define beans manualmente (`@Bean`) |

A organização em camadas separa responsabilidades:

```
Controller  ->  Service  ->  Repository  ->  Banco de dados
(HTTP)          (regras)     (persistência)
```

## Persistência com Spring Data JPA

> 📌 Este tema é citado no vídeo como próximo passo (parte da formação/outros vídeos), não construído aqui. Fica como referência de fundamentos.

O **JPA** mapeia classes Java para tabelas do banco (ORM). O **Spring Data** elimina o código repetitivo de acesso a dados.

### Entidade

```java
@Entity
@Table(name = "produtos")
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private double preco;

    // getters e setters
}
```

### Repository

Basta estender `JpaRepository` e você ganha `save`, `findById`, `findAll`, `delete` etc. de graça:

```java
public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    // queries derivadas do nome do método
    List<Produto> findByNomeContaining(String nome);
    List<Produto> findByPrecoGreaterThan(double preco);
}
```

### Configuração do banco (`application.properties`)

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/meudb
spring.datasource.username=postgres
spring.datasource.password=senha
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

## DTOs

Evite expor suas entidades diretamente na API. Use **DTOs** (Data Transfer Objects) para controlar o que entra e sai, geralmente como `record`:

```java
public record ProdutoDTO(String nome, double preco) {}
```

## Resumo

- **Spring Boot** abstrai o Spring Framework e configura o essencial automaticamente; comece pelo Spring Initializr (Maven, Spring Web, DevTools, Lombok).
- A classe principal usa **`@SpringBootApplication`** = `@Configuration` + `@EnableAutoConfiguration` + `@ComponentScan`.
- **`@RestController`** (= `@Controller` + `@ResponseBody`) expõe uma API REST *stateless*; `@RequestMapping` + `@GetMapping`/`@PostMapping` mapeiam os endpoints.
- O **IoC Container** cria e injeta os beans; prefira injeção por construtor, mas `@Autowired` também é comum.
- Use **`@Configuration` + `@Bean`** para gerenciar classes externas (escopo singleton por padrão).
- Receba dados com **`@RequestBody`**, **`@PathVariable`** e **`@RequestParam`** (com `value`/`defaultValue`).
- Configure a aplicação no **`application.properties`**, incluindo **profiles** por ambiente.
- Organize em camadas: **Controller → Service → Repository**.

> Para temas além dos fundamentos (tratamento de exceções, Spring Security e migrations), veja o guia **Conhecendo o framework Spring** (`spring-advanced-guide.md`).
