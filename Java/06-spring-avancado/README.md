# Conhecendo o framework Spring | Exceções, Spring Security e Migrations

Este guia aprofunda três temas essenciais para uma API Spring pronta para produção: tratamento centralizado de exceções, autenticação e autorização com Spring Security, e controle de versão do banco de dados com migrations.

> Pré-requisito: os fundamentos do framework (IoC, camadas, JPA). Veja `spring-guide.md`.

## 1. Tratamento de exceções

> 🎥 No vídeo: [00:00](https://youtu.be/GmbK-O3v3Gg?t=0s)

Quando não tratamos uma exceção, o Spring devolve uma resposta padrão com toda a **stack trace** no corpo. Além de pouco amigável, isso é uma **falha de segurança**: a stack trace expõe detalhes internos da aplicação que um usuário mal-intencionado pode usar para explorar outras falhas. Por isso queremos capturar as exceções em um ponto central e devolver respostas HTTP claras e consistentes.

A alternativa ingênua é tratar cada exceção com `try/catch` nos controllers, mas isso duplica código por todos os endpoints (imagine 20-30 controllers em um monolito). A solução elegante é centralizar.

### Exceções personalizadas (Custom Exceptions)

Crie exceções específicas do seu domínio estendendo `RuntimeException`. Assim a classe se comporta como uma exceção nativa do Java, mas com nome e mensagem próprios, o que facilita o debug (você sabe exatamente qual erro foi lançado) e a construção de mensagens amigáveis:

> 🎥 No vídeo: [05:00](https://youtu.be/GmbK-O3v3Gg?t=300s)

```java
public class ProdutoNaoEncontradoException extends RuntimeException {
    public ProdutoNaoEncontradoException() {
        super("Produto não encontrado");
    }

    public ProdutoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
```

E lance no service quando fizer sentido:

```java
public Produto buscarPorId(Long id) {
    return repository.findById(id)
        .orElseThrow(ProdutoNaoEncontradoException::new);
}
```

### Handler global com @ControllerAdvice

A peça central é o **Controller Advice**. Anotando uma classe com `@ControllerAdvice` (e estendendo `ResponseEntityExceptionHandler`), o Spring passa a direcionar para ela qualquer exceção lançada e não capturada nos controllers. Cada método anotado com `@ExceptionHandler` trata um tipo de exceção e devolve a resposta adequada. Uma boa prática é colocar essa classe em um pacote `infra`:

> 🎥 No vídeo: [07:30](https://youtu.be/GmbK-O3v3Gg?t=450s)

```java
@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ProdutoNaoEncontradoException.class)
    public ResponseEntity<RestErrorMessage> handleNaoEncontrado(ProdutoNaoEncontradoException ex) {
        var erro = new RestErrorMessage(HttpStatus.NOT_FOUND, ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<RestErrorMessage> handleRuntime(RuntimeException ex) {
        var erro = new RestErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(erro);
    }
}
```

### Padronizando o corpo do erro

Em vez de devolver uma `String` solta, crie uma classe para padronizar a resposta de erro em toda a API. A instrutora usa anotações do **Lombok** (`@AllArgsConstructor`, `@Getter`/`@Setter`) para evitar boilerplate:

> 🎥 No vídeo: [13:30](https://youtu.be/GmbK-O3v3Gg?t=810s)

```java
@Getter
@Setter
@AllArgsConstructor
public class RestErrorMessage {
    private HttpStatus status;
    private String message;
}
```

> 📌 Vantagem: os controllers ficam limpos (sem `try/catch` por toda parte) e a API responde de forma padronizada. Você pode tratar tanto exceções específicas do seu domínio quanto genéricas (ex.: `RuntimeException`) na mesma classe.

## 2. Spring Security (Autenticação e Autorização)

> 🎥 No vídeo: [00:00](https://youtu.be/5w-YCcOjPD0?t=0s)

- **Autenticação:** *quem é você?* (login, validar credenciais).
- **Autorização:** *o que você pode fazer?* (permissões, papéis/roles).

### Conceitos

O Spring Security intercepta as requisições através de uma **cadeia de filtros** (*Security Filter Chain*). Um fluxo comum em APIs REST usa **JWT (JSON Web Token)**: o usuário faz login uma vez, recebe um token assinado, e o envia no header `Authorization: Bearer <token>` nas próximas requisições.

```
Login (usuário/senha) -> servidor valida -> gera JWT -> cliente guarda
Requisições seguintes -> envia o JWT -> filtro valida -> libera o acesso
```

> ⚠️ Assim que você adiciona a dependência `spring-boot-starter-security`, o Spring **bloqueia tudo por padrão** e cria uma tela de login com um usuário `user` e uma senha aleatória logada no console a cada inicialização. O objetivo é justamente **substituir** essa configuração padrão pela nossa, stateless com JWT.

### Stateful x Stateless

> 🎥 No vídeo: [26:30](https://youtu.be/5w-YCcOjPD0?t=1590s)

- **Stateful:** o servidor armazena a sessão de cada usuário logado.
- **Stateless:** o servidor **não guarda sessão**. Toda a identidade viaja dentro do token, que o cliente reenvia a cada requisição. É o padrão das aplicações web modernas e o que usaremos aqui.

### Entidade de usuário (UserDetails)

> 🎥 No vídeo: [11:30](https://youtu.be/5w-YCcOjPD0?t=690s)

A entidade que representa o usuário deve implementar a interface `UserDetails` do Spring Security. No `getAuthorities()` mapeamos a *role* do nosso domínio para as authorities que o Spring entende (`SimpleGrantedAuthority`). Note que um `ADMIN` normalmente acumula também as permissões de `USER`:

```java
@Entity
@Table(name = "users") // no PostgreSQL não use "user" (é tabela reservada)
public class Usuario implements UserDetails {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String login;
    private String password;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (role == UserRole.ADMIN) {
            return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"),
                           new SimpleGrantedAuthority("ROLE_USER"));
        }
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getUsername() { return login; }
    // demais métodos de UserDetails retornam true (sem expiração/bloqueio neste exemplo)
}
```

> ⚠️ No PostgreSQL a tabela **não pode** se chamar `user` (nome reservado). Use `users`.

### Carregando o usuário (UserDetailsService)

> 🎥 No vídeo: [21:00](https://youtu.be/5w-YCcOjPD0?t=1260s)

O Spring Security chama `loadUserByUsername` automaticamente para buscar o usuário. Implementamos esse contrato e delegamos ao repositório:

```java
@Service
public class AuthorizationService implements UserDetailsService {

    @Autowired
    private UserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) {
        return repository.findByLogin(username);
    }
}
```

### Configuração da cadeia de segurança

> 🎥 No vídeo: [24:30](https://youtu.be/5w-YCcOjPD0?t=1470s)

Anotamos uma classe de configuração com `@Configuration` e `@EnableWebSecurity` para desabilitar o padrão e declarar nossas próprias regras. Definimos a política de sessão como `STATELESS` e usamos `requestMatchers` para liberar o login/registro e proteger o resto. O endpoint de criação de produto fica restrito a `ADMIN`:

```java
@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                .requestMatchers(HttpMethod.POST, "/auth/register").permitAll()
                .requestMatchers(HttpMethod.POST, "/produtos").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // nunca guarde senha em texto puro
    }
}
```

### Endpoint de login e registro

> 🎥 No vídeo: [35:30](https://youtu.be/5w-YCcOjPD0?t=2130s)

No login, montamos um `UsernamePasswordAuthenticationToken` e o entregamos ao `AuthenticationManager`, que valida as credenciais (comparando o hash). No registro, a senha é **criptografada com BCrypt antes de salvar**, nunca guardamos a senha em texto puro:

```java
@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    @Autowired private AuthenticationManager authenticationManager;
    @Autowired private UserRepository repository;
    @Autowired private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid AuthenticationDTO dto) {
        var authToken = new UsernamePasswordAuthenticationToken(dto.login(), dto.password());
        var auth = authenticationManager.authenticate(authToken);
        // gerar e retornar o JWT (TokenService) para o usuário autenticado
        return ResponseEntity.ok().build();
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid RegisterDTO dto) {
        if (repository.findByLogin(dto.login()) != null) return ResponseEntity.badRequest().build();
        var hash = passwordEncoder.encode(dto.password());
        repository.save(new Usuario(dto.login(), hash, dto.role()));
        return ResponseEntity.ok().build();
    }
}
```

### Geração do token (JWT)

> 🎥 No vídeo: [52:30](https://youtu.be/5w-YCcOjPD0?t=3150s)

A instrutora usa a biblioteca **`java-jwt` da Auth0** com o algoritmo **HMAC256**. O token guarda o `subject` (login do usuário) e uma expiração. A `secret` que assina o token **nunca** fica hard-coded: vem de uma variável de ambiente via `@Value`:

```java
@Service
public class TokenService {

    @Value("${api.security.token.secret}")
    private String secret;

    public String generateToken(Usuario usuario) {
        try {
            var algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                .withIssuer("minha-api")
                .withSubject(usuario.getLogin())
                .withExpiresAt(/* agora + 2h */)
                .sign(algorithm);
        } catch (JWTCreationException ex) {
            throw new RuntimeException("Erro ao gerar token", ex);
        }
    }
}
```

### Pontos importantes

- **Nunca** armazene senhas em texto puro, use um hash forte (`BCryptPasswordEncoder`). O mesmo algoritmo gera sempre o mesmo hash para a mesma entrada, por isso comparamos hashes no login.
- A entidade de usuário implementa `UserDetails`; o `UserDetailsService` (`loadUserByUsername`) ensina o Spring a buscar usuários no nosso banco.
- APIs REST normalmente são **stateless** (`SessionCreationPolicy.STATELESS`), sem sessão no servidor; o JWT carrega a identidade.
- **Roles/Authorities** controlam a autorização (`hasRole("ADMIN")`, `hasAuthority("LEITURA")`).
- Para emitir e validar JWTs, a biblioteca usada na aula é a `java-jwt` (Auth0), com HMAC256 e a `secret` em variável de ambiente.
- O primeiro usuário `ADMIN` costuma ser criado **direto no banco**; a partir dele se cadastram os demais.

## 3. Migrations

> 🎥 No vídeo: [00:30](https://youtu.be/LX5jaieOIAk?t=30s)

**Migrations** versionam o schema do banco de dados de forma controlada e rastreável. Conforme a aplicação evolui (nova tabela, nova coluna, mudança de tipo), o banco precisa acompanhar, e queremos guardar o histórico dessas mudanças. Em vez de alterar tabelas na mão pelo terminal, descrevemos cada mudança em um arquivo versionado que roda automaticamente. A ferramenta usada na aula é o **Flyway** (com PostgreSQL); outra alternativa popular é o Liquibase.

### Flyway

Adicione a dependência do Flyway pelo Spring Initializr (o driver do Postgres já costuma estar no projeto). Crie os arquivos SQL em `src/main/resources/db/migration`, seguindo a convenção de nome:

```
V<VERSÃO>__<descrição>.sql
```

Exemplos:

```
V1__create_table_produtos.sql
V2__add_column_ativo.sql
V3__create_table_pedidos.sql
```

Conteúdo de uma migration (SQL puro):

```sql
-- V1__create_table_produtos.sql
CREATE TABLE produtos (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    preco NUMERIC(10,2) NOT NULL
);
```

```sql
-- V2__add_column_ativo.sql
ALTER TABLE produtos ADD COLUMN ativo BOOLEAN DEFAULT TRUE;
```

### Como funciona

> 🎥 No vídeo: [09:30](https://youtu.be/LX5jaieOIAk?t=570s)

- Na inicialização, o Flyway verifica quais migrations já foram aplicadas (ele guarda isso em uma tabela de controle que ele mesmo cria, a `flyway_schema_history`). **Nunca exclua essa tabela**, é o histórico que ele usa para saber o que já rodou.
- Aplica, **em ordem de versão**, apenas as que ainda não rodaram.
- Migrations já aplicadas **nunca devem ser editadas**; para corrigir, crie uma nova versão (`V2`, `V3`, ...).

> ⚠️ **Pare a aplicação antes de escrever uma nova migration.** O Flyway tenta executar qualquer arquivo novo que detectar; se você salvar uma migration incompleta com a aplicação rodando, ela roda pela metade e fica difícil de corrigir. Escreva o arquivo inteiro e só então reinicie a aplicação.

> ⚠️ Com migrations, desligue o `ddl-auto=update` do Hibernate em produção (`spring.jpa.hibernate.ddl-auto=validate`). Quem manda no schema passam a ser as migrations, não o Hibernate.

## Resumo

- **Exceções:** crie exceções de domínio (`extends RuntimeException`) e centralize o tratamento com `@ControllerAdvice`, padronizando o corpo do erro e evitando expor a stack trace.
- **Spring Security:** autenticação (quem é) x autorização (o que pode); entidade implementa `UserDetails`, `UserDetailsService` busca o usuário; em APIs REST, padrão **JWT + stateless** (java-jwt da Auth0), senhas sempre com hash (`BCryptPasswordEncoder`), `secret` em variável de ambiente.
- **Migrations:** versione o schema com Flyway (`V1__...sql` em `db/migration`), pare a aplicação antes de escrever a migration, nunca edite uma migration já aplicada nem exclua a `flyway_schema_history`, e deixe `ddl-auto=validate` em produção.
