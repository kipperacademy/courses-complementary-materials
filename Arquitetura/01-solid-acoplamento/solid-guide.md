# SOLID: O Princípio de Inversão de Dependência (DIP) e Injeção de Dependência

Este documento detalha o conceito do **D** do SOLID, explica como a **Injeção de Dependência** funciona e demonstra a aplicação prática dentro do ecossistema **NestJS**.

---

## 1. O "D" do SOLID: Dependency Inversion Principle (DIP)

O **Princípio de Inversão de Dependência** afirma que:
1. Módulos de alto nível não devem depender de módulos de baixo nível. Ambos devem depender de **abstrações**.
2. Abstrações não devem depender de detalhes. **Detalhes devem depender de abstrações**.

### O Conceito
Em sistemas mal projetados, a lógica de negócio (alto nível) fica "presa" a detalhes técnicos (banco de dados, bibliotecas de terceiros). Se você mudar a tecnologia, precisa reescrever a regra de negócio. O DIP inverte essa relação: a regra de negócio dita como o "detalhe" deve se comportar através de uma interface.

[Image of dependency inversion principle diagram showing high-level and low-level modules depending on an abstraction interface]

---

## 2. Injeção de Dependência (DI)

Enquanto o DIP é um **princípio arquitetural**, a Injeção de Dependência é um **padrão de projeto (design pattern)** usado para implementar a Inversão de Controle (IoC).

Em vez de uma classe instanciar suas próprias dependências internamente:
* **Sem DI:** A classe `A` cria a classe `B` (`new B()`).
* **Com DI:** Alguém externo (o NestJS, por exemplo) cria a classe `B` e a "entrega" para a classe `A`.

Isso torna o código altamente testável, pois você pode injetar objetos falsos (mocks) durante os testes.

---

## 3. Exemplo Prático no NestJS

No NestJS, usamos classes abstratas ou tokens para representar as abstrações e o decorator `@Injectable()` para permitir que o framework gerencie as instâncias.

### Passo 1: Criar a Abstração (Interface)
Definimos o contrato que o serviço de alto nível espera.

```typescript
// email-provider.abstract.ts
export abstract class EmailProvider {
  abstract sendEmail(to: string, body: string): Promise<void>;
}