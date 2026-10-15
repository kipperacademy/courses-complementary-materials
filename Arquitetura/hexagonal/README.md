# Matrículas — Arquitetura Hexagonal

Versão independente em Java 17+ da arquitetura Ports & Adapters de Alistair Cockburn. O núcleo da aplicação define **ports** conforme as conversas que precisa oferecer ou solicitar. **Primary adapters** acionam a aplicação; **secondary adapters** são acionados por ela para falar com pagamentos ou persistência. A persistência usa JPA com Hibernate e H2 em arquivo, sem Spring. As entidades JPA ficam no lado secondary.

## Ideia da arquitetura

No artigo original, Cockburn descreve uma aplicação que pode funcionar sem depender de uma interface de usuário ou de um banco específico. Uma **port** representa uma conversa com um propósito; um **adapter** traduz uma tecnologia concreta para essa conversa. Assim, a aplicação pode ser acionada por diferentes atores e pode conversar com implementações substituíveis de serviços externos.

### O que significam primary e secondary?

Esses nomes parecem técnicos, mas descrevem uma coisa simples: **quem começa a conversa com a aplicação**.

- **Port (porta)** é um contrato: diz que conversa a aplicação oferece ou de que conversa ela precisa. Por exemplo, `EnrollStudent` diz como pedir uma matrícula; `PaymentProvider` diz como pedir uma cobrança. O contrato usa os termos do negócio, sem exigir HTTP, JPA ou AppMax.
- **Adapter (adaptador)** traduz entre uma tecnologia concreta e o contrato. `EnrollmentHttpAdapter`, por exemplo, transforma HTTP/JSON em uma chamada compreendida pela aplicação.
- **Primary (primário)** é o lado de onde alguém inicia uma ação para usar a aplicação. A pessoa que envia um `POST` ou digita no console é o ator; `EnrollmentHttpAdapter` e `PixConsoleAdapter` são primary adapters. Eles chamam primary ports como `EnrollStudent` ou `CreatePixEnrollment`.
- **Secondary (secundário)** é o lado de que a aplicação precisa para completar uma ação. Durante um caso de uso, a aplicação chama ports como `PaymentProvider` ou `EnrollmentRepository`; os adapters `AppMaxPaymentAdapter` e `JpaEnrollmentRepository` atendem a esses contratos.

Uma forma de lembrar: no lado **primary**, o ator dirige a aplicação; no lado **secondary**, a aplicação dirige a conversa com um serviço de que precisa. “Primário” e “secundário” não significam mais importante/menos importante. Também não são sinônimos de entrada/saída definidos pela direção dos dados. O critério de Cockburn é quem inicia a conversa.

#### Um fluxo deste projeto

Quando alguém cria uma matrícula pela API, a sequência é:

```text
cliente HTTP
  -> EnrollmentHttpAdapter (primary adapter: traduz HTTP/JSON)
  -> EnrollStudent (primary port: contrato para pedir matrícula)
  -> EnrollStudentService (caso de uso)
  -> PaymentProvider (secondary port: contrato de pagamento)
  -> AppMaxPaymentAdapter (secondary adapter: conversa com AppMax)
  -> EnrollmentRepository (secondary port: contrato de persistência)
  -> JpaEnrollmentRepository (secondary adapter: conversa com JPA/H2)
```

O `Main` instancia os componentes concretos e os conecta. No fluxo Pix pelo terminal, `PixConsoleAdapter` faz o papel de primary adapter e chama `CreatePixEnrollment` ou `CheckPixEnrollment`. Assim, o terminal também entra na aplicação por uma port.

Neste projeto, `EnrollStudent`, `ManageEnrollments`, `CreatePixEnrollment` e `CheckPixEnrollment` são primary ports implementadas pelos casos de uso. O console e a API HTTP são primary adapters. `PaymentProvider`, `PixPaymentProvider` e `EnrollmentRepository` são secondary ports; adapters AppMax e JPA/Hibernate implementam esses contratos. `Main` conecta uma combinação concreta. Os nomes das pastas refletem os termos primary/secondary; são uma organização deste projeto, não uma árvore de diretórios obrigatória definida por Cockburn.

## Estrutura do projeto

```mermaid
flowchart TD
    root["hexagonal/"] --> pom["pom.xml"]
    root --> run["executar.sh"]
    root --> java["src/main/java/br/com/kipperdev/hexagonal/"]
    java --> domain["domain/<br/>Enrollment / PaymentStatus"]
    java --> application["application/"]
    application --> ports["ports/"]
    ports --> primary["primary/<br/>EnrollStudent / ManageEnrollments /<br/>CreatePixEnrollment / CheckPixEnrollment"]
    ports --> secondary["secondary/<br/>PaymentProvider / PixPaymentProvider /<br/>EnrollmentRepository"]
    application --> usecases["usecases/<br/>*Service"]
    java --> adapters["adapters/"]
    adapters --> primaryAdapters["primary/<br/>ConsoleEnrollmentAdapter / PixConsoleAdapter /<br/>EnrollmentHttpAdapter"]
    adapters --> secondaryAdapters["secondary/<br/>AppMax / JpaEnrollmentRepository"]
    secondaryAdapters --> persistence["persistence/"]
    java --> main["Main.java"]
    root --> resources["src/main/resources/META-INF/persistence.xml"]
```

## Organização por ports e adapters

As setas contínuas indicam chamadas em execução; as setas tracejadas indicam dependência/implementação de contrato. O núcleo contém as ports e os casos de uso; adapters concretos dependem dessas ports para traduzir console, AppMax e JPA/H2.

```mermaid
flowchart LR
    actor["Pessoa / CLI"] --> console["Primary adapters<br/>ConsoleEnrollmentAdapter / PixConsoleAdapter"]
    httpClient["Cliente HTTP"] --> http["Primary adapter<br/>EnrollmentHttpAdapter"]
    console --> primaryPort["Primary ports<br/>EnrollStudent / CreatePixEnrollment / CheckPixEnrollment"]
    http --> managePort["Primary port<br/>ManageEnrollments"]
    service["Use case<br/>EnrollStudentService"] -. "implementa" .-> primaryPort
    manageService["Use case<br/>ManageEnrollmentsService"] -. "implementa" .-> managePort
    service --> domain["Domain<br/>Enrollment / PaymentStatus"]
    service --> paymentPort["Secondary port<br/>PaymentProvider"]
    service --> repoPort["Secondary port<br/>EnrollmentRepository"]
    appmax["Secondary adapter<br/>AppMax"] -. "implementa" .-> paymentPort
    jpa["Secondary adapter<br/>JPA/Hibernate + H2"] -. "implementa" .-> repoPort
    main["Main<br/>composição"] --> console
    main --> http
    main --> service
    main --> manageService
    main --> appmax
    main --> jpa
```

Os comandos `pix-create` e `pix-check` usam `PixConsoleAdapter`, que chama `CreatePixEnrollment` e `CheckPixEnrollment`. `Main` permanece responsável por montar os casos de uso, o adapter de console, o adapter AppMax e o repositório JPA.

```sh
# A partir da pasta `Arquitetura/`
cd hexagonal
./executar.sh api
```

A execução requer JDK 17 ou superior e Maven 3.9 ou superior. `api` é o modo padrão e pode ser omitido (`./executar.sh`). Ele inicia a API HTTP em `127.0.0.1:8080`, usa o simulador local e persiste com JPA/Hibernate em H2. O script compila e inicia a aplicação com `mvn compile exec:java`. Para executar a demonstração pelo console, rode `./executar.sh demo`.

## API CRUD de matrículas

A API recebe JSON em `http://127.0.0.1:8080/api/enrollments`; configure outra porta com `API_PORT`. A criação passa pela primary port `EnrollStudent` e só grava a matrícula quando o pagamento está confirmado. Uma criação pendente retorna `202` e ainda não aparece nas consultas. No simulador, novos IDs são confirmados por padrão; `enr-bia` fica pendente e `enr-clara` é recusada.

| Método | Rota | Operação |
|---|---|---|
| `GET` | `/api/enrollments` | Lista matrículas confirmadas |
| `GET` | `/api/enrollments/{id}` | Busca uma matrícula |
| `POST` | `/api/enrollments` | Cobra e cria matrícula se o pagamento for confirmado |
| `PUT` | `/api/enrollments/{id}` | Atualiza estudante e curso |
| `DELETE` | `/api/enrollments/{id}` | Remove uma matrícula |

Na atualização, o `id` e o valor cobrado permanecem imutáveis para preservar a relação com o pagamento original. A API local fica vinculada ao loopback e não deve ser exposta diretamente à internet.

```sh
curl http://127.0.0.1:8080/api/enrollments
curl -i -X POST http://127.0.0.1:8080/api/enrollments \
  -H 'Content-Type: application/json' \
  -d '{"id":"enr-joana","student":"Joana","course":"Arquitetura","amountInCents":10000}'
curl -i http://127.0.0.1:8080/api/enrollments/enr-joana
curl -i -X PUT http://127.0.0.1:8080/api/enrollments/enr-joana \
  -H 'Content-Type: application/json' \
  -d '{"student":"Joana Silva","course":"Hexagonal"}'
curl -i -X DELETE http://127.0.0.1:8080/api/enrollments/enr-joana
```

Outros comandos (no diretório `hexagonal/`):

```sh
./executar.sh pix-create
./executar.sh pix-check <id-interno-da-matricula>
```

Por padrão, o banco H2 em arquivo fica em `data/enrollments.mv.db` (relativo a esta pasta). Para escolher outro URL JDBC H2, configure `ENROLLMENT_DB_URL`, por exemplo `ENROLLMENT_DB_URL='jdbc:h2:file:/tmp/turma;DB_CLOSE_ON_EXIT=FALSE' ./executar.sh demo`.

A demo (`./executar.sh demo`) usa os mesmos pedidos e respostas simuladas da versão Clean: Ana confirmada, Bia pendente e Clara recusada. Só Ana recebe matrícula. Rodar novamente atualiza a matrícula de Ana pela chave estável `enr-ana`; Bia e Clara não são gravadas.

## Pastas

- `domain/`: conceitos e regra de matrícula no interior da aplicação.
- `application/ports/primary/`: conversas primárias pelas quais atores acionam a aplicação.
- `application/ports/secondary/`: conversas secundárias que a aplicação inicia com atores externos.
- `application/usecases/`: execução dos casos de uso no interior da aplicação.
- `adapters/primary/`: adapters que traduzem as entradas de atores para as primary ports.
- `adapters/primary/PixConsoleAdapter`: traduz os comandos Pix do terminal para as primary ports correspondentes.
- `adapters/secondary/`: adapters que traduzem as secondary ports para tecnologias externas.
- `adapters/secondary/persistence/`: entidades JPA, fora do domínio e dos casos de uso.
- `Main.java`: composição explícita dos adapters e do núcleo.
- `src/main/resources/META-INF/persistence.xml`: unidade JPA e entidades persistidas.
- `pom.xml`: Hibernate, H2 e configuração de build/execução.
- `AppMaxPixPaymentAdapter`: adapter HTTP de produção/sandbox, limitado a Pix.

## Persistência e pagamento

`JpaEnrollmentRepository` implementa a secondary port `EnrollmentRepository`; tipos JPA não chegam ao núcleo. O estado do pagamento e a matrícula são confirmados na mesma transação JPA. A configuração H2 usa `hibernate.hbm2ddl.auto=update` para a demo; não é uma estratégia de migração de produção.

### Pix real AppMax

O modo `demo` continua usando o simulador local. O caminho real de pagamento é explícito via `pix-create` e aceita somente Pix; não há campos de cartão nem tokenização de cartão.

Configure as credenciais **do merchant** (não as credenciais do app) no ambiente, sem gravá-las no projeto:

```sh
export APP_MAX_CLIENT_ID='...'
export APP_MAX_CLIENT_SECRET='...'
# Opcional: o padrão é sandbox; use os dois domínios oficiais de produção juntos ao operar em produção.
export APP_MAX_API_BASE_URL='https://api.sandboxappmax.com.br'
export APP_MAX_AUTH_BASE_URL='https://auth.sandboxappmax.com.br'
./executar.sh pix-create
```

O comando solicita os dados do comprador, os dados do estudante/curso e o preço em centavos. Nome, sobrenome, email, telefone e IP são enviados ao endpoint de cliente; o IP deve ser coletado no checkout/Appmax JS. CPF/CNPJ é enviado no cliente e no pagamento Pix. O adapter cria cliente, pedido digital e pagamento Pix, então salva o pedido AppMax, estado e instruções EMV/QR no H2 como pendente. O exemplo precisa de dados completos do comprador e não persiste email/telefone/documento/IP localmente.

Depois que o Pix for pago, consulte o estado pela API autenticada e promova a matrícula:

```sh
./executar.sh pix-check <id-interno-da-matricula>
```

Cada consulta chama `GET /v1/orders/{order_id}`. O serviço só grava matrícula quando a resposta autenticada retorna `aprovado` ou `integrado` **e** `total_paid` corresponde exatamente ao valor persistido em centavos; status sem valor pago ou com valor divergente não libera acesso. `pendente`, `pendente_integracao` e estados desconhecidos também não liberam acesso. A atualização do pagamento e a criação da matrícula ocorrem na mesma transação JPA; repetir a confirmação não duplica a matrícula.

O projeto não usa webhook para confirmar. A documentação pública descreve os eventos e payloads, mas não documenta assinatura HMAC ou autenticação que prove a origem do POST; portanto, um payload recebido não serve como autoridade para liberar matrícula. Um webhook futuro pode apenas disparar a consulta autenticada do pedido.

## Limite da demo

`AppMaxApiSimulator` não chama a API real. Os rótulos de demonstração `paid`, `pending` e `refused` são inventados para mostrar como o adaptador traduz respostas externas para `CONFIRMED`, `PENDING` e `DECLINED`; eles não pretendem reproduzir os estados oficiais da AppMax. O adapter HTTP Pix usa OAuth merchant e os endpoints reais descritos na documentação oficial. Não envie dados de cartão a esta aplicação.

## Fontes

- Alistair Cockburn, [Hexagonal Architecture / Ports and Adapters](https://alistair.cockburn.us/hexagonal-architecture).
- Martin Fowler e Badri Janakiraman, [Badri on Hexagonal Rails](https://martinfowler.com/articles/badri-hexagonal/).
- Appmax, [autenticação](https://docs.appmax.com.br/guides/autenticacao), [exemplo completo (Pix e GET do pedido)](https://docs.appmax.com.br/guides/exemplo-integracao), [status dos pedidos](https://docs.appmax.com.br/guides/status-pedidos) e [webhooks](https://docs.appmax.com.br/guides/webhooks).
- Aula 03 no repositório do curso: `../03-clean-architecture-hexagonal/README.md`.
