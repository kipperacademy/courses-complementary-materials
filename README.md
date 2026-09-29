# 📚 Materiais Complementares dos Cursos

Material de apoio escrito dos cursos e lives da **[Kipper Academy](https://github.com/kipperacademy)**. Cada guia aprofunda, em texto, o conteúdo apresentado em vídeo: conceitos, comandos, exemplos e referências para consulta durante e depois das aulas.

> Este repositório é **material complementar**. Ele acompanha os vídeos e não os substitui — a explicação completa, com contexto e demonstração, está nas aulas.

---

## 📖 Índice de guias

### Frontend

| Guia | O que cobre |
| --- | --- |
| [TypeScript](./typescript-guide.md) | Fundamentos da linguagem: tipos primitivos, inferência, funções, `tsconfig`, instalação de tipos e migração de um projeto JavaScript. |
| [Angular](./angular-guide.md) | Pré-requisitos, Signals e RxJS, `Effects` e organização de um projeto Angular. |
| [Tailwind CSS](./tailwind-guide.md) | Framework utilitário na prática: instalação, anatomia das classes, espaçamentos, cores, tipografia, Flexbox/Grid e estados. Requer conhecimento prévio de CSS. |
| [JavaScript](./js-guide.md) | Guia de estudos de JavaScript, com foco em manipulação da DOM. |

### Java e Backend

| Guia | O que cobre |
| --- | --- |
| [Java — Fundamentos](./java-guide.md) | JVM/JDK/bytecode, sintaxe básica, tipos, condicionais, arrays, casting e Programação Orientada a Objetos. |
| [Java — Intermediário](./java-intermediate-guide.md) | Packages, Collections Framework, Generics, Records, Stream API e Optional. |
| [Java — Tópicos Avançados](./java-advanced-guide.md) | Threads e lambdas, programação paralela, recursos do Java moderno e Garbage Collector. |
| [Java Intensivo (LIVE)](./java-topicos-avancados-se-aprofundando-na-linguagem.md) | Material da live Java Intensivo, com link para o [código base](https://github.com/Fernanda-Kipper/java-language-examples) e comandos de execução. |
| [Spring — Fundamentos](./spring-guide.md) | Spring Framework x Spring Boot, Spring Initializr, estrutura de projeto, Controllers/REST, `application.properties`, Services, `@Bean` e anotações por camada. |
| [Spring — Avançado](./spring-advanced-guide.md) | Tratamento centralizado de exceções, autenticação e autorização com Spring Security e Migrations. Pré-requisito: fundamentos do Spring. |
| [SOLID](./solid-guide.md) | O "D" do SOLID (Dependency Inversion Principle) e Injeção de Dependência, com exemplo prático em NestJS. |

### Infraestrutura e DevOps

| Guia | O que cobre |
| --- | --- |
| [Git](./git-guide.md) | Sistema de controle de versão distribuído: comandos, conceitos e fluxos de trabalho. |
| [Docker](./docker-guide.md) | Containers, benefícios, comandos principais, Docker Hub, networks e inicialização de serviços. |
| [NGINX](./nginx-guide.md) | Servidor web e proxy: instalação, arquivo de configuração, proxy reverso, servidores personalizados e páginas de erro. |
| [Cloud](./cloud-guide.md) | Conceitos de cloud computing, tipos de nuvem, redes, benefícios e o Modelo de Responsabilidade Compartilhada. |

### Arquitetura e Engenharia

| Guia | O que cobre |
| --- | --- |
| [System Design](./system-design.md) | Fundamentos, requisitos funcionais x não funcionais, trade-offs, estudo de caso (*NotiFly*) e caso real da AWS (*Temenos*). |

### IA e Ferramentas

| Guia | O que cobre |
| --- | --- |
| [Claude Code](./claude/main.md) | `CLAUDE.md` e sua hierarquia, modelos, janela de contexto e compactação, Tools, Skills, Commands, Plan x Direct Mode, path-specific rules e múltiplos agentes. |
| [Claude Code na VPS](./claude/vps/main.md) | Como rodar o Claude Code em uma VPS para manter agentes e automações rodando 24/7, sem depender da sua máquina. |

---

## 🗂️ Estrutura do repositório

```
.
├── *-guide.md                 # Guias de cursos em Markdown (raiz)
├── claude/                    # Materiais sobre Claude Code
│   ├── main.md
│   ├── example.md
│   └── vps/
│       └── main.md
├── extra/                     # Imagens e assets usados pelos guias
│   ├── cloud/
│   ├── nginx/
│   └── system-design/
└── word2vec-vs-self-attention-demo/   # Demo executável em Python
```

- **`extra/`** guarda as imagens referenciadas pelos guias (ex.: diagramas de System Design e NGINX).
- **`word2vec-vs-self-attention-demo/`** é um projeto separado, com README próprio — veja abaixo.

---

## ▶️ Demo: Word2Vec vs Self-Attention

O diretório [`word2vec-vs-self-attention-demo/`](./word2vec-vs-self-attention-demo/README.md) contém uma demonstração prática em Python que compara o **Word2Vec** (embeddings fixos, sem contexto) com **Self-Attention** (embeddings contextuais). Acompanha o vídeo *"Por que o Word2Vec não entende contexto? (E como Self-Attention resolveu)"*.

Para rodar:

```bash
cd word2vec-vs-self-attention-demo
python3 -m venv .venv && source .venv/bin/activate
pip install -r requirements.txt
python3 word2vec_demo.py
python3 transformer_demo.py
```

---

## 🚀 Como usar este material

1. **Ler no GitHub** — clique em qualquer guia da tabela acima para abrir direto no navegador.
2. **Clonar o repositório** para ter tudo offline:

   ```bash
   git clone https://github.com/kipperacademy/courses-complementary-materials.git
   ```

3. **Consultar durante o curso** — os guias seguem a ordem dos vídeos e usam marcadores `🎥 No vídeo: [mm:ss]` para indicar o ponto exato da aula.

---

## 🤝 Como contribuir

Contribuições são bem-vindas — correções, melhorias de explicação e novos materiais.

1. Abra uma **issue** descrevendo a contribuição (ou o problema) antes de um PR grande.
2. Faça um **fork** e crie um branch descritivo:

   ```bash
   git switch -c docs/melhora-guia-docker
   ```

3. Siga o padrão existente:
   - Guias de curso na raiz, nomeados `*-guide.md`.
   - Títulos com `#`, seções com `##`.
   - Citações de vídeo no formato `> 🎥 No vídeo: [mm:ss](url)`.
   - Imagens e assets em `extra/<tema>/`.
4. Abra o **pull request** explicando o que mudou e por quê.

---

## 📄 Licença

Este repositório **ainda não possui uma licença definida**. Enquanto isso, o material é © Kipper Academy. Para reuso fora dos cursos, entre em contato com a mantenedora.

---

## 🙏 Créditos

Material produzido e mantido pela **Kipper Academy**:

- [Fernanda Kipper](https://github.com/Fernanda-Kipper)
- [KipperDev](https://github.com/KipperDev)

E por colaboradores da comunidade que contribuíram com guias e demos ao longo do tempo.
