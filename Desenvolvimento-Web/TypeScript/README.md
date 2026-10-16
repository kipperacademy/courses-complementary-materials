# Fundamentos de TypeScript

TypeScript é o que a gente chama de **superset** da linguagem JavaScript: ele estende o JavaScript adicionando **tipos**. Por isso o nome "type" + "script". Antes de colocar a mão no código, o mais importante é entender a relação entre os dois.

## JavaScript x TypeScript

> 🎥 No vídeo: [00:50](https://youtu.be/QoqDr4H2G8U?t=50s)

O JavaScript é uma linguagem de **tipagem dinâmica em tempo de execução**: o tipo de uma variável pode mudar enquanto o programa roda. Você declara uma variável com uma string e, mais adiante, guarda um número, um array ou um objeto nela.

```javascript
let dado = "Fernanda"; // string
dado = 20;             // agora number, permitido em JS
dado = [1, 2, 3];      // agora array, permitido em JS
```

Isso torna o JS muito flexível, mas também abre espaço para bugs difíceis de achar: somar uma string com um array, dividir uma string por um número, etc. Esses erros só aparecem **em tempo de execução**, quando o usuário já está usando a aplicação.

O TypeScript resolve isso com **tipagem estática**: os tipos são verificados em **tempo de compilação**. Quem faz essa verificação é o **compilador TSC** (o compilador do TypeScript). Ele checa o tipo de cada variável, o tipo de retorno de cada função, e aponta os erros **antes** de a aplicação iniciar (por exemplo, ao rodar `npm run build`).

Os erros típicos que o TSC pega:
- **Type mismatch:** usar uma variável esperando um tipo, mas ela é de outro.
- **Operações inválidas para o tipo:** dividir uma string por dois, multiplicar um array, etc.

Por padrão, **as variáveis no TypeScript permanecem com um único tipo**. Se você declarou uma string, não pode depois guardar um número ali, nem passá-la para uma função que espera um número.

> 📌 No final, **todo código TypeScript vira código JavaScript** depois de compilado, porque é o JS que roda no browser e na engine do Node. Como a tipagem é **opcional**, **todo código JavaScript também é um TypeScript válido**, por isso arquivos `.js` e `.ts` convivem no mesmo projeto.

## Por que usar TypeScript?

> 🎥 No vídeo: [07:05](https://youtu.be/QoqDr4H2G8U?t=425s)

- **Capturar erros antecipadamente:** identifica problemas durante o desenvolvimento e o build, evitando que ocorram em tempo de execução.
- **Aumentar a produtividade:** com o retorno das funções, os parâmetros de entrada, o formato dos objetos e o tipo das variáveis todos mapeados, fica muito mais fácil pegar um projeto em andamento e entender que dados estão sendo transacionados.
- **Autocomplete e documentação:** ao tipar, o editor sabe quais funções e propriedades existem em cada objeto e te sugere as corretas.

## Instalando o TypeScript num projeto

> 🎥 No vídeo: [10:25](https://youtu.be/QoqDr4H2G8U?t=625s), partindo de um projeto JavaScript já existente (servidor Express).

O TypeScript é instalado como qualquer outra biblioteca do NPM, mas como **dependência de desenvolvimento** (`--save-dev`):

```bash
npm install --save-dev typescript
```

Por que `--save-dev`? Porque o TypeScript só precisa existir no ambiente de desenvolvimento. Quando a aplicação é buildada para produção, o compilador já transformou tudo em arquivos `.js`, então o TypeScript não precisa ir no pacote final. Não é obrigatório, mas é boa prática.

## Configurando o tsconfig.json

> 🎥 No vídeo: [12:30](https://youtu.be/QoqDr4H2G8U?t=750s)

Depois de instalar, é preciso configurar. Rodando:

```bash
npx tsc --init
```

o compilador cria o arquivo `tsconfig.json`, um JSON com as configurações que o TSC olha antes de compilar. As opções ficam dentro de `compilerOptions`. As principais que a Fernanda destaca:

```jsonc
{
  "compilerOptions": {
    "module": "NodeNext",   // sistema de módulos (CommonJS, ES6, NodeNext...), deve casar com o projeto
    "target": "ESNext",     // para qual versão do JS o código será transpilado (ES2020, ES2018...)
    "outDir": "./dist",     // diretório onde o JavaScript gerado (output) será salvo
    "rootDir": "./src",     // pasta que o compilador deve observar
    "strict": true,         // verificações mais rigorosas (recomendado em desenvolvimento)
    "esModuleInterop": true // interoperabilidade com módulos/bibliotecas de terceiros
    // "jsx": "..."         // usado em projetos frontend com arquivos .tsx, não precisa no backend
  },
  "exclude": ["node_modules"] // pastas ignoradas, fica FORA de compilerOptions, no nível raiz
}
```

Pontos importantes da aula:
- A transpilação é basicamente uma **tradução** do TypeScript para JavaScript. `target` diz para qual versão do JS traduzir.
- `exclude` não é uma `compilerOption`, ela fica no nível raiz do JSON. Serve para o compilador ignorar pastas (assets, `node_modules`, etc.). Frameworks como Angular e NestJS já trazem o `tsconfig.json` configurado por padrão.
- O modo `strict` deixa as checagens mais rigorosas; desligá-lo torna tudo mais flexível.

## Instalando os tipos (@types)

> 🎥 No vídeo: [18:20](https://youtu.be/QoqDr4H2G8U?t=1100s)

Como reutilizamos muitas bibliotecas do mundo JavaScript dentro do TypeScript, precisamos dos **tipos** dessas bibliotecas. O padrão da comunidade é instalá-los via pacotes `@types`:

```bash
npm install --save-dev @types/node     # tipos das funções nativas do Node
npm install --save-dev @types/express  # tipos do Express
```

> 📌 Sempre que der um problema de tipo com uma biblioteca, tente instalar o `@types` dela, provavelmente os tipos já estão mapeados e o erro some.

## Tipos primitivos

> 🎥 No vídeo: [20:25](https://youtu.be/QoqDr4H2G8U?t=1225s)

São os tipos que já existem por padrão no TypeScript. Para tipar uma variável, declare-a (com `let`, `const` ou `var`) e use **dois pontos** seguidos do tipo:

```typescript
const minhaIdade: number = 20;     // number: valores numéricos
const nome: string = "Fernanda";   // string: dados textuais (nome, email, CPF formatado...)
const ativo: boolean = true;       // boolean: true / false
```

O tipo `void` representa "vazio", usado no retorno de funções que **não retornam nada**, só executam uma operação (por exemplo, uma função que só faz `console.log`).

## Inferência de tipos

> 🎥 No vídeo: [22:05](https://youtu.be/QoqDr4H2G8U?t=1325s)

Você nem sempre precisa anotar o tipo: o compilador **infere** ("adivinha") o tipo a partir do valor disponível. Como, por padrão, o valor de uma variável não muda de tipo, o TS já assume o tipo do valor atual:

```typescript
const cpf = 11111111111; // sem ": tipo", o TS infere number
// cpf = "111.111.111-11"; // ❌ Type 'string' is not assignable to type 'number'
```

Mesmo sem declarar o tipo explicitamente, o TypeScript protege contra a troca de tipo. A inferência **não funciona sempre**: ao lidar com tipos complexos (classes, interfaces, objetos próprios), em muitos casos o compilador não consegue inferir e você precisa anotar o tipo explicitamente.

## Funções

> 🎥 No vídeo: [23:45](https://youtu.be/QoqDr4H2G8U?t=1425s)

No JavaScript, uma função não declara o tipo dos parâmetros nem do retorno, e o TS reclama que o parâmetro "implicitly has an `any` type". Para tipar, use dois pontos na frente de cada parâmetro e, opcionalmente, no retorno:

```typescript
function somar(a: number, b: number): number {
  return a + b;
}
```

O tipo de retorno pode ser inferido: como `a` e `b` são `number`, o TS sabe que a função retorna `number` sem você declarar. Mas você pode escrevê-lo explicitamente (`: number`) quando quiser deixar claro.

> 📌 `any` significa "qualquer coisa", aceita qualquer tipo e desliga a checagem. O TS avisa quando um parâmetro fica com `any` implícito porque, usando TypeScript, o ideal é dar um tipo de verdade.

## Union types (união de tipos)

> 🎥 No vídeo: [26:15](https://youtu.be/QoqDr4H2G8U?t=1575s)

Há momentos em que uma variável pode ser de **mais de um tipo**. Para isso usamos o `|` (union type). Exemplo clássico: um CPF que pode vir como número (só dígitos) ou como string (com máscara `111.111.111-11`):

```typescript
let cpf: string | number = 11111111111;
cpf = "111.111.111-11"; // ✅ agora os dois tipos são aceitos
```

Você pode unir quantos tipos quiser (`string | number | boolean | null | Pessoa`), mas quanto mais tipos, mais confuso fica ("vira um carnaval").

Um uso muito comum é **enumerar strings**: limitar uma variável a um conjunto de valores pré-definidos.

```typescript
let cargo: "gerente" | "supervisor" | "estagiario";
cargo = "gerente";   // ✅
// cargo = "diretor"; // ❌ 'diretor' não é atribuível a esse tipo
```

> 📌 Existe também o `enum` para criar enumerações, mas aqui usamos union types de strings literais para o mesmo efeito: valores pré-definidos que a variável pode assumir.

## Integrando TypeScript numa aplicação Express

> 🎥 No vídeo: [29:35](https://youtu.be/QoqDr4H2G8U?t=1775s), convertendo um servidor Express de JS para TS, passo a passo.

A parte prática mostra como fazer arquivos `.ts` conviverem com `.js` numa aplicação Node/Express real. A extensão do arquivo é o que define se ele é tratado como JavaScript (`.js`) ou TypeScript (`.ts`).

**1. `import type` para tipos.** Ao importar `Request` e `Response` do Express só para usá-los como tipos, o TS pede que sejam importados como tipo:

```typescript
import type { Request, Response } from "express";

export function getAllUsers(req: Request, res: Response): void {
  res.json([/* ... */]); // o editor já conhece res.json, res.status, etc.
}
```

Com os tipos do Express instalados, ao digitar `res.` o editor mostra todas as funções disponíveis (`json`, `status`...). Se você escrever `res.jeson(...)` por engano, o TS já acusa: *Property 'jeson' does not exist on type 'Response'. Did you mean 'json'?*, em JavaScript puro esse erro só apareceria em tempo de execução (`res.jeson is not a function`).

**2. Rodando Node com TypeScript.** O Node não executa `.ts` direto (dá *Unknown file extension*). É preciso o **ts-node** combinado com o **nodemon**:

```bash
npm install --save-dev ts-node
```

Ajustando o script `dev` no `package.json` (projeto com ES Modules):

```jsonc
{
  "scripts": {
    "dev": "nodemon --exec \"node --loader ts-node/esm\" server.ts"
  }
}
```

A flag `--loader ts-node/esm` faz o TS ser transpilado para JS **antes** da execução (necessário quando o arquivo de entrada importa outros módulos). O `--esm` é usado porque a aplicação trabalha com ES Modules.

**3. Permitir convivência JS + TS.** Para um arquivo importar/conviver com `.js` e `.ts`, habilite no `tsconfig.json`:

```jsonc
{
  "compilerOptions": {
    "allowJs": true
  }
}
```

**4. Instale os `@types` que faltarem.** Ao converter mais arquivos, o TS aponta bibliotecas sem tipos (por exemplo `cors`, `express`):

```bash
npm install --save-dev @types/express @types/cors
```

Depois de tudo configurado, o servidor roda com os arquivos TypeScript funcionando em conjunto com o JavaScript, com os tipos do Express corretamente inferidos (a função `express()` já retorna um tipo `Express` tipado).

## Resumo

- TypeScript é um **superset** do JavaScript: JS + **tipos** verificados em **tempo de compilação** pelo compilador **TSC**.
- JS tem **tipagem dinâmica** (tipo muda em runtime); TS tem **tipagem estática** (tipo fixo, checado antes de rodar). Todo TS compilado vira JS, e todo JS é um TS válido.
- Instale com `--save-dev`, gere o `tsconfig.json` com `tsc --init` e deixe `strict: true`.
- Configurações-chave: `module`, `target`, `outDir`, `rootDir`, `esModuleInterop`, `allowJs` e `exclude` (no nível raiz).
- Instale os tipos das bibliotecas via pacotes `@types` (ex.: `@types/node`, `@types/express`).
- Tipos primitivos: `number`, `string`, `boolean`, `void`. Use **inferência** quando possível e anote parâmetros de função.
- **Union types** (`|`) unem tipos e servem para enumerar valores (strings literais).
- Para rodar TS junto com JS no Node, use **ts-node + nodemon** e habilite `allowJs`. A extensão (`.ts`/`.js`) define como o arquivo é tratado.
</content>
</invoke>
